package fr.upmc.components.registry.distributedRegistry.centralRegistry;

import fr.upmc.components.cvm.config.ConfigurationFileParser;
import fr.upmc.components.cvm.config.ConfigurationParameters;
import fr.upmc.components.registry.distributedRegistry.KeysCoverage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class <code>GRegistry</code> implements a registry used to referenced the distributed
 * registries with the coverage of keys.<br/>
 * <p>
 * The registry must be run on one host which name is given in the static variable
 * <code>HOSTNAME</code>. It is started with the application by the first distributed registry.It
 * listens to request on a port which number is given by the static variable <code>PORT</code><br/>
 * <p>
 * Distributed registries will connect to this registry to :
 * <ul>
 * <li>Search for another distributed registry according to its coverage of keys;</li>
 * <li>Get a free host were start a new distributed registry;</li>
 * <li>Registrer himself to this registry after have been created.</li>
 * </ul>
 * Communication between this registry and distributed registry is done according to this prtocol:
 * <p>
 * Requests Responses
 * <p>
 * seekHost <keys coverage> ok <hostname> nok seekKey <key> ok <hostname> <keys coverage> nok
 * register <key coverage> ok nok
 * <p>
 * The registry is started with the application by the first distributed registry.
 */
public class CentralRegistry extends Thread {

	/**
	 * Registry port number listen of commands.
	 */
	public static int PORT = 55353;

	/**
	 * If true, an echo of some commands executed is provided on STDOUT.
	 */
	public static final boolean DEBUG0 = true;

	/**
	 * If true, a log of the commands executed is provided on STDOUT.
	 */
	public static final boolean DEBUG1 = false;

	/**
	 * This list stores a reference of the distributed registry which are running.
	 */
	protected List<DistributedRegistryRef> directory;

	/**
	 * Server socket used to listen on <code>PORT</code>
	 */
	protected ServerSocket ss;

	/**
	 * Configuration parameters from the configuration file.
	 */
	protected ConfigurationParameters config;

	/**
	 * Number of host defined in configuration parameters.
	 */
	protected final int hosts_count;

	/**
	 * The executor service in charge of handling component requests.
	 */
	protected static ExecutorService REQUEST_HANDLER;

	/**
	 * Structure representing the state of a distributed registry. This registry contains a list of
	 * this structure for each differents hosts defined in configuration files.
	 */
	protected static class DistributedRegistryRef {

		/**
		 * Host name of the distributed registry
		 */
		protected String hostname;

		/**
		 * Coverage of key of the distributed reigstry
		 */
		protected KeysCoverage keysCoverage;

		/**
		 * Is true if the distributed registry is registred with the registry.
		 */
		protected boolean isLinked;

		public DistributedRegistryRef(String hostname) {
			this.hostname = hostname;
			this.isLinked = false;
		}

		/**
		 * Return true if this distributed registry has been reserved by another distributed
		 * registry which is in the process of divided. Later this distributed registry will be
		 * registered with this registry.
		 *
		 * @return
		 */
		public boolean isReserved() {
			return keysCoverage != null && isLinked == false;
		}

		public boolean isLinked() {
			return isLinked;
		}
	}

	/**
	 * Simple constructor creating a regitry according to the config file passed as an argument.
	 *
	 * @param configFileName
	 * @throws Exception
	 */
	public CentralRegistry(String configFileName) throws Exception {
		// Parse config file
		File configFile = new File(configFileName);
		ConfigurationFileParser cfp = new ConfigurationFileParser();
		if (!cfp.validateConfigurationFile(configFile)) {
			throw new Exception("invalid configuration file " + configFileName);
		}

		config = cfp.parseConfigurationFile(configFile);

		// Retrieve all differents hostnames from configuration file
		// TODO Ugly hack => make a function in parser to retrieve this list
		Set<String> hostnames = new HashSet<String>(config.getJvms2hosts().values());
		hostnames.add(config.getCodebaseHostname());
		hostnames.add(config.getGlobalRegistryHostname());
		hostnames.add(config.getCyclicBarrierHostname());
		hostnames.remove(null);

		hosts_count = hostnames.size();

		// Creates the directory with the list of hostnames defined in configuration files
		directory = new ArrayList<DistributedRegistryRef>(hosts_count);
		for (String hostname : hostnames) {
			DistributedRegistryRef ref = new DistributedRegistryRef(hostname);
			if (ref.hostname.equals(config.getGlobalRegistryHostname())) {
				ref.keysCoverage = new KeysCoverage("a", "z");
				ref.isLinked = true;
			}

			directory.add(ref);
		}

		REQUEST_HANDLER = Executors.newFixedThreadPool(hosts_count);

		try {
			ss = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function is used to display the directory of the registry. Will be used if DEBUG1 is
	 * enabled.
	 */
	public void displayDirectory() {
		System.out.println("Registry directory :");
		for (DistributedRegistryRef s : directory) {
			System.out.println("=> " + s.hostname + " ; " + s.keysCoverage + " ; " + s.isLinked);
		}
		System.out.println();
	}

	@Override
	public void run() {
		Socket s = null;
		if (DEBUG1) {
			System.out.println("Registry up and running!");
		}

		int count = 0;
		while (count < hosts_count) {
			try {
				REQUEST_HANDLER.submit(new ServiceRunnable(ss.accept(), this, count));
				count++;
				if (DEBUG1) {
					System.out.println("Global registry accepted a new connection.");
				}
			} catch (IOException e) {
				try {
					if (s != null) {
						s.close();
					}
					ss.close();
				} catch (IOException e1) {
				}
				e.printStackTrace();
			}
		}
		if (DEBUG0) {
			System.out.println("All connected!");
		}
		try {
			this.ss.close();
		} catch (IOException e) {
		}
	}

	/**
	 * The class <code>ServiceRunnable</code> implements the behaviour of the registry exchanging
	 * with one client; its processes the requests from the clients until the latter explicitly
	 * disconnects with a "shutdown" request of implicitly with a null string request.
	 */
	protected static class ServiceRunnable implements Runnable {

		protected CentralRegistry reg;
		protected Socket s;
		protected BufferedReader br;
		protected PrintStream ps;
		protected CountDownLatch finished;
		protected int distributedRegId;

		public ServiceRunnable(Socket socket, CentralRegistry globalReg, int id) {
			if (DEBUG1) {
				System.out.println("Global registry creating a service runnable");
			}
			reg = globalReg;
			s = socket;
			distributedRegId = id;

			try {
				this.br = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
				this.ps = new PrintStream(s.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
				if (DEBUG1) {
					System.out.println("...service runnable created");
				}
			}
		}

		@Override
		public void run() {
			if (DEBUG1) {
				System.out.println("Service runnable running...");
			}
			String message = null;
			try {
				message = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (DEBUG0) {
				System.out.println("GlobalRegistry processing " + message);
			}

			String[] tokens = message.split("\\s");

			while (message != null && !message.equals("shutdown")) {
				if (tokens[0].equals("register")) {
					new ProcessRegister(reg, ps, tokens[1]).run();
				} else if (tokens[0].equals("seekKey")) {
					new ProcessSeekKey(reg, ps, tokens[1]).run();
				} else if (tokens[0].equals("seekHost")) {
					new ProcessSeekHost(reg, ps, tokens[1]).run();
				} else {
					ps.println("nok unkonwn_command!");
				}

				try {
					message = br.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (DEBUG0) {
					System.out.println("GlobalRegistry processing " + message);
				}
				if (message != null) {
					tokens = message.split("\\s");
					if (DEBUG1) {
						System.out.println("GlobalRegistry next command " + tokens[0] + " "
										+ (!tokens[0].equals("shutdown")));
					}
				}
			}

			try {
				this.ps.print("ok");
				this.ps.close();
				this.br.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (DEBUG1) {
				System.out.println("Registry exits ");
			}
			this.finished.countDown();
		}
	}

	/**
	 * The class <code>ProcessRegister</code> implements a runnable task used by the client of a
	 * distributed registry newly created to confirm its creation to this registry.
	 */
	protected static class ProcessRegister implements Runnable {

		protected PrintStream ps;
		protected CentralRegistry reg;
		protected String keysCoverage;

		public ProcessRegister(CentralRegistry reg, PrintStream ps, String keysCoverage) {
			super();
			this.reg = reg;
			this.ps = ps;
			this.keysCoverage = keysCoverage;
		}

		@Override
		public void run() {
			System.out.print("__GR Register " + keysCoverage + " return ");

			boolean ok = false;

			synchronized (reg.directory) {
				// Update the new coverage of the registry which was divided
				for (DistributedRegistryRef reg : reg.directory) {
					if (reg.isLinked() && reg.keysCoverage.isIncluded(keysCoverage)) {
						String to = new KeysCoverage(keysCoverage).getFrom();
						to = to.substring(0, to.length() - 1);
						reg.keysCoverage.setTo(to);
						break;
					}
				}

				// Set up the coverage of the new registry
				for (DistributedRegistryRef reg : reg.directory) {
					if (reg.keysCoverage != null && reg.keysCoverage.toString().equals(keysCoverage)) {
						reg.isLinked = true;
						ok = true;
						break;
					}
				}
			}
			System.out.println((ok) ? "ok" : "nok");
			ps.println((ok) ? "ok" : "nok");
			reg.displayDirectory();
		}
	}

	/**
	 * The class <code>ProcessSeekKey</code> implements a runnable task used by a client to find the
	 * distributed registry which should contains a given key.
	 */
	protected static class ProcessSeekKey implements Runnable {

		protected CentralRegistry reg;
		protected PrintStream ps;
		protected String key;

		public ProcessSeekKey(CentralRegistry reg, PrintStream ps, String key) {
			super();
			this.reg = reg;
			this.ps = ps;
			this.key = key;
		}

		@Override
		public void run() {
			boolean ok = false;

			synchronized (reg.directory) {
				for (DistributedRegistryRef reg : reg.directory) {
					if (reg.keysCoverage.isIncluded(key)) {
						if (DEBUG1)
							System.out.println("ok " + reg.hostname + " " + reg.keysCoverage);
						ps.println("ok " + reg.hostname + " " + reg.keysCoverage);
						ok = true;
						break;
					}
				}
			}
			if (!ok) {
				ps.println("nok");
			}
			reg.displayDirectory();
			return;
		}
	}

	/**
	 * The class <code>ProcessSeekHost</code> implements a runnable task used by a client to find a
	 * free hostname where created a new distributed registry.
	 */
	protected static class ProcessSeekHost implements Runnable {

		protected CentralRegistry reg;
		protected PrintStream ps;
		protected String keysCoverage;

		public ProcessSeekHost(CentralRegistry reg, PrintStream ps, String keysCoverage) {
			super();
			this.reg = reg;
			this.ps = ps;
			this.keysCoverage = keysCoverage;
		}

		@Override
		public void run() {
			boolean ok = false;

			synchronized (reg.directory) {
				for (DistributedRegistryRef reg : reg.directory) {
					if (!reg.isReserved() && !reg.isLinked()) {
						ps.println("ok " + reg.hostname);
						reg.keysCoverage = new KeysCoverage(keysCoverage);
						ok = true;
						break;
					}
				}
			}
			if (!ok) {
				if (DEBUG1)
					System.out.println("There is no more available host where created a distributed registry.");
				ps.println("nok");
			}
			reg.displayDirectory();
		}
	}

	/**
	 * Initialise and run the central registry
	 */
	public static void main(String[] args) {
		try {
			CentralRegistry reg = new CentralRegistry("config.xml");
			reg.displayDirectory();
			reg.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

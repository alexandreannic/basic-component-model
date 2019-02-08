package fr.upmc.components.registry.distributedRegistry;

import fr.upmc.components.registry.GlobalRegistry;
import fr.upmc.components.registry.distributedRegistry.centralRegistry.CentralRegistry;
import fr.upmc.components.registry.distributedRegistry.centralRegistry.CentralRegistryClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * The class <code>DistributedRegistry</code> implements an instance of a distributed registry for
 * the component model that registers connection information to remotely access components through
 * their ports. Several instance of this registry will spread up on the available host of the
 * application if needed.
 * <p>
 * Protocol (spaces are used to split the strings, so they are meaningful):
 * <p>
 * Requests Responses
 * <p>
 * lookup <key> ok <value> sync <keys coverage> hostname <keys coverage> nok put <key> <value> ok
 * sync <keys coverage> hostname <keys coverage> nok bound! remove <key> ok sync <keys coverage>
 * hostname <keys coverage> nok not_bound! shutdown ok <anything else> nok unkonwn_command!
 * <p>
 * <p>
 * Statics variables DEBUG0 and DEBUG1 are used to provides a log of the registry's action on STDOUT
 */
public class DistributedRegistry extends GlobalRegistry {

	/**
	 * Classpath of the distributed registry. Used to create a new distributed registry by this
	 * distributed registry
	 */
	public static final String CLASS_PATH = "fr.upmc.components.registry.distributedRegistry.DistributedRegistry";

	/**
	 * dcvm.policy
	 */
	public static final String POLICIES_FILE = "policies/dcvm.policy";

	/**
	 * Name of the configuration file
	 */
	public static String CONFIG_FILE_NAME = "config.xml";

	/**
	 * When the directory of a distributed registry reach this size, he will try to split himself
	 */
	public static int DIRECTORY_SIZE = 100;

	/**
	 * Coverage of key of the distributed registry
	 */
	protected KeysCoverage keysCoverage;

	/**
	 * Stores some others registries and their coverage of keys. Used to remind answere from central
	 * registry and avoid to ask again. TODO not yet implemented
	 */
	protected Map<String, KeysCoverage> registriesKnown;

	/**
	 * Client to communicate with the central registry
	 */
	protected CentralRegistryClient askGlobalRegistry;

	/**
	 * Will be set to false when the global registry respond that there is no more available host to
	 * store a new distributed registry.
	 */
	protected boolean canBeDivided;

	/**
	 * Number of clients already connected
	 */
	protected int connectedClient_count;

	public DistributedRegistry(String configFileName, String coverage) throws Exception {
		super(configFileName);

		CONFIG_FILE_NAME = configFileName;
		DIRECTORY_SIZE = config.getDistributedRegistrySize();
		HOSTNAME = config.getGlobalRegistryHostname();

		keysCoverage = new KeysCoverage(coverage);
		registriesKnown = new HashMap<String, KeysCoverage>();
		askGlobalRegistry = new CentralRegistryClient();

		// If this registry is not the first, register it to the central registry
		if (!keysCoverage.toString().equals("a-z")) {
			askGlobalRegistry.register(keysCoverage.toString());
		}

		canBeDivided = true;
	}

	/**
	 * This constructor should be called only once to create the first distributed registry started
	 * with the application.
	 *
	 * @param configFileName
	 * @throws Exception
	 */
	public DistributedRegistry(String configFileName) throws Exception {
		this(configFileName, "a-z");

		// Start the central registry on the port defined in configuration file.
		CentralRegistry.PORT = config.getCentralRegistryPort();
		new CentralRegistry(configFileName).start();
	}

	public void run() {
		Socket s = null;
		if (DEBUG0) {
			System.out.println("Registry up and running!");
		}
		connectedClient_count = 0;

		// TODO Hack, we cannot expect only composant because the distributed registry which created
		// this one will connect to transfere half of his keys.
		while (connectedClient_count < jvmInDCVM_count + 1) {
			try {
				REQUEST_HANDLER.submit(new ServiceRunnable(ss.accept(), this));
				connectedClient_count++;
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

	// _____________________________________________________________________________________

	/**
	 * The class <code>ServiceRunnable</code> implements the behaviour of the registry exchanging
	 * with one client; its processes the requests from the clients until the latter explicitly
	 * disconnects with a "shutdown" request of implicitly with a null string request.
	 */
	protected static class ServiceRunnable implements Runnable {

		protected DistributedRegistry reg;
		protected Socket s;
		protected BufferedReader br;
		protected PrintStream ps;
		protected CountDownLatch finished;

		public ServiceRunnable(Socket socket, DistributedRegistry globalRegistry) {
			if (DEBUG1) {
				System.out.println("Distributed registry creating a service runnable");
			}
			s = socket;
			reg = globalRegistry;
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

		/**
		 * This function is called when return a message containing the new coverage of this
		 * distributed registry, the
		 *
		 * @param key
		 */
		public void sendRegistryCovering(String key) {
			try {
				String result = reg.askGlobalRegistry.seekKey(key);
				String tokens[] = result.split(" ");
				String new_hostname = tokens[0];
				String new_coverage = tokens[1];

				ps.println("sync " + reg.keysCoverage + " " + new_hostname + " " + new_coverage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void put(String key, String value) {
			if (reg.keysCoverage.isIncluded(key)) {
				new ProcessPut(ps, key, value, reg.directory).run();
			} else {
				sendRegistryCovering(key);
			}
			if (reg.canBeDivided && reg.directory.size() >= DistributedRegistry.DIRECTORY_SIZE) {
				new ProcessDivide(ps, reg).run();
			}
		}

		public void lookup(String key) {
			if (reg.keysCoverage.isIncluded(key)) {
				new ProcessLookup(ps, key, reg.directory).run();
			} else {
				sendRegistryCovering(key);
			}
		}

		public void remove(String key) {
			if (reg.keysCoverage.isIncluded(key)) {
				new ProcessRemove(ps, key, reg.directory).run();
			} else {
				sendRegistryCovering(key);
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
				System.out.println("Distributed registry processing " + message);
			}

			String[] tokens = message.split("\\s");

			while (message != null && !tokens[0].equals("shutdown")) {
				if (tokens[0].equals("lookup")) {
					lookup(tokens[1]);
				} else if (tokens[0].equals("put")) {
					put(tokens[1], tokens[2]);
				} else if (tokens[0].equals("remove")) {
					remove(tokens[1]);
				} else {
					ps.println("nok unkonwn_command!");
				}

				try {
					message = br.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				if (DEBUG0) {
					System.out.println("Distributed registry processing " + message);
				}
				if (message != null) {
					tokens = message.split("\\s");
					if (DEBUG1) {
						System.out.println("GlobalRegistry next command " + tokens[0] + " "
										+ (!tokens[0].equals("shutdown")));
					}
				}
			}

			// FIXME This is useful when another register transfer his keys and disconnected because
			// the server waits for a specific number of connection. This is absolutly not robust
			// because all
			// clients will not be able to connect if the transferred key is not completed.
			reg.connectedClient_count--;

			try {
				this.ps.print("ok");
				this.ps.close();
				this.br.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (DEBUG1) {
				System.out.println("GlobalRegistry exits ");
			}
			this.finished.countDown();
		}

		/**
		 *
		 *
		 */
		protected static class ProcessDivide implements Runnable {

			// TODO delete
			public static String TODODELETE = "/opt/java/32/jre1.8/bin/";
			protected PrintStream ps;
			protected DistributedRegistry reg;

			public ProcessDivide(PrintStream ps, DistributedRegistry reg) {
				super();
				this.ps = ps;
				this.reg = reg;
			}

			/**
			 * This thread is only used to display the output of the new distributed registry.
			 */
			protected static class NewDistributedRegistryProcess extends Thread {

				String[] commande;
				String hostname;

				public NewDistributedRegistryProcess(String[] commande, String hostname) {
					this.commande = commande;
					this.hostname = hostname;
				}

				public void run() {
					try {
						Process p = Runtime.getRuntime().exec(commande);
						BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
						BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String ligne = "";

						while ((ligne = output.readLine()) != null) {
							System.out.println("<DistributedRegistry " + hostname + "> : " + ligne);
						}
						while ((ligne = error.readLine()) != null) {
							System.out.println("<DistributedRegistry " + hostname + "> : " + ligne);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void run() {
				String uname = reg.config.getUname();
				String sourcesLoc = reg.config.getSourcesLocation();
				String hostname = null;

				synchronized (reg.directory) {
					// Define coverage for the two registrys
					String thisRegistry_to = KeysCoverage.findTheCut(new ArrayList<String>(reg.directory.keySet()));
					String newRegistry_from = KeysCoverage.nextBoundary(thisRegistry_to);
					String newRegistry_coverage = newRegistry_from + "-" + reg.keysCoverage.getTo();

					try {
						hostname = reg.askGlobalRegistry.seekHost(newRegistry_coverage);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					if (hostname.equals("nok")) {
						reg.canBeDivided = false;
						return;
					}
					reg.keysCoverage = new KeysCoverage(reg.keysCoverage.getFrom(), thisRegistry_to);

					// TODO Delete
					// if (hostname.endsWith("21"))
					// TODODELETE = "";
					// else if (hostname.endsWith("26")) {
					// TODODELETE = "/opt/java/32/jre1.8/bin/";
					// }

					// Transfers half of the keys on the new distributed registry
					if (DEBUG0) {
						System.out.println("Distributed registry is processing a split on " + hostname + ".");
					}
					String[] commande = {
									"ssh",
									uname + "@" + hostname,
									"cd " + sourcesLoc + " && " + TODODELETE + "java -cp '.:jars/jing.jar' "
													+ "-Djava.security.manager " + "-Djava.security.policy=" + POLICIES_FILE + " "
													+ CLASS_PATH + " " + CONFIG_FILE_NAME + " " + newRegistry_coverage };

					new NewDistributedRegistryProcess(commande, hostname).start();

					// Split the directory of information.
					DistributedRegistryClient client = new DistributedRegistryClient(hostname);

					Iterator<String> it = reg.directory.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next();
						if (key.compareToIgnoreCase(thisRegistry_to) > 0) {
							try {
								client.put(key, reg.directory.get(key));
								it.remove();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					try {
						client.shutdown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * The class <code>ProcessLookup</code> implements a runnable task used to look up the
		 * registry for a given key.
		 */
		protected static class ProcessLookup implements Runnable {

			protected PrintStream ps;
			protected String key;
			protected Hashtable<String, String> directory;

			public ProcessLookup(PrintStream ps, String key, Hashtable<String, String> directory) {
				super();
				this.ps = ps;
				this.key = key;
				this.directory = directory;
			}

			@Override
			public void run() {
				String result = null;
				synchronized (this.directory) {
					result = this.directory.get(this.key);
				}
				if (result == null) {
					this.ps.println("nok");
				} else {
					if (DEBUG1) {
						System.out.println("Global registry looking up " + this.key + " found " + result);
					}
					this.ps.println("ok " + result);
				}
			}
		}

		/**
		 * The class <code>ProcessPut</code> implements a runnable task used to update the registry
		 * with the association of a given key to a given value.
		 */
		protected static class ProcessPut implements Runnable {

			protected PrintStream ps;
			protected String key;
			protected String value;
			protected Hashtable<String, String> directory;

			public ProcessPut(PrintStream ps, String key, String value, Hashtable<String, String> directory) {
				super();
				this.ps = ps;
				this.key = key;
				this.value = value;
				this.directory = directory;
			}

			@Override
			public void run() {
				String result = null;
				synchronized (this.directory) {
					result = this.directory.get(key);
				}
				if (result != null) {
					ps.println("nok bound!");
				} else {
					this.directory.put(this.key, this.value);
					ps.println("ok");
				}
			}
		}

		/**
		 * The class <code>ProcessRemove</code> implements a runnable task used to remove the
		 * association of a given key from the registry.
		 */
		protected static class ProcessRemove implements Runnable {

			protected PrintStream ps;
			protected String key;
			protected Hashtable<String, String> directory;

			public ProcessRemove(PrintStream ps, String key, Hashtable<String, String> directory) {
				super();
				this.ps = ps;
				this.key = key;
				this.directory = directory;
			}

			@Override
			public void run() {
				String result = null;
				synchronized (this.directory) {
					result = this.directory.get(key);
				}
				if (result == null) {
					ps.println("nok not_bound!");
				} else {
					this.directory.remove(this.key);
					ps.println("ok");
				}
			}
		}
	}

	/**
	 * Initialise and run a distributed registry
	 */
	public static void main(String[] args) {
		DistributedRegistry reg;
		try {
			if (args.length == 1) {
				reg = new DistributedRegistry(args[0]);
			} else {
				reg = new DistributedRegistry(args[0], args[1]);
			}
			reg.run();
			reg.finished.await();
			if (DEBUG0) {
				System.out.println("Global registry shuts down!");
			}
			REQUEST_HANDLER.shutdownNow();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

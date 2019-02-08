package fr.upmc.components.cvm.utils;

// Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
// 
// Jacques.Malenfant@lip6.fr
// 
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
// 
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
// 
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
// 
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
// 
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.upmc.components.cvm.config.ConfigurationFileParser;
import fr.upmc.components.cvm.config.ConfigurationParameters;

/**
 * The class <code>DCVMCyclicBarrier</code> implements a synchronisation
 * mechanism, known as a cyclic barrier, in a distributed way, and to be used
 * by DCVM to synchronise at important steps in the deployment, execution
 * and shutting down of distributed component-based applications.
 *
 * <p><strong>Description</strong></p>
 * 
 * The basic idea of the implementation is to map external processes to threads
 * in the virtual machine running the distributed cyclic barrier, and to use a
 * standard <code>java.util.concurrent.CyclicBarrier</code> to synchronise
 * these.  To couple the internal threads with the external processes, the
 * implementation uses sockets and their property of putting threads reading
 * from them into a wait.  First the cyclic barrier waits on a server socket for
 * the processes to connect, and create a thread and a socket for communicating
 * with each of them.  Each time processes need to synchronise, they send their
 * identification and a hostname and a port number on which they are waiting for
 * a release message.  The threads in the cyclic barrier receive these calls and
 * register the hostname and port number in a table and then synchronise on the
 * local cyclic barrier.  When released from the local cyclic barrier, the
 * release message is written on the sockets corresponding to each of the
 * external processes, and the cycle starts again.
 * 
 * TODO: currently, the shutting down of the cyclic barrier depends upon the
 *       fact that when a process stops, the sockets is closed and the cyclic
 *       barrier receives a null string as ultimate message.  A cleaner
 *       explicit shutting down mechanism should be implemented.
 * 
 * TODO: the mechanism should be better abstracted by defining a wait barrier
 *       client which would hide the use of sockets from the processes to be
 *       synchronised.
 * 
 * <p><strong>Usage</strong></p>
 * 
 * The class <code>DCVMCyclicBarrier</code> is started in its own JVM,
 * passing it the name of the configuration file as command line argument.
 * The configuration file must contain the hostname and the port number on
 * which the cyclic barrier will be waiting for the first connections from
 * processes.  Processes simply call the method <code>waitBarrier</code>
 * defined in the class <code>AbstractDistributedCVM</code> to synchronise
 * with each others.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true	// TODO
 * </pre>
 * 
 * <p>Created on : 2012-12-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DCVMCyclicBarrier
{
	/**
	 * The class <code>HostPortSocket</code> defines a tuple of three values
	 * (host name, port number and socket on which this host waits for a
	 * release signal) that must be kept when a host has sent a request for
	 * synchronisation to the cyclic barrier.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true	// TODO
	 * </pre>
	 * 
	 * <p>Created on : 17 janv. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	protected static class	HostPortSocket {
		public HostPortSocket(String name, int port, Socket s) {
			super();
			this.name = name;
			this.port = port;
			this.s = s ;
		}
		public String	name ;
		public int		port ;
		public Socket	s ;
	}

	/**	Default cyclic barrier host name, configurable through the configuration file.	*/
	public static String	CYCLIC_BARRIER_HOSTNAME = "bonaventure-ec2.rsr.lip6.fr" ;
	/**	port number used by the cyclic barrier to listen to processes.		*/
	public static int		CYCLIC_BARRIER_PORT = 55253 ;
	// TODO: make this no longer a limit but simply a default.
	/**	Max number of processes to be synchronised.							*/
	public static int		MAX_NUMBER_OF_THREADS = 100 ;

	public static boolean	DEBUG0 = true ;
	public static boolean	DEBUG1 = true ;

	/** Configuration parameters from the configuration file.				*/
	protected ConfigurationParameters		configurationParameters ;
	/**	Assemblies waiting for a release signal.								*/
	protected final Hashtable<String,HostPortSocket>	awaitingSites ;
	/** Server socket waiting for calls from assemblies.					*/
	protected ServerSocket					ss ;
	/** Number of JVM in the current distributed assembly.					*/
	protected final int						numberOfJVMsInDCVM ;
	/** The executor service in charge of handling component requests.		*/
	protected static ExecutorService		REQUEST_HANDLER ;
	/** local cyclic barrier used to synchronise the assembly processing threads.	*/ 
	protected CyclicBarrier					localCyclicBarrier ;
	/**	synchroniser to finish the execution of this cyclic wait barrier.	*/
	protected CountDownLatch				finished ;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	/**
	 * create a distributed cyclic barrier for a given assembly, as described
	 * by its configuration file.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 * @throws Exception 
	 *
	 */
	public				DCVMCyclicBarrier(String configFileName)
	throws Exception
	{
		super();
		File configFile = new File(configFileName) ;
		ConfigurationFileParser cfp = new ConfigurationFileParser() ;
		if (!cfp.validateConfigurationFile(configFile)) {
			throw new Exception("invalid configuration file " + configFileName) ;
		}
		this.configurationParameters = cfp.parseConfigurationFile(configFile) ;

		this.numberOfJVMsInDCVM =
							this.configurationParameters.getJvms().length ;
		this.awaitingSites =
			new Hashtable<String,HostPortSocket>(
									(int) (1.5*this.numberOfJVMsInDCVM)) ;
		DCVMCyclicBarrier.CYCLIC_BARRIER_HOSTNAME =
					this.configurationParameters.getCyclicBarrierHostname() ;
		DCVMCyclicBarrier.CYCLIC_BARRIER_PORT =
					this.configurationParameters.getCyclicBarrierPort() ;
		this.ss = new ServerSocket(DCVMCyclicBarrier.CYCLIC_BARRIER_PORT) ;
		REQUEST_HANDLER = Executors.newFixedThreadPool(MAX_NUMBER_OF_THREADS) ;
		this.finished = new CountDownLatch(this.numberOfJVMsInDCVM) ;
		this.localCyclicBarrier =
			new CyclicBarrier(
				this.numberOfJVMsInDCVM,
				new Runnable() {
					@Override
					public void run() {
						synchronized (awaitingSites) {
							if (DEBUG0) {
								System.out.println(
									"DCVM Cyclic Barrier releases "
									+ awaitingSites.size() +
									" sites.") ;
							}
							for(HostPortSocket p : awaitingSites.values()) {
								PrintStream ps;
								try {
									ps = new PrintStream(
											p.s.getOutputStream(),
											true);
									ps.println("resume") ;
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							awaitingSites.clear() ;
						}
					}
			}) ;
	}

	// ------------------------------------------------------------------------
	// Service runnable
	// ------------------------------------------------------------------------

	/**
	 * The class <code>ServiceRunnable</code> implements the barrier as a
	 * <code>Runnable</code> that receives the synchronisation requests from
	 * assemblies, store them awaiting, and sends all of the stored assemblies
	 * the release signal when all of them have sent their request.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * The implementation relies on sockets and their waiting for reading
	 * capability to force the waiting and then release of the processes to
	 * be synchronised.
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true	// TODO
	 * </pre>
	 * 
	 * <p>Created on : 17 janv. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	protected static class	ServiceRunnable
	implements	Runnable
	{
		protected Hashtable<String,HostPortSocket>	awaitingSites ;
		protected int								numberOfJVMsInDCVM ;
		protected Socket							s ;
		protected BufferedReader					br ;
		protected CyclicBarrier						localCyclicBarrier ;
		protected CountDownLatch					finished ;

		public				ServiceRunnable(
			Hashtable<String, HostPortSocket> awaitingSites,
			int numberOfJVMsInDCVM,
			Socket s,
			CyclicBarrier localCyclicBarrier,
			CountDownLatch finished
			)
		{
			super();
			this.awaitingSites = awaitingSites;
			this.numberOfJVMsInDCVM = numberOfJVMsInDCVM;
			this.s = s ;
			try {
				this.br = new BufferedReader(
						  	  new InputStreamReader(this.s.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.localCyclicBarrier = localCyclicBarrier ;
			this.finished = finished ;
		}

		/**
		 * for each external process to be synchronised, a copy of this method
		 * is the behaviour of the associated thread in the distributed cyclic
		 * barrier, which repeatedly read the external process info written on
		 * its socket, put the information in the hash table of awaiting
		 * processes and wait on the local cyclic barrier to be released; this
		 * is repeated until a null is returned from the read on socket, marking
		 * the disconnection from the barrier by the external process.
		 *  
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	true				// no more preconditions.
		 * post	true				// no more postconditions.
		 * </pre>
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void			run()
		{
			String message = null ;
			try {
				message = br.readLine() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (message != null) {
				if (DEBUG0) {
					System.out.println("DCVM Cyclic Barrier accepts: "
																	+ message) ;
				}
				String[] tokens = message.split("\\s") ;
				synchronized (this.awaitingSites) {
					// TODO: verify that the host name is known from the
					// configuration file...
					if (!this.awaitingSites.containsKey(tokens[0])) {	
						this.awaitingSites.put(
								tokens[0],
								new HostPortSocket(
										tokens[1],
										Integer.parseInt(tokens[2]),
										this.s)) ; 
					} else {
						if (DEBUG1) {
							System.out.println(
								"DCVM Cyclic Barrier warning: " +
								"jvm already registered " + tokens[0]);
						}
					}
					if (DEBUG1) {
						System.out.println(
							"DCVM Cyclic Barrier has received " +
							this.awaitingSites.size() + " out of " +
							this.numberOfJVMsInDCVM + " expected.") ;
					}
				}
				try {
					this.localCyclicBarrier.await() ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				try {
					message = br.readLine() ;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				s.close() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (DEBUG1) {
				System.out.println("DCVMCyclicBarrier.run() finished.") ;
			}
			this.finished.countDown() ;
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * wait for processes for their first connection on a server socket and
	 * create a new <code>ServiceRunnable</code> for each of them, submitting
	 * its execution to the executor service; these threads will then
	 * synchronise with each other on a local standard cyclic barrier and
	 * alternatively wait and release their external process by exchanging
	 * messages through sockets.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	protected void		run()
	{
		if (DEBUG0) {
			System.out.println("DCVM Cyclic Barrier up and running!") ;
		}
		int connected = 0 ;
		while (connected < this.numberOfJVMsInDCVM) {
			try {
				REQUEST_HANDLER.submit(
					new ServiceRunnable(
							this.awaitingSites,
							this.numberOfJVMsInDCVM,
							this.ss.accept(),
							this.localCyclicBarrier,
							this.finished)) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
			connected++ ;
		}
		if (DEBUG0) {
			System.out.println("All connected!") ;
		}
		try {
			this.ss.close() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------
	// Main method
	// ------------------------------------------------------------------------

	/**
	 * starts the cyclic barrier.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	args.length == 1
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param args
	 */
	public static void	main(String[] args) {
		DCVMCyclicBarrier as;
		try {
			as = new DCVMCyclicBarrier(args[0]);
			as.run() ;
			as.finished.await() ;
			if (DEBUG1) {
				System.out.println("DCVM Cyclic Barrier shuts down!") ;
			}
			REQUEST_HANDLER.shutdown() ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

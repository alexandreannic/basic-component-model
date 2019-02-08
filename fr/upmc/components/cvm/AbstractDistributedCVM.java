package fr.upmc.components.cvm;

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

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;
import fr.upmc.components.cvm.config.ConfigurationFileParser;
import fr.upmc.components.cvm.config.ConfigurationParameters;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreator;
import fr.upmc.components.cvm.utils.DCVMCyclicBarrier;
import fr.upmc.components.cvm.utils.DCVMCyclicBarrierClient;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.ports.PortI;
import fr.upmc.components.registry.GlobalRegistry;
import fr.upmc.components.registry.GlobalRegistryClient;
import fr.upmc.components.registry.distributedRegistry.DistributedRegistryClient;

/**
 * The class <code>AbstractDistributedCVM</code> defines the common properties
 * of distributed component virtual machines in the component model.
 *
 * <p><strong>Description</strong></p>
 * 
 * DCVM are deployed on a set of Java virtual machines, themselves running over
 * a set of different hosts.  A deployment uses:
 * <ul>
 * <li>one RMI registry per host (limitation of the RMI registry provided by
 *   Oracle that entries may be set only on a registry that runs on the same
 *   host);</li>
 * <li>one JVM running the global registry that is mapping port URIs to the
 *   host on which RMI registry it is published;</li>
 * <li>one JVM running a  distributed cyclic barrier used to synchronise the
 *   deployment processes among the different JVM running components;</li>
 * <li>at least one, but more pragmatically more than two JVM running
 *   components which are given a URI (this URI is used in the deployment code
 *   to know which components must be created by the current JVM and which are
 *   created by other virtual machines.</li>
 * </ul>
 * 
 * The configuration file provides application-wide informations required on
 * each virtual machine to make the system work properly.  This file is giving
 * the necessary information to all of the different JVM, and to the tools like
 * the cyclic barrier used to synchronise the different JVM.  The Relax NG
 * schema of the configuration file is as follows:
 * 
 * <pre>
 * start = deployment
 * 
 * deployment = element deployment {
 *   codebase?,             # localisation of the code base of the application
 *   cyclicBarrier,         # configuration of the cyclic barrier
 *   globalRegistry,        # configuration of the global registry
 *   rmiRegistryPort,       # configuration of the RMI registry
 *   jvms2hostnames         # mapping from JVM to hosts running them
 * }
 * 
 * codebase = element codebase {
 *   attribute hostname  { text },  # host on which the code base may be found
 *   attribute directory { text },  # directory in which the code base may be found
 *   empty
 * }
 * 
 * cyclicBarrier = element cyclicBarrier {
 *   attribute hostname  { text },   # host on which the cyclic barrier is running
 *   attribute port      { xsd:int } # port number listen by the cyclic barrier
 * }
 * 
 * globalRegistry = element globalRegistry {
 *   attribute hostname  { text },   # host on which the global registry is running
 *   attribute port      { xsd:int } # port number listen by the global registry
 * }
 * 
 * rmiRegistryPort = element rmiRegistryPort {
 *   attribute no        { xsd:int }  # port number listen by the RMI registry
 * }
 * 
 * jvms2hostnames = element jvms2hostnames {
 *   jvm2hostname+
 * }
 * 
 * jvm2hostname = element jvm2hostname {
 *   attribute jvmuri { xsd:anyURI },      # JVM URI
 *                                         # is this JVM creating the RMI registry
 *   attribute rmiRegistryCreator { xsd:boolean },
 *   attribute hostname { text }           # name of the host running that JVM
 * }
 * </pre>
 * 
 * The DCVM object created from the user-defined subclass of this abstract class
 * is meant to be executed on every JVM running components used for the
 * deployment.  The configuration file and the code of the user must be written
 * in such a way that it identifies the static components to be instantiated
 * locally in order to create them and then publish on the RMI registry the
 * connection points of their inbound ports (the ones for the offered interfaces
 * of the components) so that the client components will be able to get the
 * information from the registry to do the connection.
 * 
 * In order to ensure the availability of the information and to avoid deadlocks
 * in this initialisation phase, all of the static creations and inbound
 * connection publications must be done before beginning to query the registry
 * to connect local components to distant ones.  The cyclic barrier serves as a
 * mean for synchronisation to this end.
 * 
 * Starting a component-based application entails:
 * <ol>
 * <li>Starting the global registry on the global registry host.</li>
 * <li>On each host, start the different JVM that must run on that host, paying
 *   attention to the fact that all of these JVM must be given the URI that is
 *   used in the deployment code to identify the components that must be
 *   created on that JVM.</li>
 * </ol>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true			// no invariant yet, TODO
 * </pre>
 * 
 * <p>Created on : 2012-05-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractDistributedCVM
extends		AbstractCVM
implements	DistributedComponentVirtualMachineI
{
	// ------------------------------------------------------------------------
	// Deployment information
	// ------------------------------------------------------------------------

	/** parameters obtained form the xml configuration file.			*/
	protected ConfigurationParameters	configurationParameters ;
	/** URI of the current JVM in the deployment platform.				*/
	public static String				thisJVMURI ;
	/** name of the host on which the JVM is running.					*/
	public static String				thisHostname ;
	/** name of the JVMs creating RMI registry.							*/
	public static Set<String>			rmiRegistryCreators ;
	/** name of the hosts holding RMI registry.							*/
	public static Set<String>			rmiRegistryHosts ;
	/** port number used for the RMI registry.							*/
	public static int					rmiRegistryPort ;
	/**	reference to the RMI registry.									*/
	public static Registry				theRMIRegistry ;

	// ------------------------------------------------------------------------
	// Registry management
	// ------------------------------------------------------------------------

	/** Global registry client; singleton.								*/
//	public static GlobalRegistryClient	GLOBAL_REGISTRY_CLIENT;
	public static DistributedRegistryClient GLOBAL_REGISTRY_CLIENT = new DistributedRegistryClient();

	/**
	 * publish inbound ports (data inbound ports and two way ports) both
	 * locally and globally, which includes the RMI registry and the global
	 * component registry; outbound ports need not be published globally
	 * but only locally (this information is used by the connection builder to
	 * decide how the connections to that port must be done).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	port != null
	 * post	true				// no more postconditions.
	 * </pre>
	 *
	 * @param port			port to be published
	 * @throws Exception
	 */
	public static void	publishPort(OfferedI port)
	throws Exception
	{
		assert	port != null ;
		//	TODO not already published in the global registry
		//	TODO not already published in the RMI registry of the current host
		//	!LOCAL_REGISTRY.containsKey(port.getPortURI())

		String portURI = ((PortI)port).getPortURI() ;
		if (AbstractCVM.DEBUG && !AbstractCVM.isDistributed) {
			System.out.println("locally publishing port " + portURI) ;
		} else if (AbstractCVM.DEBUG && !AbstractCVM.isDistributed) {
			System.out.println("distributedly publishing port " + portURI +
					" on the host " + AbstractDistributedCVM.thisHostname) ;
		}

		AbstractCVM.localPublishPort((PortI) port) ;
		if (AbstractCVM.isDistributed) {
			assert	AbstractDistributedCVM.theRMIRegistry != null ;
			AbstractDistributedCVM.theRMIRegistry.bind(portURI, port) ;
			AbstractDistributedCVM.GLOBAL_REGISTRY_CLIENT.
					put(portURI,
						"rmi=" + AbstractDistributedCVM.thisHostname) ;
		}

		//	LOCAL_REGISTRY.containsKey(port.getPortURI())
		//	port == LOCAL_REGISTRY.get(port.getPortURI())
		//	TODO published in the global registry
		//	TODO published in the RMI registry of the current host
	}

	/**
	 * unpublishes previously published inbound ports (data inbound ports and
	 * two way ports) both locally and globally, which includes the RMI
	 * registry and the global component registry; outbound ports need not be
	 * published globally but only locally.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	port != null
	 * post	true				// no more postconditions.
	 * </pre>
	 *
	 * @param port			port to be unpublished.
	 * @throws Exception
	 */
	public static void	unpublishPort(OfferedI port)
	throws Exception
	{
		assert	port != null ;
		//	TODO published in the global registry
		//	TODO published in the RMI registry of the current host
		//	LOCAL_REGISTRY.containsKey(port.getPortURI())
		//	port == LOCAL_REGISTRY.get(port.getPortURI())

		String portURI = ((PortI)port).getPortURI() ;

		if (AbstractCVM.DEBUG && !AbstractCVM.isDistributed) {
			System.out.println("locally unpublishing port " + portURI) ;
		} else if (AbstractCVM.DEBUG && AbstractCVM.isDistributed) {
			System.out.println("unpublishing distributed port " + portURI +
					" on the host " + AbstractDistributedCVM.thisHostname) ;
		}

		AbstractCVM.localUnpublishPort((PortI) port) ;
		if (AbstractCVM.isDistributed) {
			assert	AbstractDistributedCVM.theRMIRegistry != null ;
			AbstractDistributedCVM.theRMIRegistry.unbind(portURI) ;
			AbstractDistributedCVM.GLOBAL_REGISTRY_CLIENT.remove(portURI) ;
		}

		//	LOCAL_REGISTRY.containsKey(port.getPortURI())
		//	TODO not published in the global registry
		//	TODO not published in the RMI registry of the current host
	}

	// ------------------------------------------------------------------------
	// Internal dynamic state
	// ------------------------------------------------------------------------

	/** true if the initialisations of the CVM are done.					*/
	protected boolean			initialised ;

	/** true if the instantiations of the local components and the publications
	 * of their connection points on the RMI registry have been completed.	*/
	protected boolean			instantiationAndPublicationDone ;

	/** true if the interconnections of the client side connections of the
	 * local components to their corresponding server side connection points
	 * have been completed.													*/
	protected boolean			interconnectionDone ;

	/**	distributed implementation of a cyclic barrier for assemblies.		*/
	protected DCVMCyclicBarrierClient	cyclicBarrierClient ;

	/**
	 * instantiate the DCVM object.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * The constructor gets from the command line arguments the logical
	 * name of the current JVM in the assembly and the name of an XML
	 * configuration file giving a mapping between the URI of hosts to their
	 * IP addresses in the current deployment.  This JVM URI must be in the
	 * static array JVM_URIs and all of the hosts URI in the array HOSTS_URIs
	 * and only these ones must appear in the XML configuration file
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	args.length > 1
	 * post	true			// TODO
	 * </pre>
	 *
	 * @param args	command line arguments from the main method.
	 * @throws Exception 
	 */
	public				AbstractDistributedCVM(
		String[] args
		) throws Exception
	{
		super(true);
		// this line will only be executed if a DCVM is created, which means
		// that the CVM currently running is indeed distributed.
		// Otherwise, a local CVM will be used.
		AbstractDistributedCVM.thisJVMURI = args[0] ;
		File configFile = new File(args[1]) ;
		ConfigurationFileParser cfp = new ConfigurationFileParser() ;
		if (!cfp.validateConfigurationFile(configFile)) {
			throw new Exception("invalid configuration file " + args[1]) ;
		}
		this.configurationParameters = cfp.parseConfigurationFile(configFile) ;
		AbstractDistributedCVM.thisHostname =
			this.configurationParameters.getJvms2hosts().
								get(AbstractDistributedCVM.thisJVMURI) ;
		DCVMCyclicBarrier.CYCLIC_BARRIER_HOSTNAME =
					this.configurationParameters.getCyclicBarrierHostname() ;
		DCVMCyclicBarrier.CYCLIC_BARRIER_PORT =
					this.configurationParameters.getCyclicBarrierPort() ;
		GlobalRegistry.HOSTNAME =
					this.configurationParameters.getGlobalRegistryHostname() ;
		GlobalRegistry.PORT =
					this.configurationParameters.getGlobalRegistryPort() ;
		AbstractDistributedCVM.rmiRegistryCreators =
					this.configurationParameters.getRmiRegistryCreators() ;
		AbstractDistributedCVM.rmiRegistryHosts =
					this.configurationParameters.getRmiRegistryHosts() ;
		AbstractDistributedCVM.rmiRegistryPort =
					this.configurationParameters.getRmiregistryPort() ;
		this.initialised = false ;
		this.instantiationAndPublicationDone = false ;
		this.interconnectionDone = false ;
		// RMI registry creation
		if (AbstractDistributedCVM.rmiRegistryCreators.contains(
									AbstractDistributedCVM.thisJVMURI)) {
			AbstractDistributedCVM.theRMIRegistry =
								LocateRegistry.createRegistry(rmiRegistryPort) ;
		} else {
			AbstractDistributedCVM.theRMIRegistry = null ;
		}
		// Cyclic barrier initialisation
		this.cyclicBarrierClient =
			new DCVMCyclicBarrierClient(
					DCVMCyclicBarrier.CYCLIC_BARRIER_HOSTNAME,
					DCVMCyclicBarrier.CYCLIC_BARRIER_PORT,
					AbstractDistributedCVM.thisHostname,
					AbstractDistributedCVM.thisJVMURI) ;
	}

	// ------------------------------------------------------------------------
	// Life-cycle
	// ------------------------------------------------------------------------

	/**
	 * instantiate the components, publish their connection points on the
	 * registry and interconnect them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!deploymentDone
	 * post	deploymentDone
	 * </pre>
	 * @throws Exception 
	 * 
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		assert	!deploymentDone ;

		// Wait until all of the assembly object instantiation, and therefore
		// be sure that the RMI registry has been created.
		this.cyclicBarrierClient.waitBarrier() ;
		// Initialise the local RMI registry.
		this.initialise() ;
		this.cyclicBarrierClient.waitBarrier() ;
		// Instantiate the components and publish their ports on the
		// different registry.
		this.instantiateAndPublish() ;
		this.cyclicBarrierClient.waitBarrier() ;
		// Interconnect the components, lookup for their ports on the
		// different registry.
		this.interconnect() ;
		this.cyclicBarrierClient.waitBarrier() ;
		super.deploy() ;

		assert	deploymentDone ;
	}

	/**
	 * initialise the RMI registry reference for JVM that are not responsible
	 * for its creation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.initialised
	 * post	AbstractDistributedAssembly.theRMIRegistry != null
	 * post	this.initialised
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.DistributedComponentVirtualMachineI#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		assert	!this.initialised ;

		// RMI registry reference
		if (AbstractDistributedCVM.theRMIRegistry == null) {
			// looking for a host that has a running RMI registry
			// BEWARE: with Oracle RMI registry implementation, the registry for
			//         publication must be on the same host as the publisher!!
			//         If another vendor's registry is used, the following code
			//         assumes that only one registry will be running.
			String registryHostname = null ;
			if (!AbstractDistributedCVM.rmiRegistryHosts.contains(
																thisHostname)) {
				// Take the first, most probably the only one in this case
				registryHostname =
						AbstractDistributedCVM.rmiRegistryHosts.
															iterator().next() ;
			} else {
				registryHostname = thisHostname ;
			}
			AbstractDistributedCVM.theRMIRegistry =
					LocateRegistry.getRegistry(
								registryHostname,
								AbstractDistributedCVM.rmiRegistryPort) ;
		}

		try {
			DynamicComponentCreator dcc =
				new DynamicComponentCreator(
						this,
						AbstractDistributedCVM.thisJVMURI +
									DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI);
			this.deployedComponents.add(dcc) ;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					"The dynamic component creator has not been "
											+ "successfully deployed!") ;
			System.exit(1) ;
		}

		this.initialised = true ;

		assert	AbstractDistributedCVM.theRMIRegistry != null ;
		assert	this.initialised ;
	}

	/**
	 * simply establish that the instantiation of components and the publication
	 * of the entry points are completed, so it should be called at the end of
	 * the user's own <code>instantiateAndPublish</code> method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	instantiationAndPublicationDone
	 * </pre>
	 * 
	 * @throws Exception 
	 * @see fr.upmc.components.cvm.DistributedComponentVirtualMachineI#instantiateAndPublish()
	 */
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		assert	this.initialised ;

		this.instantiationAndPublicationDone = true ;
	}

	/**
	 * simply check if instantiation of components and the publication of the
	 * entry points are completed, so it should be called at the beginning of
	 * the user's own <code>interconnect</code> method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	instantiationAndPublicationDone
	 * post	interconnectionDone
	 * </pre>
	 * 
	 * @throws Exception 
	 * @see fr.upmc.components.cvm.DistributedComponentVirtualMachineI#interconnect()
	 */
	@Override
	public void			interconnect() throws Exception
	{
		assert	this.instantiationAndPublicationDone ;

		this.interconnectionDone = true ;
	}

	/**
	 * simply check if the deployment and the interconnection are completed,
	 * so it should be called at the beginning of the user's own
	 * <code>start</code> method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	deploymentDone
	 * post	!this.isShutdown()
	 * </pre>

	 * @throws Exception 
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#start()
	 */
	@Override
	public void			start() throws Exception
	{
		// Start all of the components that are running within the current
		// virtual machine
		super.start() ;
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		this.cyclicBarrierClient.waitBarrier() ;
		super.shutdown();
		this.cyclicBarrierClient.waitBarrier() ;
		this.cyclicBarrierClient.closeBarrier() ;
		GLOBAL_REGISTRY_CLIENT.shutdown() ;
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws Exception
	{
		this.cyclicBarrierClient.waitBarrier() ;
		super.shutdownNow();
		this.cyclicBarrierClient.waitBarrier() ;
		this.cyclicBarrierClient.closeBarrier() ;
		GLOBAL_REGISTRY_CLIENT.shutdown() ;
	}
}

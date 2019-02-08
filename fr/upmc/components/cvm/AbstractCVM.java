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

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.ConnectionBuilder;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreator;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>AbstractCVM</code> defines the common properties of
 * component virtual machines in the component model.
 *
 * <p><strong>Description</strong></p>
 * 
 * Local CVM are deployed on a single Java virtual machine and have local
 * component interconnections only.  A CVM must define a <code>deploy</code>
 * method that includes all the code necessary to instantiate and interconnect
 * the static components in the application.  Then, they must define a
 * <code>start</code> method that plays the role of a <code>main</code> method
 * in object-oriented Java applications.
 * 
 * <p><strong>Usage</strong></p>
 * 
 * Local CVM are defined as subclasses of this abstract class.  A CVM has to
 * redefine <code>deploy</code> and may redefine <code>start</code>.  The method
 * <code>start</code> defined here defaults to starting all of the components
 * registered as deployed on this site by calling the method
 * <code>addDeployedComponent</code>.
 * 
 * Every port that will be used to connect components must be published in
 * the local registry by calling the method <code>localPublishPort</code> and
 * it can be unpublished by calling the method <code>localUnpublishPort</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.componentRegistry != null
 * </pre>
 * 
 * <p>Created on : 2011-11-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractCVM
implements	ComponentVirtualMachineI
{
	/**	Enables or not debugging messages.									*/
	public static boolean DEBUG = true ;
	/** suffix for the dynamic component creator component inbound port URI.	*/
	public static final String DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI = "-dcc" ;

	// ------------------------------------------------------------------------
	// Debugging mode configuration
	// ------------------------------------------------------------------------

	public static void	toggleDebugMode()
	{
		AbstractCVM.DEBUG = !AbstractCVM.DEBUG ;
	}

	// ------------------------------------------------------------------------
	// Internal information about components in the CVM and CVM lifecycle
	// management.
	// ------------------------------------------------------------------------
	
	/** collection of deployed components.									*/
	protected final Vector<ComponentI>		deployedComponents ;

	/** initial number of potential entries in the local registry.			*/
	protected static int					LOCAL_REGISTRY_INIT_SIZE = 1000 ;
	/** local registry linking port URI to local port objects.				*/
	protected static final Hashtable<String,PortI>	LOCAL_REGISTRY =
						new Hashtable<String,PortI>(LOCAL_REGISTRY_INIT_SIZE) ;

	/** true if the deployment is completed and false otherwise.			*/
	protected boolean						deploymentDone ;
	/** true if the CVM currently running is distributed.					*/
	public static boolean					isDistributed ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create and initialise a local CVM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public				AbstractCVM() {
		this(false);
	}

	/**
	 * create and initialise the CVM, tagged as distributed if the parameter is
	 * true, and as local otherwise; note however that distributed CVM must be
	 * created as subclasses of the class <code>AbstractDistributedCVM</code>,
	 * so this constructor should never be called directly but only through the
	 * other constructor of this class or through one of the constructors of
	 * <code>AbstractDistributedCVM</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param isDistributed	true if the CVM is distributed, false otherwise
	 */
	public				AbstractCVM(
		boolean isDistributed
		)
	{
		super();
		this.deployedComponents = new Vector<ComponentI>() ;
		this.deploymentDone = false ;
		AbstractCVM.isDistributed = isDistributed ;
		AbstractCVM.initialiseConnectionBuilder() ;

		if (!isDistributed) {
			try {
				DynamicComponentCreator dcc =
						new DynamicComponentCreator(
									this,
									DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI);
				this.deployedComponents.add(dcc) ;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("The dynamic component creator has not been "
												+ "successfully deployed!") ;
				System.exit(1) ;
			}
		}
	}

	// ------------------------------------------------------------------------
	// Static Methods
	// ------------------------------------------------------------------------

	/**
	 * initialise the static variable <code>SINGLETON</code> of the class
	 * <code>ConnectionBuilder</code> to the sole instance of this class
	 * that is required and used in the current JVM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no postcondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	protected static synchronized void	initialiseConnectionBuilder()
	{
		assert	ConnectionBuilder.SINGLETON == null ;

		if (!AbstractCVM.isDistributed) {
			ConnectionBuilder.SINGLETON =
				new ConnectionBuilder(LOCAL_REGISTRY, null) ;
		} else {
			ConnectionBuilder.SINGLETON =
				new ConnectionBuilder(
						LOCAL_REGISTRY,
						AbstractDistributedCVM.GLOBAL_REGISTRY_CLIENT) ;
		}

		assert	ConnectionBuilder.SINGLETON != null ;
	}

	/**
	 * publish the port in the local registry.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	port != null
	 * post true			// no postcondition.
	 * </pre>
	 * 
	 * @param port	port to be published
	 * @throws Exception 
	 */
	public synchronized static void	localPublishPort(PortI port)
	throws Exception
	{
		assert	port != null ;
		assert	!LOCAL_REGISTRY.containsKey(port.getPortURI()) ;

		if (AbstractCVM.DEBUG) {
			System.out.println("locally publishing port " + port.getPortURI()) ;
		}

		LOCAL_REGISTRY.put(port.getPortURI(), port) ;

		assert	LOCAL_REGISTRY.containsKey(port.getPortURI()) ;
		assert	port == LOCAL_REGISTRY.get(port.getPortURI()) ;
	}

	/**
	 * unpublish the port in the local registry.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	port != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param port	port to be unpublished
	 * @throws Exception
	 */
	public synchronized static void	localUnpublishPort(PortI port)
	throws Exception
	{
		assert	port != null ;
		assert	LOCAL_REGISTRY.containsKey(port.getPortURI()) ;
		assert	port == LOCAL_REGISTRY.get(port.getPortURI()) ;

		if (AbstractCVM.DEBUG) {
			System.out.println("unpublishing local port " + port.getPortURI()) ;
		}

		LOCAL_REGISTRY.remove(port.getPortURI()) ;

		assert	!LOCAL_REGISTRY.containsKey(port.getPortURI()) ;
	}

	// ------------------------------------------------------------------------
	// Instance Methods
	// ------------------------------------------------------------------------

	/**
	 * simply set the <code>deploymentDone</code> flag to true, so it should
	 * be called at the end of the user's own <code>deploy</code> method.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!deploymentDone
	 * post	deploymentDone
	 * </pre>
	 * 
	 * @throws Exception 
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		assert	!deploymentDone ;

		this.deploymentDone = true ;
	}
	
	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#isDeployedComponent(fr.upmc.components.ComponentI)
	 */
	@Override
	public boolean		isDeployedComponent(ComponentI component)
	{
		assert	component != null ;

		return this.deployedComponents.contains(component) ;
	}

	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#addDeployedComponent(fr.upmc.components.ComponentI)
	 */
	@Override
	public void			addDeployedComponent(ComponentI component)
	{
		assert	component != null ;
		assert	!this.isDeployedComponent(component) ;

		this.deployedComponents.add(component) ;
	}

	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#removeDeployedComponent(fr.upmc.components.ComponentI)
	 */
	@Override
	public void			removeDeployedComponent(ComponentI component)
	{
		assert	component != null ;
		assert	this.isDeployedComponent(component) ;

		this.deployedComponents.remove(component) ;
	}

	/**
	 * check if the deployment is completed, and start all of the deployed
	 * components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	deploymentDone
	 * post	!this.isShutdown()
	 * </pre>
	 * 
	 * @throws Exception 
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#start()
	 */
	@Override
	public void			start() throws Exception
	{
		assert	this.deploymentDone ;

		for(ComponentI c : this.deployedComponents) {
			c.start() ;
		}
	}

	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		// assumes that all components are disconnected

		for(ComponentI c : this.deployedComponents) {
			c.shutdown() ;
		}
	}

	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws Exception
	{
		for(ComponentI c : this.deployedComponents) {
			c.shutdownNow() ;
		}
	}

	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#isShutdown()
	 */
	@Override
	public boolean		isShutdown()
	{
		boolean ret = true ;
		for(ComponentI c : this.deployedComponents) {
			ret = ret && c.isShutdown() ;
		}
		return ret ;
	}

	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#isTerminated()
	 */
	@Override
	public boolean		isTerminated()
	{
		boolean ret = true ;
		for(ComponentI c : this.deployedComponents) {
			ret = ret && c.isTerminated() ;
		}
		return ret ;
	}

	/**
	 * @see fr.upmc.components.cvm.ComponentVirtualMachineI#awaitTermination(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean		awaitTermination(long timeout, TimeUnit unit)
	throws InterruptedException
	{
		// TODO needs more reflection... how to await termination of several
		// entities?
		return false;
	}
}

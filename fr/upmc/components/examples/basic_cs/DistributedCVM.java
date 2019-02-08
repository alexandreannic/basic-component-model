package fr.upmc.components.examples.basic_cs;

import java.io.File;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.examples.basic_cs.components.URIConsumer;
import fr.upmc.components.examples.basic_cs.components.URIProvider;
import fr.upmc.components.ports.PortI;
import fr.upmc.components.registry.distributedRegistry.DistributedRegistryClient;

/**
 * The class <code>DistributedCVM</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 22 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DistributedCVM
extends		AbstractDistributedCVM
{
	protected static String		PROVIDER_JVM_URI = "provider" ;
	protected static String		CONSUMER_JVM_URI = "consumer" ;
	protected static String		URIConsumerOutboundPortURI = "oport" ;
	protected static String		URIProviderInboundPortURI = "iport" ;

	protected URIProvider	uriProvider ;
	protected URIConsumer	uriConsumer ;

	public				DistributedCVM(String[] args)
	throws Exception
	{
		super(args);
//		GLOBAL_REGISTRY_CLIENT = new DistributedRegistryClient();
	}

	/**
	 * do some initialisation before any can go on.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise() ;
		// any other application-specific initialisation must be put here

		// logging configuration
		AbstractComponent.configureLogging("." + File.separator + "bcm-tmp",
										   "log", 4000, '|') ;
//		// debugging mode configuration; comment and uncomment the line to see
//		// the difference
//		AbstractCVM.toggleDebugMode() ;
	}

	/**
	 * instantiate components and publish their ports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#instantiateAndPublish()
	 */
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {

			// create the provider component
			this.uriProvider =
				new URIProvider("myURI", URIProviderInboundPortURI, true) ;
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			// uriProvider.toggleTracing() ;
			uriProvider.toggleLogging() ;
			// add it to the deployed components
			this.deployedComponents.add(uriProvider) ;

		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {

			// create the consumer component
			this.uriConsumer = new URIConsumer(URIConsumerOutboundPortURI) ;
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			// uriConsumer.toggleTracing() ;
			uriConsumer.toggleLogging() ;
			// add it to the deployed components
			this.deployedComponents.add(uriConsumer) ;

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.instantiateAndPublish();
	}

	/**
	 * interconnect the components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#interconnect()
	 */
	@Override
	public void			interconnect() throws Exception
	{
		assert	this.instantiationAndPublicationDone ;

		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {

		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {

			// do the connection
			PortI consumerOutboundPort =
					uriConsumer.findPortFromURI(URIConsumerOutboundPortURI) ;
			consumerOutboundPort.doConnection(
				URIProviderInboundPortURI,
				"fr.upmc.components.examples.basic_cs.URIServiceConnector") ;

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.interconnect();
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {

			// print logs on files, if activated
			this.uriProvider.printExecutionLogOnFile("provider") ;

			// any disconnection not done yet should be performed here

		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {

			// print logs on files, if activated
			this.uriConsumer.printExecutionLogOnFile("consumer") ;

			// any disconnection not done yet should be performed here

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.shutdown();
	}

	public static void	main(String[] args)
	{
		System.out.println("Beginning") ;
		try {
			DistributedCVM da = new DistributedCVM(args) ;
			da.deploy() ;
			da.start() ;
			Thread.sleep(15000L) ;
			da.shutdown() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Main thread ending") ;
		System.exit(0);
	}
}

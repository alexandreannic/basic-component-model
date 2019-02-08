package fr.upmc.components.examples.dynamic_cs;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.examples.dynamic_cs.components.DynamicAssembler;

/**
 * The class <code>DynamicDistributedDynamicCVM</code> creates a component
 * assembly for the multiple-JVM execution of the dynamic connection example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 14 mars 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DynamicDistributedCVM
extends		AbstractDistributedCVM
{
	protected static String		ASSEMBLER_JVM_URI = "assembler" ;
	protected static String		PROVIDER_JVM_URI = "provider" ;
	protected static String		CONSUMER_JVM_URI = "consumer" ;
	protected static String		URIConsumerPortURIPrefix = "oport" ;
	protected static String		URIProviderInboundPortURI = "iport" ;

	protected DynamicAssembler	da ;

	public				DynamicDistributedCVM(
		String[] args
		) throws Exception
	{
		super(args);
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
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#instantiateAndPublish()
	 */
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(ASSEMBLER_JVM_URI)) {

			this.da = new DynamicAssembler(this,
										   CONSUMER_JVM_URI,
										   PROVIDER_JVM_URI,
										   URIConsumerPortURIPrefix,
										   URIProviderInboundPortURI) ;
			this.addDeployedComponent(this.da) ;

		}

		super.instantiateAndPublish();
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
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#start()
	 */
	@Override
	public void			start() throws Exception
	{
		super.start() ;

		if (thisJVMURI.equals(ASSEMBLER_JVM_URI)) {
			final DynamicAssembler fDa = this.da ;
			this.da.runTask(
				new ComponentI.ComponentTask() {
						@Override
						public void run() {
							try {
								fDa.deploy() ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}) ;
		}
	}

	public static void	main(String[] args)
	{
		System.out.println("Beginning") ;
		try {
			DynamicDistributedCVM dda =
										new DynamicDistributedCVM(args) ;
			dda.deploy() ;
			dda.start() ;
			Thread.sleep(15000) ;
			dda.shutdown() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Main thread ending") ;
		System.exit(0);
	}
}

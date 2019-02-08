package fr.upmc.components.examples.dynamic_cs;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.examples.dynamic_cs.components.DynamicAssembler;

/**
 * The class <code>DynamicCVM</code> creates a component assembly for the
 * single-JVM execution of the dynamic connection example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 12 mai 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DynamicCVM
extends		AbstractCVM
{
	protected static String		ASSEMBLER_JVM_URI = "" ;
	protected static String		PROVIDER_JVM_URI = "" ;
	protected static String		CONSUMER_JVM_URI = "" ;
	protected static String		URIConsumerPortURIPrefix = "oport" ;
	protected static String		URIProviderInboundPortURI = "iport" ;

	protected DynamicAssembler	da ;

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		this.da = new DynamicAssembler(this,
				   					   CONSUMER_JVM_URI,
				   					   PROVIDER_JVM_URI,
				   					   URIConsumerPortURIPrefix,
				   					   URIProviderInboundPortURI) ;
		this.addDeployedComponent(this.da) ;

		// deployment done
		super.deploy() ;
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#start()
	 */
	@Override
	public void			start() throws Exception
	{
		super.start() ;

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

	public static void	main(String[] args)
	{
		System.out.println("Beginning") ;
		DynamicCVM da = new DynamicCVM() ;
		try {
			da.deploy() ;
			da.start() ;
			Thread.sleep(15000) ;
			da.shutdown() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Main thread ending") ;
		System.exit(0);
	}
}

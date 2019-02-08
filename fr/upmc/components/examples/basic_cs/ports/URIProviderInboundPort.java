package fr.upmc.components.examples.basic_cs.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.examples.basic_cs.components.URIProvider;
import fr.upmc.components.examples.basic_cs.interfaces.URIProviderI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * The class <code>URIProviderInboundPort</code> defines the inbound port
 * exposing the interface <code>URIProviderI</code> for components of
 * type <code>URIProvider</code>.
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
public class			URIProviderInboundPort
extends		AbstractInboundPort
implements	URIProviderI
{
	/** required by UnicastRemonteObject.									*/
	private static final long serialVersionUID = 1L;

	/**
	 * create the port under some given URI and for a given owner.
	 * 
	 * The constructor for <code>AbstractInboundPort</code> requires the
	 * interface that the port is implementing as an instance of
	 * <code>java.lang.CLass</code>, but this is statically known so
	 * the constructor does not need to receive the information as parameter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner instanceof URIProvider
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	uri under which the port will be published.
	 * @param owner	component owning the port.
	 * @throws Exception
	 */
	public				URIProviderInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		// the implemented interface is statically known
		super(uri, URIProviderI.class, owner) ;

		assert	uri != null && owner instanceof URIProvider ;
	}

	/**
	 * calls the service method of the owner object by executing a task
	 * using one of the component's threads.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.examples.basic_cs.interfaces.URIProviderI#provideURI()
	 */
	@Override
	public String		provideURI() throws Exception
	{
		// a final variable is useful to reference the owner in the method
		// call.
		final URIProvider up = (URIProvider) this.owner ;
		// the handleRequestSync wait for the result before retunring to the
		// caller; hence it is a synchronous remote method invocation.
		return up.handleRequestSync(
				new ComponentService<String>() {
					@Override
					public String call() throws Exception {
						return up.provideURIService() ;
					}
				}) ;
	}
}

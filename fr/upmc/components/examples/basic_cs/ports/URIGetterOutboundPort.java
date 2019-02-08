package fr.upmc.components.examples.basic_cs.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.examples.basic_cs.interfaces.URIConsumerI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * The class <code>URIGetterOutboundPort</code> implements the outbound port
 * of a component that requires an URI service through the
 * <code>URIConsumerI</code> interface.
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
public class			URIGetterOutboundPort
extends		AbstractOutboundPort
implements	URIConsumerI
{
	/**
	 * create the port with the given URI and the given owner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of the port.
	 * @param owner	owner of the port.
	 */
	public				URIGetterOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, URIConsumerI.class, owner) ;
	}

	/**
	 * get an URI by calling the server component through the connector that
	 * implements the required interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.examples.basic_cs.interfaces.URIConsumerI#getURI()
	 */
	@Override
	public String		getURI() throws Exception
	{
		return ((URIConsumerI)this.connector).getURI() ;
	}
}

package fr.upmc.components.examples.basic_cs;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.examples.basic_cs.interfaces.URIConsumerI;
import fr.upmc.components.examples.basic_cs.interfaces.URIProviderI;

/**
 * The class <code>URIServiceConnector</code> implements a connector between
 * the <code>URIConsumerI</code> and the <code>URIProviderI</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>URIConsumerI</code> and in the
 * method <code>getURI</code>, it calls the corresponding offered method
 * <code>provideURI</code>.
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
public class			URIServiceConnector
extends		AbstractConnector
implements	URIConsumerI
{
	/**
	 * implement the required interface by simply calling the inbound port with
	 * the corresponding offered method.
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
		return ((URIProviderI)this.offering).provideURI() ;
	}
}

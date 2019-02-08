package fr.upmc.components.examples.basic_cs.components;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.examples.basic_cs.interfaces.URIProviderI;
import fr.upmc.components.examples.basic_cs.ports.URIProviderInboundPort;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>URIProvider</code>
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
public class			URIProvider
extends		AbstractComponent
{
	// ------------------------------------------------------------------------
	// Constructors and instance variables
	// ------------------------------------------------------------------------

	/**	a string prefix that will identify the URI provider.				*/
	protected String	uriPrefix ;

	/**
	 * create a component with a given uri prefix and that will expose its
	 * service through a port of the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uriPrefix			the prefix of this provider
	 * @param providerPortURI	the URI of the port exposing the service
	 * @param isDistributed		true if distributed, otherwise false
	 * @throws Exception
	 */
	public					URIProvider(
		String uriPrefix,
		String providerPortURI,
		boolean isDistributed
		) throws Exception
	{
		super(true);
		this.uriPrefix = uriPrefix ;

		// put the offered interface in the set of interfaces offered by
		// the component.
		this.addOfferedInterface(URIProviderI.class) ;
		// create the port that exposes the offered interface
		PortI p = new URIProviderInboundPort(providerPortURI, this) ;
		// add the port to the set of ports of the component
		this.addPort(p) ;
		// publish the port
		if (isDistributed) {
			// if distributed, publish on registry
			p.publishPort() ;
		} else {
			// if not distributed, a local publication is sufficient
			p.localPublishPort() ;
		}
	}

	//-------------------------------------------------------------------------
	// Component internal services
	//-------------------------------------------------------------------------

	/**
	 * produce and return an URI beginning with a substring that identifies the
	 * provider.
	 * 
	 * Starting point is to define the service methods of the server component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the produced URI
	 */
	public String		provideURIService() {
		this.logMessage("provider create a new URI and returns it.") ;
		// see http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq
		return  this.uriPrefix + "-" + java.util.UUID.randomUUID().toString() ;
	}
}

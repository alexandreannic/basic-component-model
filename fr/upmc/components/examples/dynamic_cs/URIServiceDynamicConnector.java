package fr.upmc.components.examples.dynamic_cs;

import java.io.Serializable;
import fr.upmc.components.examples.basic_cs.URIServiceConnector;

/**
 * The class <code>URIServiceDynamicConnector</code> implements a dynamically
 * installed connector between the <code>URIConsumerI</code> and the
 * <code>URIProviderI</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 18 mars 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			URIServiceDynamicConnector
extends		URIServiceConnector
implements	Serializable
{
	private static final long serialVersionUID = 1L;

}

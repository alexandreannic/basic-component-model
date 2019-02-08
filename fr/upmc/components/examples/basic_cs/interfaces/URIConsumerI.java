package fr.upmc.components.examples.basic_cs.interfaces;

import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>URIConsumerI</code>
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
public interface		URIConsumerI
extends		RequiredI
{
	public String		getURI() throws Exception ;
}

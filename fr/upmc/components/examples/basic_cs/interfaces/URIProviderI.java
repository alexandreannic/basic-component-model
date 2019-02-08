package fr.upmc.components.examples.basic_cs.interfaces;

import fr.upmc.components.interfaces.OfferedI;

/**
 * The interface <code>URIProviderI</code>
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
public interface URIProviderI
extends		OfferedI
{
	public String		provideURI() throws Exception ;
}

package fr.upmc.components.examples.dynamic_cs.components;

import fr.upmc.components.examples.basic_cs.components.URIProvider;

/**
 * The class <code>DynamicURIProvider</code> implements the dynamic version of
 * the <code>URIProvider</code> component of the basic client/server example.
 *
 * <p><strong>Description</strong></p>
 * 
 * The only important point in this redefinition is to change the type of the
 * third parameter in the constructor from <code>boolean</code> to
 * <code>Boolean</code>.  This is required by the dynamic component creator
 * which looks for a constructor of the given parameter and which does not
 * work properly for standard types.
 * 
 * Therefore, the rule for the dynamic creation of components is to lake sure
 * that all constructor parameters are of object (class) types.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 29 avr. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DynamicURIProvider
extends URIProvider
{
	public				DynamicURIProvider(
		String uriPrefix,
		String providerPortURI,
		Boolean isDistributed
		) throws Exception
	{
		super(uriPrefix, providerPortURI, isDistributed);
	}
}

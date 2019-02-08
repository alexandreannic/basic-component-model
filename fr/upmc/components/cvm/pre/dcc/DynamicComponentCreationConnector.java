package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.connectors.AbstractConnector;

/**
 * The class <code>DynamicComponentCreationConnector</code> implements a
 * connector between components requiring <code>DynamicComponentCreationI</code>
 * and a component <code>DynamicComponentCreator</code>.
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
public class			DynamicComponentCreationConnector
extends		AbstractConnector
implements	DynamicComponentCreationI
{
	/**
	 * @see fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI#createComponent(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void			createComponent(
		String classname,
		Object[] constructorParams
		) throws Exception
	{
		((DynamicComponentCreationI)this.offering).
								createComponent(classname, constructorParams) ;
	}
}

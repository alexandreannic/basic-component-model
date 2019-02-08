package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * The class <code>DynamicComponentCreationOutboundPort</code> implements the
 * outbound port of a <code>DynamicCOmponentCreator</code> client component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 13 mars 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DynamicComponentCreationOutboundPort
extends		AbstractOutboundPort
implements	DynamicComponentCreationI
{
	public 				DynamicComponentCreationOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(DynamicComponentCreationI.class, owner);
	}

	public				DynamicComponentCreationOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, DynamicComponentCreationI.class, owner);
	}

	/**
	 * @see fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI#createComponent(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void			createComponent(
		String classname,
		Object[] constructorParams
		) throws Exception
	{
		((DynamicComponentCreationI)this.connector).
								createComponent(classname, constructorParams) ;
	}
}

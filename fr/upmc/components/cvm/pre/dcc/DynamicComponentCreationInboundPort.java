package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * The class <code>DynamicComponentCreationInboundPort</code> implements the
 * inbound port of a <code>DynamicCOmponentCreator</code> component.
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
public class			DynamicComponentCreationInboundPort
extends		AbstractInboundPort
implements	DynamicComponentCreationI
{
	private static final long serialVersionUID = 1L;

	public				DynamicComponentCreationInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(DynamicComponentCreationI.class, owner);
	}

	public				DynamicComponentCreationInboundPort(
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
		final DynamicComponentCreator dcc =
										(DynamicComponentCreator) this.owner ;
		final String fClassname = classname ;
		final Object[] fConstructorParams = constructorParams ;
		dcc.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						dcc.createComponent(fClassname, fConstructorParams) ;
						return null ;
					}
				}) ;
	}
}

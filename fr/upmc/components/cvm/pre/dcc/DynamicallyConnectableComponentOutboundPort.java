package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * The class <code>DynamicallyConnectableComponentOutboundPort</code> reifies
 * the <code>DynamicallyConnectableComponentI</code> required interface for
 * a component and connects it to the offering component through a
 * <code>DynamicallyConnectableComponentConnector</code> connector.
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
public class			DynamicallyConnectableComponentOutboundPort
extends		AbstractOutboundPort
implements	DynamicallyConnectableComponentI
{
	public				DynamicallyConnectableComponentOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(DynamicallyConnectableComponentI.class, owner);
	}

	public				DynamicallyConnectableComponentOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, DynamicallyConnectableComponentI.class, owner);
	}

	/**
	 * @see fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI#connectWith(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void			connectWith(
		String serverPortURI,
		String clientPortURI,
		String ccname
		) throws Exception
	{
		((DynamicallyConnectableComponentI)this.connector).
							connectWith(serverPortURI, clientPortURI, ccname) ;
	}

	/**
	 * @see fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI#disconnectWith(java.lang.String, java.lang.String)
	 */
	@Override
	public void			disconnectWith(
		String serverPortURI,
		String clientPortURI
		) throws Exception
	{
		((DynamicallyConnectableComponentI)this.connector).
								disconnectWith(serverPortURI, clientPortURI) ;
	}
}

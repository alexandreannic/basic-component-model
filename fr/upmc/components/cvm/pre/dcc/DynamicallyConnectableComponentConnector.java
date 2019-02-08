package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.connectors.AbstractConnector;

/**
 * The class <code>DynamicallyConnectableComponentConnector</code> connects
 * a component requiring the dynamic component connection service from another
 * component offering it.
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
public class			DynamicallyConnectableComponentConnector
extends		AbstractConnector
implements	DynamicallyConnectableComponentI
{
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
		((DynamicallyConnectableComponentI)this.offering).
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
		((DynamicallyConnectableComponentI)this.offering).
								disconnectWith(serverPortURI, clientPortURI) ;
	}
}

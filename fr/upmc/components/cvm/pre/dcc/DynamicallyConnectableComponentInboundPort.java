package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * The class <code>DynamicallyConnectableComponentInboundPort</code> reifies
 * the <code>DynamicallyConnectableComponentI</code> offered interface for
 * a component and connects it to the requiring component through a
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
public class			DynamicallyConnectableComponentInboundPort
extends		AbstractInboundPort
implements	DynamicallyConnectableComponentI
{
	private static final long serialVersionUID = 1L ;

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner instanceof DynamicallyConnectableI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner
	 * @throws Exception
	 */
	public				DynamicallyConnectableComponentInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(DynamicallyConnectableComponentI.class, owner) ;
		assert	owner instanceof DynamicallyConnectableI ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner instanceof DynamicallyConnectableI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of the port
	 * @param owner	component owning the port
	 * @throws Exception
	 */
	public				DynamicallyConnectableComponentInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, DynamicallyConnectableComponentI.class, owner);
		assert	owner instanceof DynamicallyConnectableI ;
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
		final DynamicallyConnectableI dc =
										(DynamicallyConnectableI) this.owner ;
		final String fServerPortURI = serverPortURI ;
		final String fClientPortURI = clientPortURI ;
		final String fCcname = ccname ;
		this.owner.handleRequestSync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						dc.connectWith(fServerPortURI, fClientPortURI, fCcname) ;
						return null ;
					}
				});
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
		final DynamicallyConnectableI dc =
										(DynamicallyConnectableI) this.owner ;
		final String fServerPortURI = serverPortURI ;
		final String fClientPortURI = clientPortURI ;
		this.owner.handleRequestSync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						dc.disconnectWith(fServerPortURI, fClientPortURI) ;
						return null ;
					}
				});
	}
}

package fr.upmc.components.cvm.pre.dcc;

/**
 * The interface <code>DynamicallyConnectableI</code> defines the component
 * methods implementing the dynamic connection service.
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
public interface		DynamicallyConnectableI
{
	/**
	 * connect the ports through the given connector, one of the two given port
	 * URI must correspond to a port of the component executing this service.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param serverPortURI	URI of the server side port.
	 * @param clientPortURI	URI of the client side port.
	 * @param ccname		name of the connector class used in the connection.
	 * @throws Exception 
	 */
	public void			connectWith(
		String serverPortURI,
		String clientPortURI,
		String ccname
		) throws Exception ;

	/**
	 * disconnect the ports through the given connector, one of the two given
	 * port URI must correspond to a port of the component executing this
	 * service.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param serverPortURI
	 * @param clientPortURI
	 * @throws Exception
	 */
	public void			disconnectWith(
		String serverPortURI,
		String clientPortURI
		) throws Exception ;
}

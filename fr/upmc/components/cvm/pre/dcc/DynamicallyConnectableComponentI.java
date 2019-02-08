package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>DynamicallyConnectableI</code> defines a component
 * service to connect some of its port to another component at run-time.
 *
 * <p><strong>Description</strong></p>
 * 
 * To implement the dynamic creation of components, such dynamically created
 * components must also be connected dynamically.  However, to be able to
 * ensure that the two components are created before they are connected, the
 * connection code cannot be executed in the constructor of the components
 * but rather after their creation.  Moreover, the connection logic in
 * <code>ConnectionBuilder</code> requires that at least one of the two ports
 * to be connected is local to the virtual machine executing the method
 * <code>connectWith</code>.  So, the best solution is to have one of the
 * two dynamically created component perform the call to
 * <code>ConnectionBuilder</code>, which is the purpose of the service
 * defined here.
 * 
 * TODO: maybe this should evolve towards a more comprehensive "reflective"
 * interface making possible to perform several other operations that could be
 * called as Java methods by an assembly but would be nice to be also able to
 * call dynamically among components, such as the method <code>run</code>.
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
public interface		DynamicallyConnectableComponentI
extends		OfferedI,
			RequiredI
{
	/**
	 * connect the ports, one of the two given port URI must correspond to a
	 * port of the component executing this service.
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
	 * disconnect the ports, one of the two given port URI must correspond to a
	 * port of the component executing this service.
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

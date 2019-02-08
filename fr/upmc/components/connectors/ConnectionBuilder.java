package fr.upmc.components.connectors;

// Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
// 
// Jacques.Malenfant@lip6.fr
// 
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
// 
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
// 
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
// 
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
// 
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.interfaces.ComponentImplementedI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataTwoWayI;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.components.interfaces.TwoWayI;
//import fr.upmc.components.ports.InboundPortI;
//import fr.upmc.components.ports.OutboundPortI;
//import fr.upmc.components.ports.DataInboundPortI;
//import fr.upmc.components.ports.DataOutboundPortI;
import fr.upmc.components.ports.PortI;
import fr.upmc.components.registry.ConnectionData;
import fr.upmc.components.registry.ConnectionType;
import fr.upmc.components.registry.GlobalRegistryClient;
import fr.upmc.components.registry.simpleRegistry.SimpleRegistryClient;

/**
 * The class <code>ConnectionBuilder</code> groups all the logic for the
 * interconnection of components, implementing location-transparency for
 * components by taking care care of identifying the possible cases (local
 * versus remote) and building the correct connection for each case.  It also
 * provides for the disconnection logic.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true	// TODO
 * </pre>
 * 
 * <p>Created on : 2012-10-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ConnectionBuilder {

	public static ConnectionBuilder	SINGLETON ;

	protected final Hashtable<String,PortI>	localRegistry ;
	protected final GlobalRegistryClient	globalRegistryClient ;

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param localRegistry
	 * @param gLOBAL_REGISTRY_CLIENT
	 */
	public				ConnectionBuilder(
		Hashtable<String, PortI> localRegistry,
		GlobalRegistryClient gLOBAL_REGISTRY_CLIENT
		)
	{
		super();
		this.localRegistry = localRegistry;
		this.globalRegistryClient = gLOBAL_REGISTRY_CLIENT;
	}

	/**
	 * find the connection data for a socket-based communication in the
	 * registry.
	 * 
	 * SOCKET COMMUNICATION NOT YET TERMINATED
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param remoteURI	uri of the socket port.
	 * @return			the connection data corresponding to the remote URI
	 * @throws Exception
	 */
	protected ConnectionData		getRemoteSocketInfo(String remoteURI)
	throws Exception
	{
		String result = this.globalRegistryClient.lookup(remoteURI) ;
		ConnectionData cd = new ConnectionData(result) ;
		if (cd.getType() != ConnectionType.SOCKET) {
			throw new Exception("not a socket port!") ;
		}
		return cd ;
	}

	/**
	 * find the remote reference corresponding to a port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param remoteURI	uri identifying the port in the registry.
	 * @return			reference to the component port.
	 * @throws Exception
	 */
	protected ComponentImplementedI	getRemoteReference(String remoteURI)
	throws Exception
	{
		ComponentImplementedI inter = null ;
		String info = this.globalRegistryClient.lookup(remoteURI) ;
		ConnectionData cd = new ConnectionData(info) ;
		if (cd.getType() == ConnectionType.RMI) {
			try {
				inter = (ComponentImplementedI)
							Naming.lookup(
								"//" + cd.getHostname() +
								":" + AbstractDistributedCVM.rmiRegistryPort +
								"/" + remoteURI) ;
			} catch (MalformedURLException e) {
				System.out.println("MalformedURLException thrown when trying to get the remote reference of "+ remoteURI);
				throw e ;
			} catch (RemoteException e) {
				System.out.println("RemoteException thrown when trying to get the remote reference of "+ remoteURI);
				throw e ;
			} catch (NotBoundException e) {
				System.out.println("NotBoundException thrown when trying to get the remote reference of "+ remoteURI);
				throw e ;
			}
		} else { // cd.getType() == ConnectionType.SOCKET
			throw new Exception("not a RMI port!") ;
		}
		return inter ;
	}

	/**
	 * connects two components through the named port (using port URI).
	 * 
	 * If the two component are local, the connector is simply used to connect
	 * them.  If one is local and the other is remote, the global registry is
	 * called to get the reference to the remote port in order to connect it
	 * to the local one through the connector.  The standard case is to connect
	 * a local client to a remote server, and then the reference to the remote
	 * server port is obtained from the registry to connect.  But when the
	 * server port implements a data offered interface (resp. a two way
	 * interface), the server-side connector is used to connect to the client
	 * port push interface (resp. two way interface), and then the reference
	 * to the client port is also obtained from the registry.
	 * 
	 * It should never happen that this method is called upon two remote
	 * components, as it would be nonsense for a host to try to connect
	 * components that are both on other hosts.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param serverPortURI	URI of the server port to connect.
	 * @param clientPortURI	URI of the client port to connect.
	 * @param connector		local connector to be used to connect.
	 * @throws Exception 
	 */
	public void			connectWith(
		String serverPortURI,
		String clientPortURI,
		ConnectorI connector
		) throws Exception
	{
		PortI localServerPort = this.localRegistry.get(serverPortURI) ;
		PortI localClientPort = this.localRegistry.get(clientPortURI) ;
		if (localServerPort != null) {
			localServerPort.setClientPortURI(clientPortURI) ;
			localServerPort.setServerPortURI(serverPortURI) ;
		}
		if (localClientPort != null) {
			localClientPort.setClientPortURI(clientPortURI) ;
			localClientPort.setServerPortURI(serverPortURI) ;
		}
		if (localServerPort != null && localClientPort != null) {
			// local connection
			connector.setRemote(false) ;
			if (AbstractCVM.DEBUG) {
				System.out.println("local connection of client " + 
								clientPortURI + " to server " + serverPortURI) ;
			}
			connector.connect(
					(OfferedI)localServerPort, (RequiredI)localClientPort) ;
		} else if (localServerPort != null && localClientPort == null) {
			// remote connection
			connector.setRemote(true) ;
			if (AbstractCVM.DEBUG) {
				System.out.println("remote connection of remote client " + 
						clientPortURI + " to local server " + serverPortURI) ;
			}
			if (localServerPort instanceof DataOfferedI.PullI) {
				if (AbstractCVM.DEBUG) {
					System.out.println("connecting data interfaces...");
				}
				PortI remoteClientPort =
							(PortI) this.getRemoteReference(clientPortURI) ;
				remoteClientPort.setServerPortURI(serverPortURI) ;
				((DataConnectorI)connector).connectServer(
					(OfferedI)localServerPort, (RequiredI)remoteClientPort) ;
			} else if (localServerPort instanceof DataTwoWayI.PullI) {
				PortI remoteClientPort =
							(PortI) this.getRemoteReference(clientPortURI) ;
				remoteClientPort.setServerPortURI(serverPortURI) ;
				((DataTwoWayConnectorI)connector).connectServer(
					(OfferedI)localServerPort, (RequiredI)remoteClientPort) ;
			} else if (localServerPort instanceof TwoWayI) {
				PortI remoteClientPort =
							(PortI) this.getRemoteReference(clientPortURI) ;
				remoteClientPort.setServerPortURI(serverPortURI) ;
				((TwoWayConnectorI)connector).connectServer(
					(OfferedI)localServerPort, (RequiredI)remoteClientPort) ;
			} else if (localServerPort instanceof OfferedI) {
				// do nothing, passive connection
			} else {
				throw new Exception("unknown interface " + localServerPort) ;
			}
		} else if (localServerPort == null && localClientPort != null) {
			// remote connection
			connector.setRemote(true) ;
			if (AbstractCVM.DEBUG) {
				System.out.println("remote connection of local client " +
					clientPortURI + " to remote server " + serverPortURI) ;
			}
			PortI remoteServerPort =
							(PortI) this.getRemoteReference(serverPortURI) ;
			remoteServerPort.setClientPortURI(clientPortURI) ;
			connector.connectClient(
					(OfferedI)remoteServerPort, (RequiredI)localClientPort) ;
		} else { // localServerPort == null && localClientPort == null
			throw new Exception(
					"ConnectionBuilder: can't connect two remote components!") ;
		}
	}

	/**
	 * disconnects two components that were previously connected through
	 * the two ports which URIs are given as parameters, and the connector
	 * also given as parameter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param serverPortURI	URI of the server side port.
	 * @param clientPortURI	URI of the cloent side port.
	 * @param connector		local connector used to connect the two ports.
	 * @throws Exception
	 */
	public void			disconnectWith(
		String serverPortURI,
		String clientPortURI,
		ConnectorI connector
		) throws Exception
	{
		PortI localServerPort = this.localRegistry.get(serverPortURI) ;
		PortI localClientPort = this.localRegistry.get(clientPortURI) ;
		if (localServerPort != null) {
			localServerPort.unsetClientPortURI() ;
			localServerPort.unsetServerPortURI() ;
		}
		if (localClientPort != null) {
			localClientPort.unsetClientPortURI() ;
			localClientPort.unsetServerPortURI() ;
		}
		if (localServerPort != null && localClientPort != null) {
			// local disconnection
			if (AbstractCVM.DEBUG) {
				System.out.println("local disconnection of client " + 
						clientPortURI + " to server " + serverPortURI) ;
			}
			connector.disconnect() ;
		} else if (localServerPort != null && localClientPort == null) {
			if (AbstractCVM.DEBUG) {
				System.out.println("remote disconnection of remote client " + 
						clientPortURI + " to local server " + serverPortURI) ;
			}
			if (localServerPort instanceof DataOfferedI.PullI) {
				if (AbstractCVM.DEBUG) {
					System.out.println("disconnecting data interfaces...");
				}
				((DataConnectorI)connector).disconnectServer() ;
			} else if (localServerPort instanceof DataTwoWayI.PullI) {
				((DataTwoWayConnectorI)connector).disconnectServer() ;
			} else if (localServerPort instanceof TwoWayI) {
				((TwoWayConnectorI)connector).disconnectServer() ;
			} else if (localServerPort instanceof OfferedI) {
				// do nothing, passive connection
			} else {
				throw new Exception("unknown interface " + localServerPort) ;
			}
		} else if (localServerPort == null && localClientPort != null) {
			if (AbstractCVM.DEBUG) {
				System.out.println("remote disconnection of local client " +
						clientPortURI + " to remote server " + serverPortURI +
						" using connector " + connector) ;
			}
			connector.disconnectClient() ;
		} else { // localServerPort == null && localClientPort == null
			throw new Exception(
					"ConnectionBuilder: can't disconnect two remote components!") ;
		}
	}
}

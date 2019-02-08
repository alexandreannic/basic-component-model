package fr.upmc.components.ports;

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

import java.lang.reflect.Constructor;

import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.AbstractTwoWayConnector;
import fr.upmc.components.connectors.ConnectionBuilder;
import fr.upmc.components.connectors.ConnectorI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.interfaces.TwoWayI;

/**
 * The class <code>AbstractTwoWayPort</code> partially implements a two-way
 * port for components calling each others in a peer-to-peer fashion.
 *
 * <p><strong>Description</strong></p>
 * 
 * As components connected in a two-way peer-to-peer relationship where both
 * can call each others using the same methods, the two-way port is modeled
 * upon the inbound ports that admit multiple clients connected to them.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.connectors != null
 * </pre>
 * 
 * <p>Created on : 2012-01-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractTwoWayPort
extends		AbstractInboundPort
implements	TwoWayPortI
{
	private static final long	serialVersionUID = 1L;

	/** URI of the client port to which this port is connected.			*/
	protected String	clientPortURI ;
	/** URI of the server port to which this port is connected.			*/
	protected String	serverPortURI ;
	/** connector used to link with the other component.				*/
	protected TwoWayI	connector ;

	// FIXME: doesn't work when the two components implement the same
	// interface because the confusion between the sender and the receiver
	// persists in the connector.  A possible solution would be to accept
	// to have two connectors for two-way interfaces (including the data
	// and the data two-way interfaces.
	/** proxy used to call the other component through this port.		*/
	protected TwoWayI	out ;

	/**
	 * create and initialize two-way ports, with a given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null && uri != null
	 * pre	TwoWayI.class.isAssignableFrom(implementedInterface)
	 * post	this.implementedInterface.equals(implementedInterface)
	 * post	this.owner.equals(owner)
	 * post	this.uri.equals(uri)
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @throws Exception 
	 */
	public				AbstractTwoWayPort(
		String		uri,
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);

		assert	TwoWayI.class.isAssignableFrom(implementedInterface) ;

		this.out = null ;
	}

	/**
	 * create and initialize two-way ports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null ;
	 * pre	TwoWayI.class.isAssignableFrom(implementedInterface)
	 * post	this.uri != null
	 * post	this.implementedInterface.equals(implementedInterface)
	 * post	this.owner.equals(owner)
	 * </pre>
	 *
	 * @param implementedInterface
	 * @param owner
	 * @throws Exception 
	 */
	public				AbstractTwoWayPort(
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		this(AbstractPort.generatePortURI(implementedInterface),
			 implementedInterface, owner);

		// All two-way ports implement a two-way interface
		assert	TwoWayI.class.isAssignableFrom(implementedInterface) ;
	}

	// ------------------------------------------------------------------------
	// Registry management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.PortI#unpublishPort()
	 */
	@Override
	public void			unpublishPort() throws Exception
	{
		assert	this.isPublished ;
		assert	!this.connected() ;

		super.unpublishPort() ;
	}

	// ------------------------------------------------------------------------
	// Life-cycle management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.PortI#destroyPort()
	 */
	@Override
	public void			destroyPort() throws Exception
	{
		assert	!this.connected() ;
		assert	this.isPublished() ;

		super.destroyPort() ;

		assert	!this.isPublished() ;
	}

	// ------------------------------------------------------------------------
	// Connection management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#setClientPortURI(java.lang.String)
	 */
	@Override
	public void			setClientPortURI(String clientPortURI)
	throws Exception
	{
		this.clientPortURI = clientPortURI ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#setServerPortURI(java.lang.String)
	 */
	@Override
	public void			setServerPortURI(String serverPortURI)
	throws Exception
	{
		this.serverPortURI = serverPortURI ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#unsetClientPortURI()
	 */
	@Override
	public void			unsetClientPortURI() throws Exception
	{
		this.clientPortURI = null ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#unsetServerPortURI()
	 */
	@Override
	public void			unsetServerPortURI() throws Exception
	{
		this.serverPortURI = null ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#getClientPortURI()
	 */
	@Override
	public String		getClientPortURI() throws Exception
	{
		assert	this.connected() ;

		return this.clientPortURI ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#getServerPortURI()
	 */
	@Override
	public String		getServerPortURI() throws Exception
	{
		assert	this.connected() ;

		return this.serverPortURI ;
	}

	/**
	 * @see fr.upmc.components.ports.OutboundPortI#getConnector()
	 */
	@Override
	public ConnectorI	getConnector() throws Exception
	{
		return (ConnectorI) this.connector ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * pre	!this.connectors.contains(c)
	 * post	this.connectors.contains(c)
	 * </pre>
	 * 
	 * @see fr.upmc.components.ports.OutboundPortI#setConnector(fr.upmc.components.connectors.ConnectorI)
	 */
	@Override
	public synchronized void 	setConnector(ConnectorI c)
	throws Exception
	{
		assert	c != null ;
		assert	this.connector != c ;

		this.connector = (TwoWayI) c ;
		this.notifyAll() ;

		assert	this.connected() && this.connector == c ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null
	 * pre	this.connected() && this.connector == c
	 * post	!this.connected() && this.connector != c
	 * </pre>
	 * 
	 * @see fr.upmc.components.ports.OutboundPortI#unsetConnector(fr.upmc.components.connectors.ConnectorI)
	 */
	@Override
	public synchronized void 	unsetConnector(ConnectorI c)
	throws Exception
	{
		assert	c != null ;
		assert	this.connected() && this.connector == c ;

		this.connector = null ;

		assert	!this.connected() && this.connector != c ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#connected()
	 */
	@Override
	public boolean		connected() throws Exception
	{
		return this.connector != null ;
	}

	/**
	 * @see fr.upmc.components.ports.TwoWayPortI#awaitConnection()
	 */
	@Override
	public void			awaitConnection() throws Exception
	{
		if (!this.connected()) {
			this.wait() ;
		}
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#doConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public void			doConnection(String otherPortURI, String ccname)
	throws Exception
	{
		assert	!this.connected() ;

		// FIXME: should use a proper state machine model to implement the
		// connection and disconnection protocol

		// As two-way connections assume that components are peers, they
		// are symmetric rather than assymetric.  Hence, the same code will
		// be executed on both sides: the code that appears here, in this class.
		// Therefore, we must make sure that both sides will use the same ports
		// to act as client and server ports.  The connection made here assumes
		// that the current port is the server port, and the other port is the
		// client.
		Class<?> cc = Class.forName(ccname) ;
		Constructor<?> c = cc.getConstructor(new Class<?>[]{}) ;
		ConnectorI connector = (ConnectorI) c.newInstance() ;
		ConnectionBuilder.SINGLETON.connectWith(this.getPortURI(),
												otherPortURI,
												connector) ;

		((AbstractTwoWayConnector)this.connector). obeyConnection(this, ccname) ;

		assert	this.connected() ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#obeyConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public void			obeyConnection(String otherPortURI, String ccname)
	throws Exception
	{
		assert	!this.connected() ;

		// FIXME: should use a proper state machine model to implement the
		// connection and disconnection protocol

		// See doConnection; here the connection is made symmetrically to the
		// one made in doConnection.
		Class<?> cc = Class.forName(ccname) ;
		Constructor<?> c = cc.getConstructor(new Class<?>[]{}) ;
		ConnectorI connector = (ConnectorI) c.newInstance() ;
		ConnectionBuilder.SINGLETON.connectWith(otherPortURI,
												this.getPortURI(),
												connector) ;

		assert	this.connected() ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#doDisconnection()
	 */
	@Override
	public void			doDisconnection() throws Exception
	{
		assert	this.connected() ;

		// FIXME: should use a proper state machine model to implement the
		// connection and disconnection protocol

		if (AbstractCVM.isDistributed) {
			((AbstractTwoWayConnector)this.connector).obeyDisconnection(this) ;
		}
		this.obeyDisconnection() ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#obeyDisconnection()
	 */
	@Override
	public void			obeyDisconnection() throws Exception
	{
		assert	this.connected() ;

		// FIXME: should use a proper state machine model to implement the
		// connection and disconnection protocol

		ConnectionBuilder.SINGLETON.disconnectWith(this.getServerPortURI(),
												   this.getClientPortURI(),
												   this.getConnector()) ;
	}

	/**
	 * @see fr.upmc.components.ports.TwoWayPortI#getOut()
	 */
	@Override
	public TwoWayI		getOut() throws Exception
	{
		return this.out ;
	}

	/**
	 * @see fr.upmc.components.ports.TwoWayPortI#setOut(fr.upmc.components.interfaces.TwoWayI)
	 */
	@Override
	public void			setOut(TwoWayI out) throws Exception
	{
		this.out = out ;
	}
}

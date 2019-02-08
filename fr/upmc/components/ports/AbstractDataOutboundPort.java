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

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.AbstractDataConnector;
import fr.upmc.components.connectors.ConnectionBuilder;
import fr.upmc.components.connectors.ConnectorI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The class <code>AbstractDataOutboundPort</code> partially implements an
 * outbound port for data exchanging components.
 *
 * <p><strong>Description</strong></p>
 * 
 * Data exchanging components focus their interaction on the exchange of
 * pieces of data rather than calling each others services.  Hence, the
 * required and offered interfaces merely implements a simple protocol in
 * terms of methods used to pass data from the provider to the clients.
 * But data exchanges can be done in two modes: pull (the primary one) and push.
 * For outbound port, representing interfaces through which a client calls the
 * provider, the port uses the required pull interface, that is also implemented
 * by the connector, while the port implements the required push interface
 * through which data can be received in push mode from the provider.
 * 
 * A concrete outbound connector must therefore implement the method
 * <code>receive</code> which will receive a piece of data as parameter
 * and pass it to the owner component.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	DataRequiredI.PullI.class.isAssignableFrom(this.implementedInterface)
 * invariant	DataRequiredI.PushI.class.isAssignableFrom(this.implementedPushInterface)
 * </pre>
 * 
 * <p>Created on : 2011-11-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractDataOutboundPort
extends		AbstractInboundPort	// for the push interface
implements	DataOutboundPortI
{
	private static final long serialVersionUID = 1L;

	/** push interface implemented by this port, to receive data from the provider. */
	protected final Class<?>	implementedPushInterface ;

	/** URI of the server port to which this port is connected.			*/
	protected String	serverPortURI ;
	/** connector used to link with the provider component.				*/
	protected RequiredI	connector ;

	public				AbstractDataOutboundPort(
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		super(implementedInterface, owner);
		throw new RuntimeException("AbstractDataOutboundPort: must use the " +
				"three or four parameters version of the constructor.") ;
	}

	public				AbstractDataOutboundPort(
		String		uri,
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);
		throw new RuntimeException("AbstractDataOutboundPort: must use the " +
				"three or four parameters version of the constructor.") ;
	}

	/**
	 * create and initialize a data putbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner != null
	 * pre	DataRequiredI.PullI.class.isAssignableFrom(implementedPullInterface)
	 * pre	DataRequiredI.PushI.class.isAssignableFrom(implementedPushInterface)
	 * post	this.uri.equals(uri)
	 * post	this.owner.equals(owner)
	 * post	this.implementedInterface.equals(implementedPullInterface)
	 * post	this.implementedPushInterface.equals(implementedPushInterface)
	 * </pre>
	 *
	 * @param uri						unique identifier of the port.
	 * @param implementedPullInterface	pull interface implemented by this port.
	 * @param implementedPushInterface	push interface implemented by this port.
	 * @param owner						component that owns this port.
	 * @throws Exception 
	 */
	public				AbstractDataOutboundPort(
		String		uri,
		Class<?>	implementedPullInterface,
		Class<?>	implementedPushInterface,
		ComponentI	owner
		) throws Exception
	{
		super(uri, implementedPullInterface, owner) ;
		// the implemented interfaces are coming from a data required interface
		assert DataRequiredI.PullI.class.
								isAssignableFrom(implementedPullInterface) ;
		assert DataRequiredI.PushI.class.
								isAssignableFrom(implementedPushInterface) ;

		this.implementedPushInterface = implementedPushInterface ;
	}

	/**
	 * create and initialize a data putbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	DataRequiredI.PullI.class.isAssignableFrom(implementedPullInterface)
	 * pre	DataRequiredI.PushI.class.isAssignableFrom(implementedPushInterface)
	 * post	this.uri != null
	 * post	this.owner.equals(owner)
	 * post	this.implementedInterface.equals(implementedPullInterface)
	 * post	this.implementedPushInterface.equals(implementedPushInterface)
	 * </pre>
	 *
	 * @param implementedPullInterface	pull interface implemented by this port.
	 * @param implementedPushInterface	push interface implemented by this port.
	 * @param owner
	 * @throws Exception 
	 */
	public				AbstractDataOutboundPort(
		Class<?>	implementedPullInterface,
		Class<?>	implementedPushInterface,
		ComponentI	owner
		) throws Exception
	{
		this(AbstractPort.generatePortURI(implementedPullInterface),
			 implementedPullInterface, implementedPushInterface, owner) ;
	}

	// ------------------------------------------------------------------------
	// Self-properties management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.AbstractPort#getImplementedInterface()
	 */
	@Override
	public Class<?> 	getImplementedInterface() throws Exception
	{
		// make sure this method is always used to get the pull interface
		return this.getImplementedPullInterface() ;
	}

	/**
	 * @see fr.upmc.components.ports.DataOutboundPortI#getImplementedPullInterface()
	 */
	@Override
	public Class<?>		getImplementedPullInterface() throws Exception
	{
		// the pull interface is stored as the original implemented interface.
		return super.getImplementedInterface() ;
	}

	/**
	 * @see fr.upmc.components.ports.DataOutboundPortI#getImplementedPushInterface()
	 */
	@Override
	public Class<?> 	getImplementedPushInterface() throws Exception
	{
		return this.implementedPushInterface ;
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

		return this.uri ;
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
	 * @see fr.upmc.components.ports.OutboundPortI#getConnector()
	 */
	@Override
	public ConnectorI	getConnector() throws Exception
	{
		assert	this.connected() ;

		return (ConnectorI) this.connector ;
	}

	/**
	 * @see fr.upmc.components.ports.OutboundPortI#setConnector(fr.upmc.components.connectors.ConnectorI)
	 */
	@Override
	public synchronized void	setConnector(ConnectorI c)
	throws Exception
	{
		assert	c != null ;
		assert	!this.connected()  ;

		if (AbstractCVM.DEBUG) {
			((AbstractComponent)this.owner).logMessage(
					"*** AbstractDataOutboundPort setting connector " +
																c.toString()) ;
		}

		this.connector = (RequiredI)c ;
		this.notifyAll() ;

		assert	this.connected() ;
	}

	/**
	 * @see fr.upmc.components.ports.OutboundPortI#unsetConnector(fr.upmc.components.connectors.ConnectorI)
	 */
	@Override
	public synchronized void	unsetConnector(ConnectorI c)
	throws Exception
	{
		assert	c != null ;
		assert	this.connected() ;
		assert	this.connector == c ;

		this.connector = null ;

		assert	!this.connected() ;
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
	 * @see fr.upmc.components.ports.OutboundPortI#awaitConnection()
	 */
	@Override
	public void			awaitConnection() throws Exception {
		if (!this.connected()) {
			this.wait() ;
		}
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.connected()
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.ports.AbstractInboundPort#doConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public void			doConnection(String otherPortURI, String ccname)
	throws Exception
	{
		assert	!this.connected() ;

		this.obeyConnection(otherPortURI, ccname) ;

		((AbstractDataConnector)this.connector).obeyConnection(this, ccname) ;

		assert	this.connected() ;
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.connected()
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.ports.AbstractInboundPort#obeyConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public void			obeyConnection(String otherPortURI, String ccname)
	throws Exception
	{
		assert	!this.connected() ;

		// FIXME: should use a proper state machine model to implement the
		// connection and disconnection protocol

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

		((AbstractDataConnector)this.connector).obeyDisconnection(this) ;
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

		assert	!this.connected() ;
	}

	// ------------------------------------------------------------------------
	// Request handling
	// ------------------------------------------------------------------------

	/**
	 * called by the requiring component in pull mode to trigger the obtaining
	 * of a piece of data from the offering one; this definition imposes the
	 * synchronized nature of the method, as it is called by the owner to get
	 * data from server components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.connected() ;
	 * post	true			// no precondition.
	 * </pre>
	 * 
	 * @throws Exception 
	 * 
	 * @see fr.upmc.components.interfaces.DataRequiredI.PullI#request()
	 */
	@Override
	public synchronized DataRequiredI.DataI	request()
	throws Exception
	{
		assert	this.connected() ;

		return ((DataRequiredI.PullI) this.connector).request() ;
	}
}

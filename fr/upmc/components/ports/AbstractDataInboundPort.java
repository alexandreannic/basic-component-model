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
import fr.upmc.components.AbstractComponent ;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.AbstractDataConnector;
import fr.upmc.components.connectors.ConnectionBuilder;
import fr.upmc.components.connectors.ConnectorI;
import fr.upmc.components.connectors.DataConnectorI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.interfaces.DataOfferedI;

/**
 * The class <code>AbstractDataInboundPort</code> partially implements an
 * inbound port for data exchanging components.
 *
 * <p><strong>Description</strong></p>
 * 
 * Data exchanging components focus their interaction on the exchange of
 * pieces of data rather than calling each others services.  Hence, the
 * required and offered interfaces merely implements a simple protocol in
 * terms of methods used to pass data from the provider to the clients.
 * But data exchanges can be done in two modes: pull (the primary one) and push.
 * For inbound port, representing interfaces through which a provider is called,
 * the port implements the offered pull interface, while the connector
 * implements the offered push interface through which data can be pushed
 * from the provider towards the client.
 * 
 * A concrete inbound connector must therefore implement the method
 * <code>get</code> which will ask the owner component for a piece of data
 * and provide as result.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.connectors != null
 * invariant	DataOfferedI.PullI.class.isAssignableFrom(this.implementedInterface)
 * invariant	DataOfferedI.PushI.class.isAssignableFrom(this.implementedPushInterface)
 * </pre>
 * 
 * <p>Created on : 2011-11-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractDataInboundPort
extends		AbstractInboundPort
implements	DataInboundPortI
{
	private static final long serialVersionUID = 1L;

	/** URI of the client port to which this port is connected.			*/
	protected String				clientPortURI ;
	/** connectors of this port towards the client components.				*/
	protected DataOfferedI.PushI	connector ;
	/** push interface implemented by this port, to send data to the client. */
	protected final Class<?>		implementedPushInterface ;

	public				AbstractDataInboundPort(
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		super(implementedInterface, owner) ;
		throw new RuntimeException("AbstractDataInboundPort: must use the " +
				"three or four parameters version of the constructor.") ;
	}

	public				AbstractDataInboundPort(
		String		uri,
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);
		throw new RuntimeException("AbstractDataInboundPort: must use the " +
				"three or four parameters version of the constructor.") ;
	}

	/**
	 * create and initialize data inbound ports, with a given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner != null
	 * pre	DataOfferedI.PullI.class.isAssignableFrom(implementedPullInterface)
	 * pre	DataOfferedI.PushI.class.isAssignableFrom(implementedPushInterface)
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
	public				AbstractDataInboundPort(
		String		uri,
		Class<?>	implementedPullInterface,
		Class<?>	implementedPushInterface,
		ComponentI	owner
		) throws Exception
	{
		super(uri, implementedPullInterface, owner) ;
		// the implemented interfaces are coming from a data offered interface
		assert DataOfferedI.PullI.class.
									isAssignableFrom(implementedPullInterface) ;
		assert DataOfferedI.PushI.class.
									isAssignableFrom(implementedPushInterface) ;

		this.implementedPushInterface = implementedPushInterface ;
	}

	/**
	 * create and initialize a data inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null
	 * pre	DataOfferedI.PullI.class.isAssignableFrom(implementedPullInterface)
	 * pre	DataOfferedI.PushI.class.isAssignableFrom(implementedPushInterface)
	 * post	this.uri != null
	 * post	this.owner.equals(owner)
	 * post	this.implementedInterface.equals(implementedPullInterface)
	 * post	this.implementedPushInterface.equals(implementedPushInterface)
	 * </pre>
	 *
	 * @param implementedPullInterface	pull interface implemented by this port.
	 * @param implementedPushInterface	push interface implemented by this port.
	 * @param owner						component that owns this port.
	 * @throws Exception 
	 */
	public				AbstractDataInboundPort(
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
	public Class<?>		getImplementedInterface() throws Exception
	{
		// make sure this method is always used to get the pull interface
		return this.getImplementedPullInterface() ;
	}

	/**
	 * @see fr.upmc.components.ports.DataInboundPortI#getImplementedPullInterface()
	 */
	@Override
	public Class<?>		getImplementedPullInterface() throws Exception
	{
		// the pull interface is stored as the original implemented interface.
		return super.getImplementedInterface() ;
	}

	/**
	 * @see fr.upmc.components.ports.DataInboundPortI#getImplementedPushInterface()
	 */
	@Override
	public Class<?>		getImplementedPushInterface() throws Exception
	{
		return this.implementedPushInterface ;
	}

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
	 * @see fr.upmc.components.ports.PortI#unsetClientPortURI()
	 */
	@Override
	public void			unsetClientPortURI() throws Exception
	{
		this.clientPortURI = null ;
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

		return this.uri ;
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
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null && !this.connected()
	 * post	this.connected() && this.connector == c
	 * </pre>
	 * 
	 * @see fr.upmc.components.ports.DataInboundPortI#setConnector(fr.upmc.components.connectors.DataConnectorI)
	 */
	@Override
	public synchronized void	setConnector(DataConnectorI c)
	throws Exception
	{
		assert	c != null ;
		assert	!this.connected() ;

		if (AbstractCVM.DEBUG) {
			((AbstractComponent)this.owner).logMessage(
					"*** AbstractDataInboundPort setting connector " +
																c.toString()) ;
		}
		this.connector = (DataOfferedI.PushI) c ;
		this.notifyAll() ;

		assert	this.connected() && this.connector == c ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null && this.connector == c && this.connected()
	 * post	!this.connected() && this.connector != c
	 * </pre>
	 * 
	 * @see fr.upmc.components.ports.DataInboundPortI#unsetConnector(fr.upmc.components.connectors.DataConnectorI)
	 */
	@Override
	public synchronized void	unsetConnector(DataConnectorI c)
	throws Exception
	{
		assert	c != null ;
		assert	this.connector == c ;
		assert	this.connected() ;

		this.connector = null ;

		assert	!this.connected() && this.connector != c ;
	}

	/**
	 * @see fr.upmc.components.ports.DataInboundPortI#getConnector()
	 */
	@Override
	public ConnectorI	getConnector() throws Exception
	{
		return (ConnectorI) this.connector ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#connected()
	 */
	@Override
	public boolean		connected() throws Exception
	{
		return this.connector != null ;
	}

	/**
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
		ConnectionBuilder.SINGLETON.connectWith(this.getPortURI(),
												otherPortURI,
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

		assert	!this.connected() ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractInboundPort#obeyDisconnection()
	 */
	@Override
	public void			obeyDisconnection() throws Exception
	{
		assert	this.connected() ;

		ConnectionBuilder.SINGLETON.disconnectWith(this.getServerPortURI(),
												   this.getClientPortURI(),
												   this.getConnector()) ;

		assert	!this.connected() ;
	}

	// ------------------------------------------------------------------------
	// Request handling
	// ------------------------------------------------------------------------

	/**
	 * sends data to all of the connected components in the push mode; this
	 * definition imposes the synchronised nature of the method that is called
	 * by the owner component to push data to all of its clients.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.connected()
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @throws Exception 
	 * 
	 * @see fr.upmc.components.interfaces.DataOfferedI.PushI#send(fr.upmc.components.interfaces.DataOfferedI.DataI)
	 */
	@Override
	public synchronized void	send(DataOfferedI.DataI d)
	throws Exception
	{
		assert	this.connected() ;

		if (AbstractCVM.DEBUG) {
			((AbstractComponent)this.owner).logMessage(
						"AbstractDataInboundPort sends... " + d.toString() +
						" ...on connector " + connector.toString()) ;
		}
		this.connector.send(d) ;
		if (AbstractCVM.DEBUG) {
			System.out.println("...AbstractDataInboundPort sent! " +
							   d.toString()) ;
		}
	}
}

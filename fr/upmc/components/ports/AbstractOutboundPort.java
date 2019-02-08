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
import fr.upmc.components.connectors.ConnectionBuilder;
import fr.upmc.components.connectors.ConnectorI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The class <code>AbstractOutboundPort</code> partially implements an outbound
 * port which implements the required interface of the owning component so
 * that it can call its providers through this port.
 *
 * <p><strong>Description</strong></p>
 * 
 * A concrete port class must implement the required interface of the component
 * with methods that call the corresponding services of their provider
 * component using the connector..
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.connector != null
 * invariant	RequiredI.class.isAssignableFrom(this.implementedInterface)
 * </pre>
 * 
 * <p>Created on : 2011-11-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractOutboundPort
extends		AbstractPort
implements	OutboundPortI
{
	/** URI of the server port to which this port is connected.			*/
	protected String	serverPortURI ;
	/** connector used to link with the provider component.				*/
	protected RequiredI	connector ;

	/**
	 * create and initialize outbound ports, with a given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null && uri != null
	 * pre	RequiredI.class.isAssignableFrom(implementedInterface)
	 * post	this.implementedInterface.equals(implementedInterface)
	 * post	this.owner.equals(owner)
	 * post	this.uri.equals(uri)
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 */
	public				AbstractOutboundPort(
		String		uri,
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		super(uri, implementedInterface, owner) ;

		// All outbound ports implement an required interface
		assert	RequiredI.class.isAssignableFrom(implementedInterface) ;
	}

	/**
	 * create and initialize outbound ports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null
	 * pre	RequiredI.class.isAssignableFrom(implementedInterface)
	 * post	this.uri != null
	 * post	this.implementedInterface.equals(implementedInterface)
	 * post	this.owner.equals(owner)
	 * </pre>
	 *
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 */
	public				AbstractOutboundPort(
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		this(AbstractPort.generatePortURI(implementedInterface),
			 implementedInterface, owner) ;
 	}

	// ------------------------------------------------------------------------
	// Self-properties management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.AbstractPort#setServerPortURI(java.lang.String)
	 */
	@Override
	public void			setServerPortURI(String serverPortURI)
	{
		this.serverPortURI = serverPortURI ;
	}

	/**
	 * @see fr.upmc.components.ports.AbstractPort#unsetServerPortURI()
	 */
	@Override
	public void			unsetServerPortURI() throws Exception
	{
		this.serverPortURI = null ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#getClientPortURI()
	 */
	@Override
	public String		getClientPortURI()
	throws Exception
	{
		assert	this.connected() ;

		return this.uri ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#getServerPortURI()
	 */
	@Override
	public String		getServerPortURI()
	throws Exception
	{
		assert	this.connected() ;

		return this.serverPortURI ;
	}

	// ------------------------------------------------------------------------
	// Registry management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.AbstractPort#publishPort()
	 */
	@Override
	public void			publishPort() throws Exception
	{
		// an outbound port never needs to be published distributedly
		super.localPublishPort() ;
	}

	// ------------------------------------------------------------------------
	// Connection management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.OutboundPortI#getConnector()
	 */
	@Override
	public ConnectorI	getConnector()
	throws Exception
	{
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
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.connected()
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.ports.PortI#doConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public void			doConnection(String otherPortURI, String ccname)
	throws Exception
	{
		assert	!this.connected() ;

		// FIXME: should use a proper state machine model to implement the
		// connection and disconnection protocol

		// In a simple client/server connection, where a plain outbound port is
		// connected to a plain inbound port, be it remote or local, the
		// connection is done one way on the client (outbound port) side,
		// so we need only to connect this side.
		this.obeyConnection(otherPortURI,ccname) ;

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
	 * @see fr.upmc.components.ports.PortI#obeyConnection(java.lang.String, java.lang.String)
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
	 * @see fr.upmc.components.ports.OutboundPortI#awaitConnection()
	 */
	@Override
	public void			awaitConnection() throws Exception
	{
		if (!this.connected()) {
			this.wait() ;
		}
	}

	/**
	 * @see fr.upmc.components.ports.PortI#doDisconnection()
	 */
	@Override
	public void			doDisconnection() throws Exception
	{
		assert	this.connected() ;

		// FIXME: should use a proper state machine model to implement the
		// connection and disconnection protocol

		// In a simple client/server connection, where a plain outbound port is
		// connected to a plain inbound port, be it remote or local, the
		// connection is done one way on the client (outbound port) side,
		// so we need only to disconnect this side.
		this.obeyDisconnection() ;

		assert	!this.connected() ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#obeyDisconnection()
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
}

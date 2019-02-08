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

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.interfaces.ComponentImplementedI;
import fr.upmc.components.interfaces.OfferedI;

import java.rmi.server.UnicastRemoteObject;

/**
 * The class <code>AbstractInboundPort</code> partially implements an inbound
 * port which implements the offered interface of the provider component so
 * that the provider can be called through this port.
 *
 * <p><strong>Description</strong></p>
 * 
 * A concrete port class must implement the offered interface of the component
 * with methods that call the corresponding implementation services of their
 * owner component, paying attention to the discipline (synchronised, ...)
 * with which these calls must be made for the given implementation of the
 * component.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	OfferedI.class.isAssignableFrom(this.implementedInterface)
 * </pre>
 * 
 * <p>Created on : 2011-11-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractInboundPort
extends		UnicastRemoteObject
implements	InboundPortI
{
	// ------------------------------------------------------------------------
	// Note: as this class cannot extend AbstractPort, everything from that
	// class must be copied here.
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Port instance variables and constructors
	// ------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;

	/** the unique identifier used to publish this entry point.				*/
	protected final String		uri ;
	/** the interface implemented by this port.								*/
	protected final Class<?>	implementedInterface ;
	/** the component owning this port.										*/
	protected final ComponentI	owner ;
	/** the port has been locally published.								*/
	protected boolean			isPublished = false ;
	/** the port has been distributedly published.							*/
	protected boolean			isDistributedlyPublished = false ;

	/**
	 * create and initialize inbound ports, with a given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner != null
	 * pre	OfferedI.class.isAssignableFrom(implementedInterface)
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
	public				AbstractInboundPort(
		String		uri,
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		super() ;
		assert	uri != null && owner != null ;
		assert	ComponentImplementedI.class.
									isAssignableFrom(implementedInterface) ;

		this.uri = uri ;
		this.implementedInterface = implementedInterface ;
		this.owner = owner ;

		// All inbound ports implement an offered interface, except
		// subclasses that inherits from AbstractDataOutboundPort
		// for which the inbound behavior comes from the push interface
		// and not the RequiredI implemented interface
		assert	OfferedI.class.isAssignableFrom(implementedInterface)
				|| this instanceof AbstractDataOutboundPort ;
	}

	/**
	 * create and initialize inbpund ports with an automatically generated URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	OfferedI.class.isAssignableFrom(implementedInterface)
	 * post	this.uri != null
	 * post	this.implementedInterface.equals(implementedInterface)
	 * post	this.owner.equals(owner)
	 * </pre>
	 *
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @throws Exception 
	 */
	public				AbstractInboundPort(
		Class<?>	implementedInterface,
		ComponentI	owner
		) throws Exception
	{
		this(AbstractPort.generatePortURI(implementedInterface),
			 implementedInterface, owner) ;

		// All inbound ports implement an offered interface
		assert	OfferedI.class.isAssignableFrom(implementedInterface) ;
	}

	// ------------------------------------------------------------------------
	// Self-properties management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.PortI#getOwner()
	 */
	@Override
	public ComponentI	getOwner() throws Exception
	{
		return this.owner ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#getImplementedInterface()
	 */
	@Override
	public Class<?>		getImplementedInterface() throws Exception
	{
		return this.implementedInterface;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#getPortURI()
	 */
	@Override
	public String		getPortURI() throws Exception
	{
		return this.uri ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#setClientPortURI(java.lang.String)
	 */
	@Override
	public void			setClientPortURI(String clientPortURI)
	throws Exception
	{
		// Inbound ports do not know their client port, as they may have
		// many clients.
	}

	/**
	 * @see fr.upmc.components.ports.PortI#setServerPortURI(java.lang.String)
	 */
	@Override
	public void			setServerPortURI(String serverPortURI)
	throws Exception
	{
		// Do nothing, this is their own port URI.
	}

	/**
	 * @see fr.upmc.components.ports.PortI#unsetClientPortURI()
	 */
	@Override
	public void			unsetClientPortURI() throws Exception
	{
		// Inbound ports do not know their client port, as they may have
		// many clients.
	}

	/**
	 * @see fr.upmc.components.ports.PortI#unsetServerPortURI()
	 */
	@Override
	public void			unsetServerPortURI() throws Exception
	{
		// Do nothing, this is their own port URI.
	}

	/**
	 * @see fr.upmc.components.ports.PortI#getClientPortURI()
	 */
	@Override
	public String		getClientPortURI() throws Exception
	{
		assert	this.connected() ;

		return "" ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#getServerPortURI()
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
	 * @return the isPublished
	 */
	public boolean		isPublished() {
		return this.isPublished;
	}

	/**
	 * @return the isDistributedlyPublished
	 */
	public boolean		isDistributedlyPublished() {
		return this.isDistributedlyPublished;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#localPublishPort()
	 */
	@Override
	public void			localPublishPort() throws Exception
	{
		assert	!this.isPublished() && !this.isDistributedlyPublished() ;

		AbstractCVM.localPublishPort(this) ;
		this.isPublished = true ;
		this.isDistributedlyPublished = false ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#publishPort()
	 */
	@Override
	public void			publishPort() throws Exception
	{
		assert	!this.isPublished() && !this.isDistributedlyPublished() ;

		AbstractDistributedCVM.publishPort((OfferedI)this) ;
		this.isPublished = true ;
		this.isDistributedlyPublished = true ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#unpublishPort()
	 */
	@Override
	public void			unpublishPort() throws Exception
	{
		assert	this.isPublished ;

		if (this.isDistributedlyPublished) {
			AbstractDistributedCVM.unpublishPort((OfferedI) this) ;
		} else {
			AbstractCVM.localUnpublishPort(this) ;
		}
		this.isPublished = false ;
		this.isDistributedlyPublished = false ;
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
		assert	this.isPublished() ;

		this.unpublishPort() ;
		this.owner.removePort(this) ;

		assert	!this.isPublished() ;
	}

	// ------------------------------------------------------------------------
	// Connection management
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.ports.PortI#connected()
	 */
	@Override
	public boolean		connected() throws Exception
	{
		// always return true, as an inbound port do not know if it is
		// connected or not, and don't really care anyway.
		return true;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#doConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public void			doConnection(String otherPortURI, String ccname)
	throws Exception
	{
		throw new Error("Attempt to connect a server component port "
						+ this.uri
						+ " to a client component port " + otherPortURI
						+ " from the server side; should be done from"
						+ " the client side!") ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#obeyConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public void			obeyConnection(String otherPortURI, String ccname)
	throws Exception
	{
		throw new Error("Attempt to connect a server component port "
						+ this.uri
						+ " to a client component port " + otherPortURI
						+ " from the server side; should be done from"
						+ " the client side!") ;
	}

	/**
	 * @see fr.upmc.components.ports.PortI#doDisconnection()
	 */
	@Override
	public void			doDisconnection() throws Exception
	{
		assert	this.connected() ;

		throw new Error("Attempt to disconnect a server component port "
						+ this.uri + " from a client component port"
						+ " but from the server side; should be done from"
						+ " the client side!") ;
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

		// As inbound ports do not hold data about their clients, nothing needs
		// to be done when disconnecting.
	}
}

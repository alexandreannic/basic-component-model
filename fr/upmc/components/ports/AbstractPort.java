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

/**
 * The class <code>AbstractPort</code> represents the basic properties and
 * behaviors of ports in the component model.
 *
 * <p><strong>Description</strong></p>
 * 
 * A port implement an interface on behalf of a component that owns it.  The
 * port is the entity that is seen from other components when connecting to
 * each others using connectors.  Hence, ports are used entry and exit points
 * in components to handle calls or data exchanges among them. 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.uri != null && this.owner != null
 * </pre>
 * 
 * <p>Created on : 2012-01-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractPort
implements	PortI
{
	// ------------------------------------------------------------------------
	// Port unique identifier management
	// ------------------------------------------------------------------------

	/**
	 * generate a unique identifier for the port which has the interface
	 * name as prefix.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param implementedInterface	interface to be implemented by the port.
	 * @return						a distributed system-wide unique id.
	 */
	public static String	generatePortURI(Class<?> implementedInterface) {
		return implementedInterface.getName() + "-" + generatePortURI() ;
	}

	/**
	 * generate a unique identifier for the port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return						a distributed system-wide unique id.
	 */
	public static String	generatePortURI() {
		// see http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq
		return java.util.UUID.randomUUID().toString() ;
	}

	// ------------------------------------------------------------------------
	// Port instance variables and constructors
	// ------------------------------------------------------------------------

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
	 * create and initialise a port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner != null
	 * pre	ComponentImplementedI.class.isAssignableFrom(implementedInterface)
	 * post	this.owner.equals(owner)
	 * post	this.uri.equals(uri)
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @throws Exception 
	 */
	public				AbstractPort(
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
	}

	/**
	 * create and initialise a port with an automatically generated URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null
	 * pre	ComponentImplementedI.class.isAssignableFrom(implementedInterface)
	 * post	this.owner.equals(owner)
	 * post	this.uri.equals(uri)
	 * </pre>
	 *
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @throws Exception 
	 */
	public				AbstractPort(
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
		// Do nothing, by default.
	}

	/**
	 * @see fr.upmc.components.ports.PortI#setServerPortURI(java.lang.String)
	 */
	@Override
	public void			setServerPortURI(String serverPortURI)
	throws Exception
	{
		// Do nothing, by default.
	}

	/**
	 * @see fr.upmc.components.ports.PortI#unsetClientPortURI()
	 */
	@Override
	public void			unsetClientPortURI() throws Exception
	{
		// Do nothing, by default.
	}

	/**
	 * @see fr.upmc.components.ports.PortI#unsetServerPortURI()
	 */
	@Override
	public void			unsetServerPortURI() throws Exception
	{
		// Do nothing, by default.
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
		assert	!this.connected() ;

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
		assert	!this.connected() ;
		assert	this.isPublished() ;

		this.unpublishPort() ;
		this.owner.removePort(this) ;

		assert	!this.isPublished() ;
	}
}

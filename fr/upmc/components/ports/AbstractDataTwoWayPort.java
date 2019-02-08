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
import fr.upmc.components.interfaces.DataTwoWayI;

/**
 * The class <code>AbstractDataTwoWayPort</code> partially implements a data
 * two-way port for components exchanging data with each others in a
 * peer-to-peer fashion.
 *
 * <p><strong>Description</strong></p>
 * 
 * Strictly speaking, the class is not abstract.  But the method
 * <code>send</code> (resp. <code>request</code>) is used both to
 * send (respectively request) data to (resp. from) the other component
 * and to receive (respectively serve the request for) data from the other
 * component.  As in this case, the port must pass (respectively request)
 * the data to its owner component, it must know what method of its owner
 * component to call, a knowledge that depends upon the application
 * component, so the two methods must be extended in an application-specific
 * port to that end.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	DataTwoWayI.class.isAssignableFrom(this.implementedInterface)
 * </pre>
 * 
 * <p>Created on : 2012-01-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractDataTwoWayPort
extends		AbstractTwoWayPort
implements	DataTwoWayPortI
{
	private static final long serialVersionUID = 1L;

	/** push interface implemented by this port, to send data to the client. */
	protected final Class<?>	implementedPushInterface ;

	public 				AbstractDataTwoWayPort(
		String				uri,
		Class<?>			implementedInterface,
		ComponentI			owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);
		throw new Exception("AbstractTwoWayPort: must use the " +
					"three or four parameters version of the constructor.") ;
	}
	
	public				AbstractDataTwoWayPort(
		Class<?>			implementedInterface,
		ComponentI			owner
		) throws Exception
	{
		this(AbstractPort.generatePortURI(implementedInterface),
			 implementedInterface, owner);
		throw new Exception("AbstractTwoWayPort: must use the " +
					"three or four parameters version of the constructor.") ;
	}

	/**
	 * create and initialise data two-way ports, with a given uri.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner != null
	 * pre	DataTwoWayI.PullI.class.isAssignableFrom(implementedPullInterface)
	 * pre	DataTwoWayI.PushI.class.isAssignableFrom(implementedPushInterface)
	 * post	this.uri.equals(uri)
	 * post	this.owner.equals(owner)
	 * </pre>
	 *
	 * @param uri						unique identifier of the port.
	 * @param implementedPullInterface	pull interface implemented by this port.
	 * @param implementedPushInterface	push interface implemented by this port.
	 * @param owner						component that owns this port.
	 * @throws Exception 
	 */
	public 				AbstractDataTwoWayPort(
		String				uri,
		Class<?>			implementedPullInterface,
		Class<?>			implementedPushInterface,
		ComponentI			owner
		) throws Exception
	{
		super(uri, implementedPullInterface, owner);

		// All data two-way ports implement a data two-way interface
		assert	DataTwoWayI.PullI.class.
								isAssignableFrom(implementedPullInterface) ;
		assert	DataTwoWayI.PushI.class.
								isAssignableFrom(implementedPushInterface) ;

		this.implementedPushInterface = implementedPushInterface ;
	}

	/**
	 * create and initialise data two-way ports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null
	 * pre	DataTwoWayI.PullI.class.isAssignableFrom(implementedPullInterface)
	 * pre	DataTwoWayI.PushI.class.isAssignableFrom(implementedPushInterface)
	 * post	this.uri != null
	 * post	this.owner.equals(owner)
	 * </pre>
	 *
	 * @param implementedPullInterface	pull interface implemented by this port.
	 * @param implementedPushInterface	push interface implemented by this port.
	 * @param owner						component that owns this port.
	 * @throws Exception 
	 */
	public				AbstractDataTwoWayPort(
		Class<?>			implementedPullInterface,
		Class<?>			implementedPushInterface,
		ComponentI			owner
		) throws Exception
	{
		this(AbstractPort.generatePortURI(implementedPullInterface),
			 implementedPullInterface, implementedPushInterface, owner);
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
	 * @see fr.upmc.components.ports.DataTwoWayPortI#getImplementedPullInterface()
	 */
	@Override
	public Class<?>		getImplementedPullInterface() throws Exception
	{
		// the pull interface is stored as the original implemented interface.
		return super.getImplementedInterface() ;
	}

	/**
	 * @see fr.upmc.components.ports.DataTwoWayPortI#getImplementedPushInterface()
	 */
	@Override
	public Class<?>		getImplementedPushInterface() throws Exception
	{
		return this.implementedPushInterface ;
	}

	// ------------------------------------------------------------------------
	// Request handling
	// ------------------------------------------------------------------------

	/**
	 * sneds and receives a piece of data from another component in push mode.
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
	 * @see fr.upmc.components.interfaces.DataTwoWayI.PushI#send(fr.upmc.components.interfaces.DataTwoWayI.DataI, fr.upmc.components.ports.DataTwoWayPortI)
	 */
	@Override
	public void			send(DataTwoWayI.DataI d, DataTwoWayPortI sender)
	throws Exception
	{
		assert	sender == this && this.connected() ;

		if (sender == this) {
			((DataTwoWayI.PushI) this.connector).send(d, sender) ;
		} else {
			throw new Exception(
					"port must say how to make the owner receiving data.") ;
		}
	}

	/**
	 * requests a data from a peer in pull mode, assuming there is only one peer
	 * connected through this port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.connectors.size() == 1
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.interfaces.DataTwoWayI.PullI#request(fr.upmc.components.ports.DataTwoWayPortI)
	 */
	@Override
	public DataTwoWayI.DataI	request(DataTwoWayPortI requirer)
	throws Exception
	{
		assert	this.connected() ;

		if (this == requirer) {
			return ((DataTwoWayI.PullI)this.connector).request(requirer) ;
		} else {
			throw new Exception(
						"port must say how to handle requests to owner.") ;
		}
	}
}

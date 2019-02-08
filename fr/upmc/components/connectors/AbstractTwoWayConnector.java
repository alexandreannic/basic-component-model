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

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.components.interfaces.TwoWayI;
import fr.upmc.components.ports.PortI;
import fr.upmc.components.ports.TwoWayPortI;;

/**
 * The class <code>AbstractTwoWayConnector</code> partially implements a
 * basic connector between components calling each others services in a peer
 * to peer way rather than in a client-provider relationship.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2012-01-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractTwoWayConnector
extends		AbstractConnector
implements	TwoWayConnectorI
{
	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	peer1 instanceof TwoWayI
	 * pre	peer2 instanceof TwoWayI
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#connect(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connect(OfferedI peer1, RequiredI peer2)
	throws Exception
	{
		assert	peer1 != null && peer2 != null ;
		assert	peer1 instanceof TwoWayI ;
		assert	peer2 instanceof TwoWayI ;
		assert	!this.connected() ;

		super.connect(peer1, peer2) ;
		// requiring's connector has been set in the previous superclass call
		((TwoWayPortI)this.offering).setConnector(this) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	peer1 instanceof TwoWayI
	 * pre	peer2 instanceof TwoWayI
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#connectClient(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connectClient(
		OfferedI peer1,
		RequiredI peer2
		) throws Exception
	{
		assert	peer1 != null && peer2 != null ;
		// the only reason to redefine this method is to test these
		assert	peer1 instanceof TwoWayI ;
		assert	peer2 instanceof TwoWayI ;
		assert	!this.connected() ;

		super.connectClient(peer1, peer2);

		assert	this.connected() ;
		assert	((PortI) peer2).connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.TwoWayConnectorI#connectServer(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connectServer(
		OfferedI peer1,
		RequiredI peer2
		) throws Exception
	{
		assert	peer1 != null && peer2 != null ;
		assert	peer1 instanceof TwoWayI ;
		assert	peer2 instanceof TwoWayI ;
		assert	!this.connected() ;

		this.offering = peer1 ;
		this.requiring = peer2 ;
		// the requiring side cannot be set because it is remote
		((TwoWayPortI)this.offering).setConnector(this) ;

		assert	this.connected() ;
		assert	((PortI) peer1).connected() ;

	}

	/**
	 * @see fr.upmc.components.connectors.AbstractConnector#disconnect()
	 */
	@Override
	public synchronized void	disconnect() throws Exception
	{
		assert	this.connected() ;
		assert	((PortI) this.offering).connected() &&
										((PortI) this.requiring).connected() ;

		super.disconnectClient() ;
		((TwoWayPortI)this.offering).unsetConnector(this) ;
		this.offering = null ;

		assert	!this.connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.TwoWayConnectorI#disconnectServer()
	 */
	@Override
	public synchronized void	disconnectServer() throws Exception
	{
		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;

		((TwoWayPortI)this.offering).unsetConnector(this) ;
		this.offering = null ;
		this.requiring = null ;

		assert	!this.connected() ;
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	peer1 instanceof TwoWayI
	 * pre	peer2 instanceof TwoWayI
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#reconnect(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	reconnect(
		OfferedI peer1,
		RequiredI peer2
		) throws Exception
	{
		assert	peer1 != null && peer2 != null ;
		// the only reason to redefine this method is to test these
		assert	peer1 instanceof TwoWayI ;
		assert	peer2 instanceof TwoWayI ;
		assert	this.connected() ;

		super.reconnect(peer1, peer2) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.AbstractConnector#reconnectClient(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	reconnectClient(
		OfferedI peer1,
		RequiredI peer2
		) throws Exception
	{
		assert	peer1 != null && peer2 != null ;
		// the only reason to redefine this method is to test these
		assert	peer1 instanceof TwoWayI ;
		assert	peer2 instanceof TwoWayI ;
		assert	this.connected() ;

		super.reconnectClient(peer1, peer2) ;

		assert	this.connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.TwoWayConnectorI#reconnectServer(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	reconnectServer(
		OfferedI peer1,
		RequiredI peer2
		) throws Exception
	{
		assert	peer1 != null && peer2 != null ;
		assert	peer1 instanceof TwoWayI ;
		assert	peer2 instanceof TwoWayI ;
		assert	this.connected() ;

		this.disconnectServer() ;
		this.connectServer(peer1, peer2) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;
	}
}

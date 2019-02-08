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

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.components.ports.DataInboundPortI;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>AbstractDataConnector</code> partially implements a basic
 * data connector between components exchanging data rather than calling each
 * others services.
 *
 * <p><strong>Description</strong></p>
 * 
 * This abstract data connector merely implements the translator methods
 * that mediate between data types from the required and the offered interfaces,
 * and the <code>connect</code> method to connect components through the
 * connector.
 * 
 * The <code>to</code> and <code>from</code> translating methods both assumes
 * that the actual class providing for the data to be exchanged implements
 * both <code>DataI</code> interfaces from the required and the offered
 * interfaces, so that a simple cast will do for the translation.
 * 
 * The <code>connect</code> method postpone to the base connector class the
 * implementation of a <code>setConnectorOnComponents</code> method the
 * setting of a reference to the connector in both the requiring and the
 * offering components.  Indeed, this method has to know the method implemented
 * by both components to do so, and because components may implement several
 * offered and required interfaces, there is no practical way to know the
 * signature of those setting methods in a generic way.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2011-11-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractDataConnector
extends		AbstractConnector
implements	DataConnectorI
{
	/**
	 * @see fr.upmc.components.connectors.DataConnectorI#required2offered(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public DataOfferedI.DataI required2offered(DataRequiredI.DataI d)
	{
		// the data class must implement both the RequiredActuatorI
		// and the OfferedActuatorI ActuatorDataI interfaces.
		return (DataOfferedI.DataI) d ;
	}

	/**
	 * @see fr.upmc.components.connectors.DataConnectorI#offered2required(fr.upmc.components.interfaces.DataOfferedI.DataI)
	 */
	@Override
	public DataRequiredI.DataI offered2required(DataOfferedI.DataI d)
	{
		// the data class must implement both the RequiredActuatorI
		// and the OfferedActuatorI ActuatorDataI interfaces.
		return (DataRequiredI.DataI) d ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	offering instanceof DataOfferedI.PullI
	 * pre	requiring instanceof DataRequiredI.PullI
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#connect(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connect(OfferedI offering, RequiredI requiring)
	throws Exception
	{
		assert	offering != null && requiring != null ;
		assert	!this.connected() ;
		assert	offering instanceof DataOfferedI.PullI ;
		assert	requiring instanceof DataRequiredI.PullI ;

		super.connect(offering, requiring) ;
		// requiring's connector has been set in the previous superclass call
		((DataInboundPortI)this.offering).setConnector(this) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	requiring instanceof DataRequiredI.PushI
	 * pre	offering instanceof DataOfferedI.PullI
	 * post	true				// no more postconditions.
	 * </pre>
	 * @throws Exception 
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#connectClient(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connectClient(
		OfferedI offering,
		RequiredI requiring
		) throws Exception
	{
		assert	offering != null && requiring != null ;
		// the only reason to redefine this method is to test these
		assert	requiring instanceof DataRequiredI.PushI ;
		assert	offering instanceof DataOfferedI.PullI ;
		assert	!this.connected() ;

		super.connectClient(offering, requiring) ;

		assert	this.connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.DataConnectorI#connectServer(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connectServer(
		OfferedI offering,
		RequiredI requiring
		) throws Exception
	{
		assert	offering != null && requiring != null ;
		assert	offering instanceof DataOfferedI.PullI ;
		assert	requiring instanceof DataRequiredI.PushI ;
		assert	!this.connected() ;

		this.offering = offering ;
		this.requiring = requiring ;
		// the requiring side cannot be set because it is remote
		((DataInboundPortI)this.offering).setConnector(this) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected()  ;
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
		((DataInboundPortI)this.offering).unsetConnector(this) ;
		this.offering = null ;

		assert	!this.connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.DataConnectorI#disconnectServer()
	 */
	@Override
	public synchronized void	disconnectServer() throws Exception
	{
		assert	this.connected() ;
		assert	((PortI) this.offering).connected()  ;

		((DataInboundPortI)this.offering).unsetConnector(this) ;
		this.offering = null ;

		assert	!this.connected() ;
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	requiring instanceof DataRequiredI.PushI
	 * pre	offering instanceof DataOfferedI.PullI
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#reconnect(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	reconnect(
		OfferedI offering,
		RequiredI requiring
		) throws Exception
	{
		assert	offering != null && requiring != null ;
		// the only reason to redefine this method is to test these
		assert	offering instanceof DataOfferedI.PullI ;
		assert	requiring instanceof DataRequiredI.PullI ;
		assert	this.connected() ;

		super.reconnect(offering, requiring) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	requiring instanceof DataRequiredI.PushI
	 * pre	offering instanceof DataOfferedI.PullI
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#reconnectClient(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	reconnectClient(
		OfferedI offering,
		RequiredI requiring
		) throws Exception
	{
		assert	offering != null && requiring != null ;
		// the only reason to redefine this method is to test these
		assert	requiring instanceof DataRequiredI.PushI ;
		assert	offering instanceof DataOfferedI.PullI ;
		assert	!this.connected() ;
		assert	((PortI) this.requiring).connected()  ;

		super.reconnectClient(offering, requiring);

		assert	this.connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	requiring instanceof DataRequiredI.PushI
	 * pre	offering instanceof DataOfferedI.PullI
	 * post	this.offering != null && this.requiring != null
	 * </pre>
	 * @throws Exception 
	 * 
	 * @see fr.upmc.components.connectors.AbstractConnector#reconnect(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	reconnectServer(
		OfferedI offering,
		RequiredI requiring
		) throws Exception
	{
		assert	offering != null && requiring != null ;
		assert	requiring instanceof DataRequiredI.PushI ;
		assert	offering instanceof DataOfferedI.PullI ;
		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;

		this.disconnectServer() ;
		this.connectServer(offering, requiring) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;
	}
}

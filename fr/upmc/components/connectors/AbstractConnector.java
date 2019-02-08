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
import fr.upmc.components.ports.OutboundPortI;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>AbstractConnector</code> partially implements an abstract
 * connector between two components by assuming that the offering component
 * implements the offered interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	offering != null && requiring != null
 * </pre>
 * 
 * <p>Created on : 2011-11-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	AbstractConnector
implements	ConnectorI
{
	/** is the connection going across different processes or not.		*/
	protected boolean	isRemote ;
	/** port of the component providing the service.					*/
	protected OfferedI	offering ;
	/** port of the component requiring the service.					*/
	protected RequiredI	requiring ;

	/**
	 * @return the isRemote
	 */
	@Override
	public boolean		isRemote() throws Exception
	{
		return this.isRemote;
	}

	/**
	 * @param isRemote the isRemote to set
	 */
	public void			setRemote(boolean isRemote) throws Exception {
		this.isRemote = isRemote;
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#connected()
	 */
	@Override
	public boolean		connected() throws Exception
	{
		return this.offering != null && this.requiring != null ;
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#connect(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connect(OfferedI offering, RequiredI requiring)
	throws Exception
	{
		assert	offering != null && requiring != null ;
		assert	!this.connected() ;

		this.offering = offering ;
		this.requiring = requiring ;
		((OutboundPortI)this.requiring).setConnector(this) ;

		assert	this.connected() ;
		assert	((PortI)this.requiring).connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#connectClient(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	connectClient(
		OfferedI offering,
		RequiredI requiring
		) throws Exception
	{
		assert	offering != null && requiring != null ;
		assert	!this.connected() ;

		this.offering = offering ;
		this.requiring = requiring ;
		((OutboundPortI)this.requiring).setConnector(this) ;

		assert	this.connected() ;
		assert	((PortI)this.requiring).connected() ;
		// the offering port cannot be tested here because it is remote.
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#disconnect()
	 */
	@Override
	public synchronized void	disconnect() throws Exception
	{
		assert	this.connected() ;
		assert	((PortI) this.offering).connected() &&
										((PortI) this.requiring).connected() ;

		((OutboundPortI)this.requiring).unsetConnector(this) ;
		this.offering = null ;
		this.requiring = null ;

		assert	!this.connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#disconnectClient()
	 */
	@Override
	public synchronized void	disconnectClient() throws Exception
	{
		assert	this.connected() ;
		assert	((PortI) this.requiring).connected() ;

		((OutboundPortI)this.requiring).unsetConnector(this) ;
		this.requiring = null ;

		assert	!this.connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#reconnect(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void reconnect(OfferedI offering, RequiredI requiring)
	throws Exception
	{
		assert	offering != null && requiring != null ;
		assert	this.connected() ;
		assert	((PortI) this.requiring).connected() &&
										((PortI) this.offering).connected() ;

		this.disconnect() ;
		this.connect(offering, requiring) ;

		assert	this.connected() ;
		assert	((PortI) this.offering).connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#reconnectClient(fr.upmc.components.interfaces.OfferedI, fr.upmc.components.interfaces.RequiredI)
	 */
	@Override
	public synchronized void	reconnectClient(
		OfferedI offering,
		RequiredI requiring
		) throws Exception
	{
		assert	offering != null && requiring != null ;
		assert	this.connected() ;
		assert	((PortI) this.requiring).connected()  ;

		this.disconnectClient() ;
		this.connectClient(offering, requiring) ;

		assert	this.connected() ;
		assert	((PortI) this.requiring).connected() ;
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#obeyConnection(fr.upmc.components.ports.PortI, java.lang.String)
	 */
	@Override
	public void				obeyConnection(PortI sender, String ccname)
	throws Exception
	{
		assert	sender != null ;
		assert	this.connected() ;

		if (this.isRemote()) {
			if (sender == this.requiring) {
				((PortI)this.offering).
								obeyConnection(sender.getPortURI(), ccname) ;
			} else {
				((PortI)this.requiring).
								obeyConnection(sender.getPortURI(), ccname) ;
			}
		}
	}

	/**
	 * @see fr.upmc.components.connectors.ConnectorI#obeyDisconnection(fr.upmc.components.ports.PortI)
	 */
	@Override
	public void				obeyDisconnection(PortI sender)
	throws Exception
	{
		assert	sender != null ;
		assert	this.connected() ;

		if (this.isRemote()) {
			if (sender == this.requiring) {
				((PortI)this.offering).obeyDisconnection() ;
			} else {
				((PortI)this.requiring).obeyDisconnection() ;
			}
		}
	}
}

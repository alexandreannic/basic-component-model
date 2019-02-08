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

/**
 * The interface <code>BasicDataConnectorI</code> is the generic interface for
 * connectors that mediate between requiring and offering components that
 * exchange data rather than calling services from each others.
 *
 * <p><strong>Description</strong></p>
 * 
 * When components exchange data rather than calling services from each others,
 * the offering component is considered as the producer of data while the
 * requiring one is considered as the consumer.    And the two interfaces are
 * assumed to be developed independently, the data interfaces in the required
 * and the offered interfaces need not be the same.  The connector is therefore
 * responsible for the implementation of translating methods <code>to</code>
 * and <code>from</code>.
 * 
 * <p>Created on : 2011-11-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		DataConnectorI
extends		ConnectorI,
			DataRequiredI.PullI,	// to be called from the client side
			DataOfferedI.PushI		// to be called from the provider side
{
	/**
	 * connect an offering component to a requiring one, taking care only of
	 * the server port (when the client-side is remote).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	offering != null && requiring != null
	 * pre	offering instanceof DataOfferedI.PullI
	 * pre	requiring instanceof DataRequiredI.PushI
	 * pre	!this.connected()
	 * post	((PortI) offering).connected()
	 * </pre>
	 *
	 * @param offering	in port of the component implementing the offered interface.
	 * @param requiring	out port of the component requiring the required interface.
	 * @throws Exception 
	 */
	public void			connectServer(OfferedI offering, RequiredI requiring)
	throws Exception ;

	/**
	 * disconnect a requiring component from an offering one, taking care only
	 * of the server port (when the client-side is remote).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.connected()
	 * post	!this.connected()
	 * </pre>
	 * @throws Exception 
	 */
	public void			disconnectServer() throws Exception ;

	/**
	 * disconnect and connect anew a requiring component to an offering one,
	 * taking care only of the server port (when the client-side is remote).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	offering != null && requiring != null
	 * pre	requiring instanceof DataRequiredI.PushI
	 * pre	offering instanceof DataOfferedI.PullI
	 * pre	this.connected()
	 * post	this.connected()
	 * post	offering.connected()
	 * </pre>
	 *
	 * @param offering	in port of the component implementing the offered interface.
	 * @param requiring	out port of the component requiring the required interface.
	 * @throws Exception 
	 */
	public void			reconnectServer(OfferedI offering, RequiredI requiring)
	throws Exception ;

	/**
	 * translate data as defined by the required interface into data as
	 * defined in the offered interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	d == null && return == null || d != null && return != null
	 * </pre>
	 *
	 * @param d	data to be translated
	 * @return	data resulting from the translation
	 */
	public DataOfferedI.DataI		required2offered(DataRequiredI.DataI d) ;

	/**
	 * translate data as defined by the offered interface into data as
	 * defined in the required interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	d == null && return == null || d != null && return != null
	 * </pre>
	 *
	 * @param d	data to be translated
	 * @return	data resulting from the translation
	 */
	public DataRequiredI.DataI		offered2required(DataOfferedI.DataI d) ;
}

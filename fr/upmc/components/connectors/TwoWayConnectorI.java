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

/**
 * The interface <code>TwoWayConnectorI</code> is the generic interface for
 * connectors that mediate between components exposing two-way interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * Two-way interfaces are the ones that are used to interconnect components
 * in a peer-to-peer way to exchange services or data.  In this case, both
 * components expose the same interface 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2012-01-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		TwoWayConnectorI
extends		ConnectorI,
			TwoWayI		// to be called from both sides
{
	/**
	 * connect a requiring component to a requiring one, taking care only of
	 * the server port (when the client-side is remote).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	offering != null && requiring != null
	 * pre	offering instanceof TwoWayI
	 * pre	requiring instanceof TwoWayI
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
	 * pre	offering instanceof TwoWayI
	 * pre	requiring instanceof TwoWayI
	 * pre	!this.connected()
	 * post	((PortI) offering).connected()
	 * </pre>
	 *
	 * @param offering	in port of the component implementing the offered interface.
	 * @param requiring	out port of the component requiring the required interface.
	 * @throws Exception 
	 */
	public void			reconnectServer(OfferedI offering, RequiredI requiring)
	throws Exception ;
}

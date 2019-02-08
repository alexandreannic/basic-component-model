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

import fr.upmc.components.interfaces.DataTwoWayI;
import fr.upmc.components.ports.DataTwoWayPortI;

/**
 * The class <code>DataTwoWayConnector</code> implements a standard connector
 * for components exchanging data in a peer-to-peer mode using standard data
 * two-way interfaces.
 * 
 * <p><strong>Description</strong></p>
 * 
 * Compared to data connectors, the data two way connector cannot know from
 * which component a call is coming, so this information must be provided as a
 * parameter of the call.  Hence, when processing an incoming call, the
 * connector finds the other component and relays it the call.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2012-10-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DataTwoWayConnector
extends		AbstractDataTwoWayConnector
{
	/**
	 * @see fr.upmc.components.interfaces.DataTwoWayI.PushI#send(fr.upmc.components.interfaces.DataTwoWayI.DataI, fr.upmc.components.ports.DataTwoWayPortI)
	 */
	@Override
	public void			send(DataTwoWayI.DataI d, DataTwoWayPortI sender)
	throws Exception
	{
		assert	this.connected() ;
		assert	sender != null ;

		if (sender == this.offering) {
			((DataTwoWayI.PushI) this.requiring).send(
												this.first2second(d), sender) ;
		} else {
			((DataTwoWayI.PushI) this.offering).send(
												this.second2first(d), sender) ;
		}
	}

	/**
	 * @see fr.upmc.components.interfaces.DataTwoWayI.PullI#request(fr.upmc.components.ports.DataTwoWayPortI)
	 */
	@Override
	public DataTwoWayI.DataI	request(DataTwoWayPortI requester)
	throws Exception
	{
		assert	this.connected() ;
		assert	requester != null ;

		if (requester == this.offering) {
			return this.second2first(
						((DataTwoWayI.PullI)this.requiring).request(requester)) ;
		} else {
			return this.first2second(
						((DataTwoWayI.PullI)this.offering).request(requester)) ;
		}
	}
}

package fr.upmc.components.interfaces;

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

import java.io.Serializable;
import fr.upmc.components.ports.DataTwoWayPortI;

/**
 * The interface <code>TwoWayDataI</code> enables two components to exchange
 * data in a peer-to-peer way, that is the two components implement the same
 * interface and have both the sender and receiver roles.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface defines the type of data it will allow to exchange and two
 * methods <code>send</code> and <code>receive</code>.  Components use the
 * <code>send</code> method to send a piece of data to the other component,
 * while they implement the <code>receive</code> to accept a piece of data
 * from the other component.  The connector implements the <code>send</code>
 * method in such a way to call the <code>receive</code> of the other component
 * to pass the data to it.
 * 
 * Contrary to the data exchange offered and required interfaces, that assume
 * that one component has the sender role and the other the receiver one, in
 * this case, the two have both roles, so they can send and receive data.
 * Hence, for the connector to be able to distinguish between the sender and
 * the receiver, the <code>send</code> and <code>receive</code> methods both
 * take a second arguments identifying the sender (the port thereof) in order
 * to implement symmetrically the passing of the piece of data in the correct
 * direction.
 * 
 * <p>Created on : 2012-01-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		DataTwoWayI
extends		TwoWayI
{

	/**
	 * The interface <code>TwoWayDataI.DataI</code> is a marker for data that
	 * can be exchanged between components using a two-way data interface.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2012-01-23</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	DataI extends Serializable { }

	/**
	 * The interface <code>TwoWayDataI.PushI</code> contains the signature of
	 * the method that is called by one component to send data to the other
	 * component.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 21 janv. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	PushI extends TwoWayI {

		/**
		 * used in push mode to send a piece of data to the other component.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	sender != null
		 * post	true			// no postcondition.
		 * </pre>
		 *
		 * @param d			piece of data sent to the other component.
		 * @param sender	port of the sender component.
		 * @throws Exception
		 */
		public void			send(DataI d, DataTwoWayPortI sender)
		throws Exception ;
	}

	/**
	 * The interface <code>TwoWayDataI.PullI</code> contains the signature of
	 * the method that is called by one component to get data from the other
	 * component.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 21 janv. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface		PullI extends TwoWayI {

		/**
		 * used in pull mode to request a piece of data from the other component.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	requirer != null
		 * post	true			// no postcondition.
		 * </pre>
		 *
		 * @param requirer	port of the component requiring the data
		 * @return			piece of data produced by the other component.
		 * @throws Exception
		 */
		public DataI		request(DataTwoWayPortI requirer)
		throws Exception ;
	}
}

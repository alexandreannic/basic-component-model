package fr.upmc.components.patterns.dconnection;

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
import fr.upmc.components.ports.AbstractPort;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>DynamicConnectionRequestBehaviour</code> implements
 * the a component dynamic interconnection pattern.  See the package
 * documentation for a complete description of the pattern and its
 * implementation.
 *
 * <p><strong>Description</strong></p>
 * 
 * The class implements both the required behaviours for the server side and
 * the client side.
 *  
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2013-03-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public abstract class	DynamicConnectionRequestBehaviour
{
	/** component holding this dynamic connection request behaviour.	 	*/
	protected ComponentI	embeddingComponent ;
	/** URI the server side port receiving dynamic connection requests.		*/
	protected String		dynamicConnectionRequestInboundPortURI ;

	/**
	 * on the server side, create the manager for dynamic connections, including
	 * the creation of the dynamic connection request inbound port through which
	 * requests for the creation of new connections are made.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param embeddingComponent	component holding this behaviour.
	 * @param dCRInboundPortURI		name of the server side port handling dynamic connection requests.
	 * @throws Exception
	 */
	public				DynamicConnectionRequestBehaviour(
		ComponentI embeddingComponent,
		String dCRInboundPortURI
		) throws Exception
	{
		super();
		this.embeddingComponent = embeddingComponent ;
		this.dynamicConnectionRequestInboundPortURI = dCRInboundPortURI ;

		this.embeddingComponent.addOfferedInterface(
									DynamicConnectionRequestI.class) ;

		DynamicConnectionRequestInboundPort p =
				new DynamicConnectionRequestInboundPort(
										dCRInboundPortURI,
										embeddingComponent,
										this) ;
		this.embeddingComponent.addPort(p) ;
		this.publishDynamicConnectionRequestPort(p) ;
	}

	/**
	 * on the client side, create the manager for dynamic connections.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param embeddingComponent	component holding this behaviour.
	 */
	public				DynamicConnectionRequestBehaviour(
		ComponentI embeddingComponent
		)
	{
		super() ;
		this.embeddingComponent = embeddingComponent ;

		this.embeddingComponent.addRequiredInterface(
											DynamicConnectionRequestI.class) ;
	}

	/**
	 * on the server side, publish the dynamic connection request inbound port,
	 * either as a local or as a remote port, this choice being deferred to the
	 * actual dynamic connection implementation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	p != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param p	the port to be published.
	 * @throws	Exception
	 */
	protected abstract void	publishDynamicConnectionRequestPort(
		DynamicConnectionRequestInboundPort p
		) throws Exception ;

	/**
	 * on the client side, initiate a dynamic connection request, by first
	 * connecting to the dynamic connection request port of the server and
	 * then: (1) request the URI of the server dynamic port, (2) create
	 * this client own port, (3) connect the client to the server and (4)
	 * send the URI of the client port to the server so that it can connect
	 * its own dynamic port to the port of the client.
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param otherDCRInboundPortURI	URI of the other component port.
	 * @return							URI of the client side new port.
	 * @throws							Exception
	 */
	public String		connectDynamicallyWith(
		String otherDCRInboundPortURI
		) throws Exception
	{
		// Connect to the other component using its dynamic connection request
		// inbound port.
		String dcropURI = AbstractPort.generatePortURI(
											DynamicConnectionRequestI.class) ;
		DynamicConnectionRequestOutboundPort dcrop =
			new DynamicConnectionRequestOutboundPort(dcropURI,
													 this.embeddingComponent) ;
		this.embeddingComponent.addPort(dcrop) ;
		AbstractCVM.localPublishPort(dcrop) ;
		dcrop.doConnection(
			otherDCRInboundPortURI,
			DynamicConnectionRequestConnector.class.getCanonicalName()) ;

		// Create the dynamic connection per se
		String otherDynamicPortURI = dcrop.requestNewPortURI() ;
		PortI dynamicPort = this.createAndPublishClientSideDynamicPort() ;
		this.embeddingComponent.addPort(dynamicPort) ;
		dynamicPort.doConnection(otherDynamicPortURI,
								 this.dynamicConnectorClassName()) ;

		// Remove the dynamic request connection
		dcrop.doDisconnection() ;
		AbstractCVM.localUnpublishPort(dcrop) ;
		this.embeddingComponent.removePort(dcrop) ;

		return dynamicPort.getPortURI() ;
	}

	/**
	 * on the client side, create and publish the new client side dynamically
	 * connected port, and therefore determine what type of port must be created.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the newly created port.
	 * @throws	Exception 
	 */
	protected abstract PortI	createAndPublishClientSideDynamicPort()
	throws Exception ;

	/**
	 * on the server side, create a new server side dynamic port, publish it
	 * using the method <code>createAndPublishDynamicPort</code> and return
	 * its unique identifier (URI).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the URI of the newly created port.
	 * @throws	Exception
	 */
	public String		requestNewPortURI() throws Exception
	{
		PortI p = this.createAndPublishServerSideDynamicPort() ;
		this.embeddingComponent.addPort(p) ;
		return p.getPortURI() ;
	}

	/**
	 * on the server side, create and publish the new server side dynamically
	 * connected port, and therefore determine what type of port must be
	 * created.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the newly created port.
	 * @throws	Exception 
	 */
	protected abstract PortI	createAndPublishServerSideDynamicPort()
	throws Exception ;

	/**
	 * provide the connector class name required to connect over the dynamic
	 * connection, the choice of the type of connector being deferred to the
	 * actual dynamic connection implementation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	a connector class name to be used in the dynamic connection.
	 * @throws	Exception 
	 */
	protected abstract String	dynamicConnectorClassName() throws Exception ;

	/**
	 * closes the dynamic connection request port, using the
	 * <code>unpublishDynamicConnectionRequestPort</code> to unpublish the
	 * port; after, no dynamic connection request can be done anymore.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws	Exception
	 */
	public void			closeDynamicConnectionRequest()
	throws Exception
	{
		PortI p = this.embeddingComponent.findPortFromURI(
								this.dynamicConnectionRequestInboundPortURI) ;
		p.unpublishPort() ;
		this.embeddingComponent.removePort(p) ;
	}
}

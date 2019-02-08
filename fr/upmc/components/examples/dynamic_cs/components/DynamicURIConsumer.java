package fr.upmc.components.examples.dynamic_cs.components;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.examples.basic_cs.components.URIConsumer;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>DynamicURIConsumer</code> is the dynamic version of the
 * component <code>URIConsumer</code> of the basic client/server example.  It
 * merely adds to the later the dynamic connection service of the interface
 * <code>DynamicallyConnectableComponentI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 14 mars 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DynamicURIConsumer
extends		URIConsumer
implements	DynamicallyConnectableI
{
	protected DynamicallyConnectableComponentInboundPort dccInboundPort ;

	public				DynamicURIConsumer(
		String portURIPrefix
		) throws Exception
	{
		super(portURIPrefix + "-URIConsumerOutboundPort") ;
		this.addOfferedInterface(DynamicallyConnectableComponentI.class) ;
		this.dccInboundPort =
			new DynamicallyConnectableComponentInboundPort(
												portURIPrefix + "-dcc", this) ;
		if (AbstractCVM.isDistributed) {
			this.dccInboundPort.publishPort() ;
		} else {
			this.dccInboundPort.localPublishPort() ;
		}
		this.addPort(dccInboundPort) ;
	}

	/**
	 * @see fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI#connectWith(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void			connectWith(
		String serverPortURI,
		String clientPortURI,
		String ccname
		) throws Exception
	{
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doConnection(serverPortURI, ccname) ;
	}

	/**
	 * @see fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI#disconnectWith(java.lang.String, java.lang.String)
	 */
	@Override
	public void			disconnectWith(
		String serverPortURI,
		String clientPortURI
		) throws Exception
	{
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doDisconnection() ;
	}

	/**
	 * @see fr.upmc.components.examples.basic_cs.components.URIConsumer#getURIandPrint()
	 */
	@Override
	public void			getURIandPrint() throws Exception
	{
		if (this.uriGetterPort.connected()) {
			super.getURIandPrint() ;
		} else {
			final URIConsumer uc = this ;
			this.scheduleTask(
					new ComponentTask() {
						@Override
						public void run() {
							try {
								uc.getURIandPrint() ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					1000, TimeUnit.MILLISECONDS) ;
		}
	}
}

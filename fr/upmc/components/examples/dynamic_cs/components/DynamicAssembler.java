package fr.upmc.components.examples.dynamic_cs.components;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentOutboundPort;
import fr.upmc.components.examples.basic_cs.URIServiceConnector;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;

/**
 * The class <code>DynamicAssembler</code> implements a compoennt that creates
 * the other components of the dynamic connection example, makes them
 * interconnect, and starts them.
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
public class			DynamicAssembler
extends		AbstractComponent
{
	protected AbstractCVM	cvm ;
	protected DynamicComponentCreationOutboundPort	portToConsumerJVM ;
	protected DynamicComponentCreationOutboundPort	portToProviderJVM ;

	protected String		consumerJVMURI ;
	protected String		providerJVMURI ;
	protected String		consumerPortURIPrefix ;
	protected String		providerInboundPortURI ;

	public				DynamicAssembler(
		AbstractCVM cvm,
		String consumerJVMURI,
		String providerJVMURI,
		String consumerPortURIPrefix,
		String providerInboundPortURI
		) throws Exception
	{
		super(true) ;
		this.cvm = cvm ;
		this.consumerJVMURI = consumerJVMURI ;
		this.providerJVMURI = providerJVMURI ;
		this.consumerPortURIPrefix = consumerPortURIPrefix ;
		this.providerInboundPortURI = providerInboundPortURI ;

		this.addRequiredInterface(DynamicComponentCreationI.class) ;
		this.addRequiredInterface(DynamicallyConnectableComponentI.class) ;
	}

	/**
	 * @see fr.upmc.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		try {
			this.portToConsumerJVM =
								new DynamicComponentCreationOutboundPort(this) ;
			this.portToConsumerJVM.localPublishPort() ;
			this.addPort(this.portToConsumerJVM) ;
			this.portToConsumerJVM.doConnection(
				this.consumerJVMURI +
					AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
					DynamicComponentCreationConnector.class.getCanonicalName()) ;

			this.portToProviderJVM =
								new DynamicComponentCreationOutboundPort(this) ;
			this.portToProviderJVM.localPublishPort() ;
			this.addPort(this.portToProviderJVM) ;
			this.portToProviderJVM.doConnection(
				this.providerJVMURI +
					AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
					DynamicComponentCreationConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			e.printStackTrace() ;
			throw new ComponentStartException() ;
		}

		super.start() ;
	}

	/**
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			if (this.portToConsumerJVM.connected()) {
				this.portToConsumerJVM.doDisconnection() ;
			}
			if (this.portToProviderJVM.connected()) {
				this.portToProviderJVM.doDisconnection() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException() ;
		}

		super.shutdown();
	}

	public void			deploy() throws Exception
	{
		this.portToProviderJVM.createComponent(
			DynamicURIProvider.class.getCanonicalName(),
			new Object[]{"myURI", this.providerInboundPortURI, true}) ;
		this.portToConsumerJVM.createComponent(
			DynamicURIConsumer.class.getCanonicalName(),
			new Object[]{this.consumerPortURIPrefix}) ;

		DynamicallyConnectableComponentOutboundPort p =
						new DynamicallyConnectableComponentOutboundPort(this) ;
		this.addPort(p) ;
		p.localPublishPort() ;
		p.doConnection(
			this.consumerPortURIPrefix + "-dcc",
			DynamicallyConnectableComponentConnector.class.getCanonicalName()) ;
		p.connectWith(this.providerInboundPortURI,
					  this.consumerPortURIPrefix + "-URIConsumerOutboundPort",
					  URIServiceConnector.class.getCanonicalName()) ;
		p.doDisconnection() ;
	}
}

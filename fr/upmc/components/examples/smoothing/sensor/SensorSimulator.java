package fr.upmc.components.examples.smoothing.sensor;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.examples.smoothing.rng.NormalRNGenerator;
import fr.upmc.components.examples.smoothing.rng.UniformRNGenerator;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataOfferedI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataRequiredI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGTriggerI;
import fr.upmc.components.examples.smoothing.rng.ports.RNGDataInboundPort;
import fr.upmc.components.examples.smoothing.rng.ports.RNGDataOutboundPort;
import fr.upmc.components.examples.smoothing.rng.ports.RNGTriggerInboundPort;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorData;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataOfferedI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.components.ports.AbstractDataOutboundPort;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>SensorSimulator</code> implements an active component
 * of type <code>RNGProducerI</code> offering a random number generation service
 * through a data offered interface <code>RNGDataOfferedI</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * The component uses two <code>RNGProducer</code> components and a component
 * <code<RNGAdder</code> to produces its random numbers by the sum of two
 * random numbers generated by its inner producer components.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			SensorSimulator
extends		AbstractComponent
implements	SensorI
{
	protected static class	BridgeDataOutboundPort
	extends		AbstractDataOutboundPort
	implements	RNGDataRequiredI.PullI,
				RNGDataRequiredI.PushI
	{
		private static final long 		serialVersionUID = 1L ;
		protected double	mean ;
		protected double	standardDeviation ;

		public			BridgeDataOutboundPort(
			ComponentI owner,
			double mean,
			double standardDeviation
			) throws Exception
		{
			super(RNGDataRequiredI.PullI.class,
				  RNGDataRequiredI.PushI.class,
				  owner) ;
			this.mean = mean ;
			this.standardDeviation = standardDeviation ;
		}

		@Override
		public void		receive(DataRequiredI.DataI d) throws Exception
		{
			double value =
				((RNGDataRequiredI.DoubleRandomNumberI)d).getTheRandomNumber() ;
			double result = mean + (value * this.standardDeviation) ;
			((SensorSimulator)this.owner).resend(new SensorData(result)) ;
		}
	}

	// ------------------------------------------------------------------------
	// Inner components and their ports
	// ------------------------------------------------------------------------

	protected UniformRNGenerator		ugenerator1 ;
	protected UniformRNGenerator		ugenerator2 ;
	protected NormalRNGenerator			ngenerator ;
	protected RNGTriggerInboundPort		ugenerator1TriggerPort ;
	protected RNGTriggerInboundPort		ugenerator2TriggerPort ;
	protected RNGDataInboundPort		ngeneratorDataInboundPort ;
	protected RNGDataOutboundPort		ngeneratorDataOutboundPort1 ;
	protected RNGDataOutboundPort		ngeneratorDataOutboundPort2 ;
	protected RNGDataInboundPort		ugenerator1DataInboundPort ;
	protected RNGDataInboundPort		ugenerator2DataInboundPort ;
	protected DataConnector				ngenerator2ugenerator1connector ;
	protected DataConnector				ngenerator2ugenerator2connector ;
	protected BridgeDataOutboundPort	bridgeDataOutboundPort ;
	protected DataConnector				bridgeConnector ;

	// ------------------------------------------------------------------------
	// Component's constructor, own ports and implementation variables
	// ------------------------------------------------------------------------

	protected SensorDataInboundPort		sensorPort ;
	protected double					mean ;
	protected double					standardDeviation ;
	protected boolean					pushStopped ;
	protected long						pushInterval ;

	/**
	 * create the component, create and publish its ports.
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param mean				mean of the produced values.
	 * @param standardDeviation	standard deviation of the produced values.
	 * @throws Exception
	 */
	public				SensorSimulator(
		double mean,
		double standardDeviation,
		String sensorPortURI,
		long pushInterval
		) throws Exception
	{
		super(true, true) ;

		// Component own services, ports and initialisations

		this.addOfferedInterface(SensorDataOfferedI.PullI.class) ;
		this.addRequiredInterface(SensorDataOfferedI.PushI.class) ;
		this.sensorPort = new SensorDataInboundPort(sensorPortURI, this) ;
		if (AbstractCVM.isDistributed) {
			this.sensorPort.publishPort() ;
		} else {
			this.sensorPort.localPublishPort() ;
		}
		this.addPort(this.sensorPort) ;

		this.mean = mean ;
		this.standardDeviation = standardDeviation ;
		this.pushStopped = false ;
		this.pushInterval = pushInterval ;

		// Inner components creation and interconnection

		this.ugenerator1 = new UniformRNGenerator(false, 0.0, 1.0) ;
		this.innerComponents.add(ugenerator1) ;
		this.ugenerator2 = new UniformRNGenerator(false, 0.0, 1.0) ;
		this.innerComponents.add(ugenerator2) ;
		this.ngenerator = new NormalRNGenerator(false) ;
		this.innerComponents.add(ngenerator) ;

		PortI[] ports =
			this.ugenerator1.findPortsFromInterface(RNGTriggerI.class) ;
		this.ugenerator1TriggerPort = (RNGTriggerInboundPort) ports[0] ;
		ports =
			this.ugenerator2.findPortsFromInterface(RNGTriggerI.class) ;
		this.ugenerator2TriggerPort = (RNGTriggerInboundPort) ports[0] ;
		ports =
			this.ngenerator.findPortsFromInterface(RNGDataOfferedI.PullI.class) ;
		this.ngeneratorDataInboundPort = (RNGDataInboundPort) ports[0] ;

		PortI[] portsAdder =
			this.ngenerator.findPortsFromInterface(RNGDataRequiredI.PullI.class) ;
		this.ngeneratorDataOutboundPort1 = (RNGDataOutboundPort) portsAdder[0] ;
		this.ngeneratorDataOutboundPort2 = (RNGDataOutboundPort) portsAdder[1] ;
		ports =
			this.ugenerator1.findPortsFromInterface(RNGDataOfferedI.PullI.class) ;
		this.ugenerator1DataInboundPort = (RNGDataInboundPort) ports[0] ;
		this.ngeneratorDataOutboundPort1.doConnection(
			this.ugenerator1DataInboundPort.getPortURI(),
			"fr.upmc.components.examples.smoothing.sensor.NGenerator2UGeneratorConnector") ;

		ports =
			this.ugenerator2.findPortsFromInterface(RNGDataOfferedI.PullI.class) ;
		this.ugenerator2DataInboundPort = (RNGDataInboundPort) ports[0] ;
		this.ngeneratorDataOutboundPort2.doConnection(
			this.ugenerator2DataInboundPort.getPortURI(),
			"fr.upmc.components.examples.smoothing.sensor.NGenerator2UGeneratorConnector") ;

		this.bridgeDataOutboundPort =
			new BridgeDataOutboundPort(this, this.mean, this.standardDeviation) ;
		this.bridgeDataOutboundPort.localPublishPort() ;
		this.bridgeDataOutboundPort.doConnection(
				this.ngeneratorDataInboundPort.getPortURI(),
				"fr.upmc.components.connectors.DataConnector") ;
	}

	/**
	 * in push mode, resend a data received from the inner component
	 * <code>NRNGenerator</code> through this component's own port
	 * <code>SensorDataInboundPort</code>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param sensorData				random number to be pushed to the client.
	 * @throws Exception
	 */
	public void			resend(SensorData sensorData) throws Exception
	{
		this.sensorPort.send(sensorData) ;
	}

	@Override
	public	SensorDataOfferedI.SensorDataI	produceSensorData()
	throws Exception
	{
		double value =
				((RNGDataOfferedI.DoubleRandomNumberI)
					this.ngeneratorDataInboundPort.get()).getTheRandomNumber() ;
		return new SensorData(value) ;
	}

	@Override
	public void			pushSensorData()
	throws Exception
	{
		// make one push
		this.ugenerator1TriggerPort.trigger() ;
		this.ugenerator2TriggerPort.trigger() ;

		// plan next push, if still required
		if (!this.pushStopped) {
			final SensorSimulator p = this ;
			this.scheduleTask(
					new ComponentTask() {
						@Override
						public void run() {
							try {
								p.pushSensorData() ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, this.pushInterval, TimeUnit.MILLISECONDS) ;
		}
	}

	@Override
	public void			startPushingData()
	{
		this.pushStopped = false ;
		final SensorSimulator ss = this ;
		this.scheduleTask(
				new ComponentTask() {
					@Override
					public void run() {
						try {
							ss.pushSensorData() ;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, this.pushInterval, TimeUnit.MILLISECONDS) ;
	}

	@Override
	public void			stopPushingData()
	{
		this.pushStopped = true ;
	}

	/**
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.scheduledTasksHandler.shutdown() ;
			this.scheduledTasksHandler.
								awaitTermination(1000, TimeUnit.MILLISECONDS) ;

			// disconnecting inner components
			this.ngeneratorDataOutboundPort1.doDisconnection() ;
			this.ngeneratorDataOutboundPort2.doDisconnection() ;
			this.bridgeDataOutboundPort.doDisconnection() ;
		} catch(Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		// will also shutdown the inner components
		super.shutdown();
	}
}

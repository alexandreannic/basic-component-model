package fr.upmc.components.examples.smoothing.gauge;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.examples.smoothing.filter.SensorDataOutboundPort;
import fr.upmc.components.examples.smoothing.sensor.SensorI;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataOfferedI.SensorDataI;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataRequiredI;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.patterns.dconnection.DynamicConnectionRequestBehaviour;
import fr.upmc.components.patterns.dconnection.DynamicConnectionRequestInboundPort;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>Gauge</code> implements a component that pulls data through
 * some sensor data required interface to display them.
 *
 * <p><strong>Description</strong></p>
 * 
 * <code>Gauge</code>s are considered as display devices connected to sensors.
 * To illustrate the use of two way component interfaces, gauges can be paired
 * when they are connected to different sensors measuring the same phenomenon
 * but using different means.  When a gauge displays a new value, it gets the
 * last displayed value of the paired gauge and displays the deviation of its
 * own value compared to this one.  Indeed, the deviation is due both to the
 * differences in the measurement means, but also to the delay between these
 * measurements when the actual value evolve.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.pullInterval > 0
 * </pre>
 * 
 * <p>Created on : 30 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			Gauge
extends		AbstractComponent
implements	SensorI
{
	/**	name of the gauge for displaying.									*/
	protected String					gaugeName ;
	/** sensor from which the gauge receives values.						*/
	protected SensorDataOutboundPort	sensorPort ;
	/**	true when display must be stopped, false otherwise.					*/
	protected boolean					displayingStopped ;
	/**	interval at which values are pulled and displayed.					*/
	protected final long				pullInterval ;
	/**	last value received by the gauge from the sensor.					*/
	protected double					lastValue ;
	/**	port connecting to the paired gague.								*/
	protected PairedGaugePort			peerGaugePort;
	/**	average of the deviations between this gague and the paried one.	*/
	protected double					averageDeviation ;
	/**	number of deviation values used to date to compute the average.		 */
	protected int						numberOfDeviations ;

	// Using the dynamic connection pattern, from the component model extensions
	protected final boolean				isDynamicConnectionServer ;
	protected final String				dCRPortURI ;
	protected final DynamicConnectionRequestBehaviour	dcrb ;

	/**
	 * create a gauge that dynamically connects over the
	 * <code>ParedGaugeI</code> interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param gaugeName		name of the gauge used fir displaying.
	 * @param sensorPortURI	URI of the sensor port of the gauge.
	 * @param pullInterval	interval in milliseconds between pulling values.
	 * @param dCRPortURI	URI of the server side port for dynamic connection request.
	 * @param isDynamicConnectionServer	true if this component plays the server role in dynamic connections.
	 * @throws 				Exception
	 */
	public				Gauge(
		String gaugeName,
		String sensorPortURI,
		long pullInterval,
		String dCRPortURI,
		boolean isDynamicConnectionServer
		) throws Exception
	{
		super(true, true) ;

		assert	pullInterval > 0 ;

		this.gaugeName = gaugeName ;
		this.pullInterval = pullInterval ;
		this.dCRPortURI = dCRPortURI ;
		this.isDynamicConnectionServer = isDynamicConnectionServer ;
		this.averageDeviation = 0.0 ;
		this.numberOfDeviations = 0 ;

		// to receive sensor data from a sensor type of component
		this.addRequiredInterface(SensorDataRequiredI.PullI.class) ;
		this.addOfferedInterface(SensorDataRequiredI.PushI.class) ;
		// to exchange with another gauge paired with this one
		this.addOfferedInterface(PairedGaugeI.class) ;
		this.addRequiredInterface(PairedGaugeI.class) ;

		this.sensorPort = new SensorDataOutboundPort(sensorPortURI, this) ;
		if (AbstractCVM.isDistributed) {
			this.sensorPort.publishPort() ;
		} else {
			this.sensorPort.localPublishPort() ;
		}
		this.addPort(this.sensorPort) ;

		final Gauge fGauge = this ;
		if (this.isDynamicConnectionServer) {
			this.dcrb =
				new DynamicConnectionRequestBehaviour(this, dCRPortURI) {
					@Override
					protected void publishDynamicConnectionRequestPort(
						DynamicConnectionRequestInboundPort p
						) throws Exception
					{
						if (AbstractCVM.isDistributed) {
							p.publishPort() ;
						} else {
							p.localPublishPort() ;
						}
					}

					@Override
					protected PortI createAndPublishClientSideDynamicPort()
					throws Exception
					{
						return null ;
					}

					@Override
					protected PortI createAndPublishServerSideDynamicPort()
					throws Exception
					{
						fGauge.peerGaugePort = new PairedGaugePort(fGauge) ;
						if (AbstractCVM.isDistributed) {
							fGauge.peerGaugePort.publishPort() ;
						} else {
							fGauge.peerGaugePort.localPublishPort() ;
						}
						return fGauge.peerGaugePort ;
					}

					@Override
					protected String dynamicConnectorClassName()
					throws Exception
					{
						return PairedGaugeConnector.class.getCanonicalName() ;
					}
				} ;
		} else {
			this.dcrb =
				new DynamicConnectionRequestBehaviour(this) {
					@Override
					protected void publishDynamicConnectionRequestPort(
						DynamicConnectionRequestInboundPort p
						) throws Exception
					{
								
					}

					@Override
					protected PortI createAndPublishClientSideDynamicPort()
					throws Exception
					{
						fGauge.peerGaugePort = new PairedGaugePort(fGauge) ;
						if (AbstractCVM.isDistributed) {
							fGauge.peerGaugePort.publishPort() ;
						} else {
							fGauge.peerGaugePort.localPublishPort() ;
						}
						return fGauge.peerGaugePort ;
					}

					@Override
					protected PortI createAndPublishServerSideDynamicPort()
					throws Exception
					{
						return null;
					}

					@Override
					protected String dynamicConnectorClassName()
					throws Exception
					{
						return PairedGaugeConnector.class.getCanonicalName() ;
					}
				} ;
		}
	}

	/**
	 * start the component, and if it is the client side for the dynamic
	 * connection, do this dynamic connection over <code>PairedGaugeI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	this.peerGaugePort != null
	 * post	this.peerGaugePort.connected()
	 * </pre>
	 * 
	 * @see fr.upmc.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
		if (!this.isDynamicConnectionServer) {
			// connect dynamically the two way interfaces PairedGaugeI
			String peerPortURI = 
						this.dcrb.connectDynamicallyWith(dCRPortURI) ;

			assert	this.peerGaugePort != null ;
			assert	this.peerGaugePort.getPortURI().equals(peerPortURI) ;
			assert	this.peerGaugePort.connected() ;
		}
		} catch(Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	public void			disconnectPeer() throws Exception
	{
		if (!this.isDynamicConnectionServer) {
			this.peerGaugePort.doDisconnection() ;
		}		
	}

	/**
	 * get a value, display it a plan a new call to display after
	 * <code>pullInterval</code> milliseconds.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			display() throws Exception
	{
		this.lastValue =
			((SensorDataRequiredI.SensorDataI)this.sensorPort.request()).
																	getValue() ;
		if (this.peerGaugePort != null && this.peerGaugePort.connected()) {
			double other =
				this.peerGaugePort.getLastGaugeValue(this.peerGaugePort) ;
			double deviation = this.lastValue - other ;
			this.averageDeviation =
				(this.numberOfDeviations * this.averageDeviation + deviation)/
												(this.numberOfDeviations+1) ;
			this.numberOfDeviations++ ;
			System.out.printf("%s:  (%.4f, %.4f, %.4f)\n",
							  this.gaugeName, this.lastValue, deviation,
							  this.averageDeviation) ;
		} else {
			System.out.println(this.gaugeName + ":  " + this.lastValue) ;
		}

		if (!this.displayingStopped) {
			final Gauge fg = this ;
			this.scheduleTask(
				new ComponentTask() {
					@Override
					public void run() {
						try {
							fg.display() ;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, this.pullInterval, TimeUnit.MILLISECONDS) ;
		}
	}

	/**
	 * start the display by calling the method <code>display</code> a first
	 * time after <code>pullInterval</code> milliseconds.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public void			startDisplaying()
	{
		this.displayingStopped = false ;
		final Gauge fg = this ;
		this.scheduleTask(
				new ComponentTask() {
					@Override
					public void run() {
						try {
							fg.display() ;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, this.pullInterval, TimeUnit.MILLISECONDS) ;
	}

	/**
	 * stop the display of new values.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public void			stopDisplaying()
	{
		this.displayingStopped = true ;
	}

	/**
	 * called by the peer component through the <code>PairedGaugeI</code>
	 * component interface, return the last value received form the sensor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the last received and displayed value.
	 */
	public double		getLastValue()
	{
		return this.lastValue ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#produceSensorData()
	 */
	@Override
	public SensorDataI produceSensorData() throws Exception {
		// to act as a sensor client, but do nothing
		// could be avoided by distinguishing sensors and sensor clients in
		// two interfaces rather than having SensorI defining both.
		return null;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#pushSensorData()
	 */
	@Override
	public void pushSensorData() throws Exception {
		// to act as a sensor client, but do nothing
		// could be avoided by distinguishing sensors and sensor clients in
		// two interfaces rather than having SensorI defining both.
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#startPushingData()
	 */
	@Override
	public void startPushingData() {
		// to act as a sensor client, but do nothing
		// could be avoided by distinguishing sensors and sensor clients in
		// two interfaces rather than having SensorI defining both.
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#stopPushingData()
	 */
	@Override
	public void stopPushingData() {
		// to act as a sensor client, but do nothing
		// could be avoided by distinguishing sensors and sensor clients in
		// two interfaces rather than having SensorI defining both.
	}
}

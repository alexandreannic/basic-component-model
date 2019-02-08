package fr.upmc.components.examples.smoothing;

import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.examples.smoothing.filter.Filter;
import fr.upmc.components.examples.smoothing.gauge.Gauge;
import fr.upmc.components.examples.smoothing.sensor.SensorSimulator;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>SmoothingCVM</code> deploys components of the smoothing
 * example.
 *
 * <p><strong>Description</strong></p>
 * 
 * Two sensor simulators push normally distributed random values through
 * averaging filters and then to gauges that exchange their values to display
 * the received value, its deviation from the last displayed value of the
 * other gauge and the average deviation over time.
 * 
 * If the two sensor simulators use the same mean and standard deviation, the
 * average deviation between the two gauge should reduce as their windows
 * (number of successive values used in the smoothing) augment.
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
public class			SmoothingCVM
extends		AbstractCVM
{
	/**	URI of the first sensor simulator inbound port pushing sensor data.	*/
	protected static final String	SS_INBOUNDPORTURI_1		= "ss1inboundPort" ;
	/**	URI of the first filter outbound port receiving sensor data.		*/
	protected static final String	FILTER_OUBOUNDPORTURI_1	= "filter1OutboundPort" ;
	/**	URI of the first filter outbound port pulling filtered data.		*/
	protected static final String	FILTER_INBOUNDPORTURI_1	= "filter1InboundPort" ;
	/**	URI of the first gauge outbound port pulling filtered data.			*/
	protected static final String	GAUGE_OUTBOUNDPORTURI_1	= "gauge1OutboundPort" ;
	/** URI of the first gauge server side dynamic connection request port.	*/
	protected static final String	GAUGE_DC_REQUEST_INBOUNDPORTURI	= "gauge1DCRPort" ;

	/**	URI of the second sensor simulator inbound port pushing sensor data.*/
	protected static final String	SS_INBOUNDPORTURI_2		= "ss2inboundPort" ;
	/**	URI of the second filter outbound port receiving sensor data.		*/
	protected static final String	FILTER_OUBOUNDPORTURI_2	= "filter2OutboundPort" ;
	/**	URI of the second filter outbound port pulling filtered data.		*/
	protected static final String	FILTER_INBOUNDPORTURI_2	= "filter2InboundPort" ;
	/**	URI of the second gauge outbound port pulling filtered data.		*/
	protected static final String	GAUGE_OUTBOUNDPORTURI_2	= "gauge2OutboundPort" ;

	/**	first sensor simulator.												*/
	protected SensorSimulator		sensorSimulator1 ;
	/** second sensor simulator.											*/
	protected SensorSimulator		sensorSimulator2 ;
	/** first filter.														*/
	protected Filter				filter1 ;
	/** second filter.														*/
	protected Filter				filter2 ;
	/** first gauge.														*/
	protected Gauge					gauge1 ;
	/** second gauge.														*/
	protected Gauge					gauge2 ;

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		// creation of the first sensor simulator providing N(10.0, 2.5)
		// random values.
		this.sensorSimulator1 =
					new SensorSimulator(10.0, 2.5, SS_INBOUNDPORTURI_1, 100) ;
		this.deployedComponents.add(this.sensorSimulator1) ;

		// creation of the first filter with a window of 4 values.
		this.filter1 = new Filter("filter1",
								  FILTER_OUBOUNDPORTURI_1,
								  FILTER_INBOUNDPORTURI_1,
								  4) ;
		this.deployedComponents.add(this.filter1) ;

		// connect the first sensor simulator to the first filter
		PortI p = this.filter1.findPortFromURI(FILTER_OUBOUNDPORTURI_1) ;
		p.doConnection(SS_INBOUNDPORTURI_1,
					   DataConnector.class.getCanonicalName()) ;

		// creation of the first gauge pulling a value each 400 milliseconds
		this.gauge1 = new Gauge("gauge1", GAUGE_OUTBOUNDPORTURI_1, 400,
								GAUGE_DC_REQUEST_INBOUNDPORTURI, true) ;
		this.deployedComponents.add(gauge1) ;

		// connecting the first filter to the first gauge.
		p = this.gauge1.findPortFromURI(GAUGE_OUTBOUNDPORTURI_1) ;
		p.doConnection(FILTER_INBOUNDPORTURI_1,
				   	   DataConnector.class.getCanonicalName()) ;

		// creation of the second sensor simulator providing N(10.0, 2.5)
		// random values.
		this.sensorSimulator2 =
					new SensorSimulator(10.0, 2.5, SS_INBOUNDPORTURI_2, 200) ;
		this.deployedComponents.add(this.sensorSimulator2) ;

		// creation of the second filter with a window of 4 values.
		this.filter2 = new Filter("                            filter2",
								  FILTER_OUBOUNDPORTURI_2,
								  FILTER_INBOUNDPORTURI_2,
								  4) ;
		this.deployedComponents.add(this.filter2) ;

		// connect the second sensor simulator to the second filter
		p = this.filter2.findPortFromURI(FILTER_OUBOUNDPORTURI_2) ;
		p.doConnection(SS_INBOUNDPORTURI_2,
			   	   	   DataConnector.class.getCanonicalName()) ;

		// creation of the second gauge pulling a value each 400 milliseconds
		this.gauge2 = new Gauge("                            gauge2",
								GAUGE_OUTBOUNDPORTURI_2, 400,
								GAUGE_DC_REQUEST_INBOUNDPORTURI, false) ;
		this.deployedComponents.add(gauge2) ;

		// connecting the second filter to the second gauge.
		p = this.gauge2.findPortFromURI(GAUGE_OUTBOUNDPORTURI_2) ;
		p.doConnection(FILTER_INBOUNDPORTURI_2,
					   DataConnector.class.getCanonicalName()) ;

		super.deploy();
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#start()
	 */
	@Override
	public void			start() throws Exception
	{
		super.start() ;

		final SensorSimulator ss1 = this.sensorSimulator1 ;
		this.sensorSimulator1.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						ss1.startPushingData() ;
					}
				});
		final SensorSimulator ss2 = this.sensorSimulator2 ;
		this.sensorSimulator2.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						ss2.startPushingData() ;
					}
				});

		final Gauge fg1 = this.gauge1 ;
		this.gauge1.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						fg1.startDisplaying() ;
					}
				}) ;
		final Gauge fg2 = this.gauge2 ;
		this.gauge2.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						fg2.startDisplaying() ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		final SensorSimulator ss1 = this.sensorSimulator1 ;
		this.sensorSimulator1.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						ss1.stopPushingData() ;
					}
				}) ;
		final SensorSimulator ss2 = this.sensorSimulator2 ;
		this.sensorSimulator2.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						ss2.stopPushingData() ;
					}
				}) ;


		final Gauge fg1 = this.gauge1 ;
		this.gauge1.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						fg1.stopDisplaying() ;
					}
				}) ;
		final Gauge fg2 = this.gauge2 ;
		this.gauge2.runTask(
				new ComponentTask() {
					@Override
					public void run() {
						fg2.stopDisplaying() ;
					}
				}) ;


		Thread.sleep(200) ;

		PortI p = this.sensorSimulator1.findPortFromURI(SS_INBOUNDPORTURI_1) ;
		p.doDisconnection() ;
		p = this.sensorSimulator2.findPortFromURI(SS_INBOUNDPORTURI_2) ;
		p.doDisconnection() ;
		p = this.filter1.findPortFromURI(FILTER_INBOUNDPORTURI_1) ;
		p.doDisconnection() ;
		p = this.filter2.findPortFromURI(FILTER_INBOUNDPORTURI_2) ;
		p.doDisconnection() ;
		this.gauge2.disconnectPeer() ;

		super.shutdown() ;
	}

	public static void		main(String[] args)
	{
		SmoothingCVM a = new SmoothingCVM() ;
		try {
			a.deploy() ;
			System.out.println("starting...") ;
			a.start() ;
			Thread.sleep(10000L) ;
			System.out.println("shutting down...") ;
			a.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

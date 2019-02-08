package fr.upmc.components.examples.smoothing;

import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.examples.smoothing.filter.Filter;
import fr.upmc.components.examples.smoothing.gauge.Gauge;
import fr.upmc.components.examples.smoothing.sensor.SensorSimulator;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>SmoothingDistributedCVM</code> deploys components of the
 * smoothing example.
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
 * <p>Created on : 4 Feb. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			SmoothingDistributedCVM
extends		AbstractDistributedCVM
{
	// URI of the JVM used in the deployment
	public static final String		SENSOR1_JVMURI	= "sensor1" ;
	public static final String		SENSOR2_JVMURI	= "sensor2" ;
	public static final String		FILTER1_JVMURI	= "filter1" ;
	public static final String		FILTER2_JVMURI	= "filter2" ;
	public static final String		GAUGE1_JVMURI	= "gauge1" ;
	public static final String		GAUGE2_JVMURI	= "gauge2" ;

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

	public				SmoothingDistributedCVM(String[] args)
	throws Exception
	{
		super(args);
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#instantiateAndPublish()
	 */
	@Override
	public void			instantiateAndPublish()
	throws Exception
	{
		if (thisJVMURI.equals(SENSOR1_JVMURI)) {
			// creation of the first sensor simulator providing N(10.0, 2.5)
			// random values.
			this.sensorSimulator1 =
					new SensorSimulator(10.0, 2.5, SS_INBOUNDPORTURI_1, 100) ;
			this.deployedComponents.add(this.sensorSimulator1) ;
		} else if (thisJVMURI.equals(SENSOR2_JVMURI)) {
			// creation of the second sensor simulator providing N(10.0, 2.5)
			// random values.
			this.sensorSimulator2 =
					new SensorSimulator(10.0, 2.5, SS_INBOUNDPORTURI_2, 200) ;
			this.deployedComponents.add(this.sensorSimulator2) ;
		} else if (thisJVMURI.equals(FILTER1_JVMURI)) {
			// creation of the first filter with a window of 4 values.
			this.filter1 = new Filter("filter1",
					  				  FILTER_OUBOUNDPORTURI_1,
					  				  FILTER_INBOUNDPORTURI_1,
					  				  4) ;
			this.deployedComponents.add(this.filter1) ;
		} else if (thisJVMURI.equals(FILTER2_JVMURI)) {
			// creation of the second filter with a window of 4 values.
			this.filter2 = new Filter("filter2",
									  FILTER_OUBOUNDPORTURI_2,
									  FILTER_INBOUNDPORTURI_2,
									  8) ;
			this.deployedComponents.add(this.filter2) ;
		} else if (thisJVMURI.equals(GAUGE1_JVMURI)) {
			// creation of the first gauge pulling a value each 400 milliseconds
			this.gauge1 = new Gauge("gauge1", GAUGE_OUTBOUNDPORTURI_1, 400,
									GAUGE_DC_REQUEST_INBOUNDPORTURI, true) ;
			this.deployedComponents.add(gauge1) ;
		} else if (thisJVMURI.equals(GAUGE2_JVMURI)) {
			// creation of the second filter with a window of 4 values.
			this.gauge2 = new Gauge("gauge2", GAUGE_OUTBOUNDPORTURI_2, 400,
									GAUGE_DC_REQUEST_INBOUNDPORTURI, false) ;
			this.deployedComponents.add(gauge2) ;
		} else {
			System.out.println("Error, wrong JVM URI: " + thisJVMURI) ;
			System.exit(1) ;
		}

		super.instantiateAndPublish();
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#interconnect()
	 */
	@Override
	public void			interconnect() throws Exception
	{
		assert	this.instantiationAndPublicationDone ;

		if (thisJVMURI.equals(SENSOR1_JVMURI)) {

		} else if (thisJVMURI.equals(SENSOR2_JVMURI)) {

		} else if (thisJVMURI.equals(FILTER1_JVMURI)) {
			// connect the first sensor simulator to the first filter
			PortI p = this.filter1.findPortFromURI(FILTER_OUBOUNDPORTURI_1) ;
			p.doConnection(SS_INBOUNDPORTURI_1,
						   DataConnector.class.getCanonicalName()) ;
		} else if (thisJVMURI.equals(FILTER2_JVMURI)) {
			// connect the second sensor simulator to the second filter
			PortI p = this.filter2.findPortFromURI(FILTER_OUBOUNDPORTURI_2) ;
			p.doConnection(SS_INBOUNDPORTURI_2,
						   DataConnector.class.getCanonicalName()) ;
		} else if (thisJVMURI.equals(GAUGE1_JVMURI)) {
			// connecting the first filter to the first gauge.
			PortI p = this.gauge1.findPortFromURI(GAUGE_OUTBOUNDPORTURI_1) ;
			p.doConnection(FILTER_INBOUNDPORTURI_1,
						   DataConnector.class.getCanonicalName()) ;
		} else if (thisJVMURI.equals(GAUGE2_JVMURI)) {
			// connecting the second filter to the second gauge.
			PortI p = this.gauge2.findPortFromURI(GAUGE_OUTBOUNDPORTURI_2) ;
			p.doConnection(FILTER_INBOUNDPORTURI_2,
					   	   DataConnector.class.getCanonicalName()) ;
		} else {
			System.out.println("Error, wrong JVM URI: " + thisJVMURI) ;
			System.exit(1) ;
		}

		super.interconnect();
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#start()
	 */
	@Override
	public void			start() throws Exception
	{
		super.start() ;

		if (thisJVMURI.equals(SENSOR1_JVMURI)) {
			final SensorSimulator ss1 = this.sensorSimulator1 ;
			this.sensorSimulator1.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							ss1.startPushingData() ;
						}
					});
		} else if (thisJVMURI.equals(SENSOR2_JVMURI)) {
			final SensorSimulator ss2 = this.sensorSimulator2 ;
			this.sensorSimulator2.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							ss2.startPushingData() ;
						}
					});
		} else if (thisJVMURI.equals(FILTER1_JVMURI)) {
			
		} else if (thisJVMURI.equals(FILTER2_JVMURI)) {
			
		} else if (thisJVMURI.equals(GAUGE1_JVMURI)) {
			final Gauge fg1 = this.gauge1 ;
			this.gauge1.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							fg1.startDisplaying() ;
						}
					}) ;
		} else if (thisJVMURI.equals(GAUGE2_JVMURI)) {
			final Gauge fg2 = this.gauge2 ;
			this.gauge2.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							fg2.startDisplaying() ;
						}
					}) ;
		} else {
			System.out.println("Error, wrong JVM URI: " + thisJVMURI) ;
			System.exit(1) ;
		}
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		if (thisJVMURI.equals(SENSOR1_JVMURI)) {
			final SensorSimulator ss1 = this.sensorSimulator1 ;
			this.sensorSimulator1.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							ss1.stopPushingData() ;
						}
					}) ;
			Thread.sleep(200L) ;
			PortI p = ss1.findPortFromURI(SS_INBOUNDPORTURI_1) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(SENSOR2_JVMURI)) {
			final SensorSimulator ss2 = this.sensorSimulator2 ;
			this.sensorSimulator2.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							ss2.stopPushingData() ;
						}
					}) ;
			Thread.sleep(200L) ;
			PortI p = ss2.findPortFromURI(SS_INBOUNDPORTURI_2) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(FILTER1_JVMURI)) {
			Thread.sleep(200L) ;
			PortI p = this.filter1.findPortFromURI(FILTER_INBOUNDPORTURI_1) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(FILTER2_JVMURI)) {
			Thread.sleep(200L) ;
			PortI p = this.filter2.findPortFromURI(FILTER_INBOUNDPORTURI_2) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(GAUGE1_JVMURI)) {
			Thread.sleep(200L) ;
			final Gauge fg1 = this.gauge1 ;
			this.gauge1.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							fg1.stopDisplaying() ;
						}
					}) ;
		} else if (thisJVMURI.equals(GAUGE2_JVMURI)) {
			Thread.sleep(200L) ;
			final Gauge fg2 = this.gauge2 ;
			this.gauge2.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							fg2.stopDisplaying() ;
							try {
								fg2.disconnectPeer() ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}) ;
		} else {
			System.out.println("Error, wrong JVM URI: " + thisJVMURI) ;
			System.exit(1) ;
		}

		super.shutdown();
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws Exception
	{
		if (thisJVMURI.equals(SENSOR1_JVMURI)) {
			final SensorSimulator ss1 = this.sensorSimulator1 ;
			this.sensorSimulator1.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							ss1.stopPushingData() ;
						}
					}) ;
			Thread.sleep(200) ;
			PortI p = ss1.findPortFromURI(SS_INBOUNDPORTURI_1) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(SENSOR2_JVMURI)) {
			final SensorSimulator ss2 = this.sensorSimulator2 ;
			this.sensorSimulator2.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							ss2.stopPushingData() ;
						}
					}) ;
			Thread.sleep(200) ;
			PortI p = ss2.findPortFromURI(SS_INBOUNDPORTURI_2) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(FILTER1_JVMURI)) {
			Thread.sleep(200) ;
			PortI p = this.filter1.findPortFromURI(FILTER_INBOUNDPORTURI_1) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(FILTER2_JVMURI)) {
			Thread.sleep(200) ;
			PortI p = this.filter2.findPortFromURI(FILTER_INBOUNDPORTURI_2) ;
			p.doDisconnection() ;
		} else if (thisJVMURI.equals(GAUGE1_JVMURI)) {
			Thread.sleep(200) ;
			final Gauge fg1 = this.gauge1 ;
			this.gauge1.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							fg1.stopDisplaying() ;
						}
					}) ;
		} else if (thisJVMURI.equals(GAUGE2_JVMURI)) {
			Thread.sleep(200) ;
			final Gauge fg2 = this.gauge2 ;
			this.gauge2.runTask(
					new ComponentTask() {
						@Override
						public void run() {
							fg2.stopDisplaying() ;
							try {
								fg2.disconnectPeer() ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}) ;
		} else {
			System.out.println("Error, wrong JVM URI: " + thisJVMURI) ;
			System.exit(1) ;
		}

		super.shutdownNow();
	}

	public static void	main(String[] args)
	{
		System.out.println("Beginning") ;
		try {
			SmoothingDistributedCVM da = new SmoothingDistributedCVM(args) ;
			da.deploy() ;
			System.out.println("starting...") ;
			da.start() ;
			Thread.sleep(5000L) ;
			System.out.println("shutting down...") ;
			da.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Main thread ending") ;
		System.exit(0);
	}
}

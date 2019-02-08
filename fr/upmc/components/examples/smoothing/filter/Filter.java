package fr.upmc.components.examples.smoothing.filter;

import java.util.Vector;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.examples.smoothing.sensor.SensorDataInboundPort;
import fr.upmc.components.examples.smoothing.sensor.SensorI;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorData;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataOfferedI;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataRequiredI;

/**
 * The class <code>Filter</code> defines components that can be put between a
 * sensor and a sensor client to smooth the values of the sensor.
 *
 * <p><strong>Description</strong></p>
 * 
 * Filters provide the same interface as sensors, so the sensor client need
 * not know whether it is connected to a sensor or a filter.  A filter however
 * also behaves as a sensor client.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 30 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			Filter
extends		AbstractComponent
implements	SensorI
{
	// ------------------------------------------------------------------------
	// Constructors and instance variables
	// ------------------------------------------------------------------------

	protected final String					filterName ;
	protected final Vector<Double>			buffer ;
	protected final SensorDataOutboundPort	sensorPort ;
	protected SensorDataInboundPort			filterPort ;
	protected int							windowSize ;

	/**
	 * create the component and its ports, and initialise it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param filterName	name of the filter component for displaying.
	 * @param sensorPortURI	URI of the port connecting to the sensor.
	 * @param filterPortURI	URI of the port offering the filtered data.
	 * @param windowSize	number of values used in the average filter.
	 * @throws Exception
	 */
	public				Filter(
		String filterName,
		String sensorPortURI,
		String filterPortURI,
		int windowSize
		) throws Exception
	{
		super(true) ;
		this.filterName = filterName ;
		this.windowSize = windowSize ;
		this.buffer = new Vector<Double>() ;

		// interfaces and port to the underlying sensor
		this.addRequiredInterface(SensorDataRequiredI.PullI.class) ;
		this.addOfferedInterface(SensorDataRequiredI.PushI.class) ;
		this.sensorPort = new SensorDataOutboundPort(sensorPortURI, this) ;
		if (AbstractCVM.isDistributed) {
			this.sensorPort.publishPort() ;
		} else {
			this.sensorPort.localPublishPort() ;
		}
		this.addPort(this.sensorPort) ;

		// interface and ports for the client
		this.addOfferedInterface(SensorDataOfferedI.PullI.class) ;
		this.addRequiredInterface(SensorDataOfferedI.PushI.class);
		this.filterPort = new SensorDataInboundPort(filterPortURI, this) ;
		if (AbstractCVM.isDistributed) {
			this.filterPort.publishPort() ;
		} else {
			this.filterPort.localPublishPort() ;
		}
		this.addPort(this.filterPort) ;
	}

	// ------------------------------------------------------------------------
	// Component service implementation methods
	// ------------------------------------------------------------------------

	/**
	 * 
	 * accept data from a sensor and buffer them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param sd		sensor data received from the sensor.
	 */
	public void			acceptData(SensorDataRequiredI.SensorDataI sd)
	{
		System.out.printf("%s: %.4f\n", this.filterName,  sd.getValue()) ;
		if (this.buffer.size() >= this.windowSize) {
			this.buffer.remove(0) ;
		}
		this.buffer.add(sd.getValue()) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#produceSensorData()
	 */
	@Override
	public SensorDataOfferedI.SensorDataI	produceSensorData()
	throws Exception
	{
		double mean = 0.0 ;
		for(Double d : this.buffer) {
			mean += d ;
		}
		return new SensorData(mean / this.buffer.size()) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#pushSensorData()
	 */
	@Override
	public void			pushSensorData()
	throws Exception
	{
		double mean = 0.0 ;
		for(Double d : this.buffer) {
			mean += d ;
		}
		this.filterPort.send(new SensorData(mean / this.buffer.size())) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#startPushingData()
	 */
	@Override
	public void			startPushingData() {
		// no repetitive pushing for the filter
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.sensor.SensorI#stopPushingData()
	 */
	@Override
	public void			stopPushingData() {
		// no repetitive pushing for the filter
	}
}

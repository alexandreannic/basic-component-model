package fr.upmc.components.examples.smoothing.sensor;

import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataOfferedI;

/**
 * The interface <code>SensorI</code> defines the base service implementation
 * methods for a sensor component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 4 Feb. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		SensorI
{
	/**
	 * in pull mode, produce one sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return			the new sensor data.
	 * @throws Exception
	 */
	public	SensorDataOfferedI.SensorDataI	produceSensorData()
	throws Exception ;

	/**
	 * in push mode, either compute and send one sensor data, or start a stream
	 * of sensor data sent to the consumer.
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
	public void			pushSensorData()
	throws Exception ;

	/**
	 * start a stream of sensor data that will be pushed to the consumer until
	 * the stream is stopped by a call to the method
	 * <code>stopPushingData</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 */
	public void			startPushingData() ;

	/**
	 * stop the sending of sensor data when such a stream was started by a call
	 * to the method <code>startPushingData</code>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public void			stopPushingData() ;
}

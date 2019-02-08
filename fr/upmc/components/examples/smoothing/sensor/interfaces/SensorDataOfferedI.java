package fr.upmc.components.examples.smoothing.sensor.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;

/**
 * The component interface <code>SensorDataOfferedI</code> defines a sensor
 * provider service as a data exchanging API.
 *
 * <p><strong>Description</strong></p>
 * 
 * The exchange of data uses the standard methods defined in the interfaces
 * <code>DataOfferedI.PullI</code> and <code>DataOfferedI.PushI</code>, but
 * the exchanged data will have to implement the interface
 * <code>SensorDataI</code> defined here.
 * 
 * <p>Created on : 31 Jan. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		SensorDataOfferedI
extends		DataOfferedI
{
	/**
	 * The interface <code>SensorDataI</code> defines the basic behaviour of
	 * sensor data exchanged through this component interface.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 4 Feb. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	SensorDataI
	extends		DataOfferedI.DataI
	{
		public double	getSensorData() ;
	}
}

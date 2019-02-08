package fr.upmc.components.examples.smoothing.sensor.interfaces;

import fr.upmc.components.interfaces.DataRequiredI;

/**
 * The component interface <code>SensorDataRequiredI</code> defines a sensor
 * client service as a data exchanging API.
 *
 * <p><strong>Description</strong></p>
 * 
 * The exchange of data uses the standard methods defined in the interfaces
 * <code>DataRequiredI.PullI</code> and <code>DataRequiredI.PushI</code>, but
 * the exchanged data will have to implement the Java interface
 * <code>SensorDataI</code> defined here.
 * 
 * <p>Created on : 31 Jan. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		SensorDataRequiredI
extends		DataRequiredI
{
	/**
	 * The Java interface <code>SensorDataI</code>defines the basic behaviour of
	 * sensor data exchanged through this component interface.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 6 Feb. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	SensorDataI
	extends		DataRequiredI.DataI
	{
		public double	getValue() ;
	}
}

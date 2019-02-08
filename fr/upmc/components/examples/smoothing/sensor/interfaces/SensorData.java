package fr.upmc.components.examples.smoothing.sensor.interfaces;

/**
 * The class <code>SensorData</code> implements the data that can be exchanged
 * through <code>SensorDataOfferedI</code> and <code>SensorDataRequiredI</code>
 * interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * The class can be used to exchange sensor values that are (double) real
 * numbers.  It implements the <code>SensorDataI</code> of both the offered
 * and the required interfaces so to ease the use of the service.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 31 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			SensorData
implements	SensorDataOfferedI.SensorDataI,
			SensorDataRequiredI.SensorDataI
{
	private static final long serialVersionUID = 1L;
	protected double	value ;

	public			SensorData(double value)	{ this.value = value; }

	@Override
	public double	getSensorData()				{ return this.value ; }
	@Override
	public double	getValue()					{ return this.value ; }
}
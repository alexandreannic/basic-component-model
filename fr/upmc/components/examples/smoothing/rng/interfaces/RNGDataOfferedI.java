package fr.upmc.components.examples.smoothing.rng.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;

/**
 * The component interface <code>RNGDataOfferedI</code> defines a random number
 * generation service offered through a data exchanging API.
 *
 * <p><strong>Description</strong></p>
 * 
 * Data will be exchanged using the standard <code>DataOfferedI.PullI</code>
 * and <code>DataOfferedI.PushI</code>.  The data exchanged through these
 * methods will be typed by the Java interface <code>DoubleRandomNumberI</code>
 * defined in this component interface.
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		RNGDataOfferedI
extends		DataOfferedI
{
	/**
	 * The Java interface <code>DoubleRandomNumberI</code> defines the behaviour
	 * of objects used to transmit the random number from the provider component
	 * to its client.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 28 janv. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	DoubleRandomNumberI
	extends		DataOfferedI.DataI
	{
		public double	getTheRandomNumber() ;
	}
}

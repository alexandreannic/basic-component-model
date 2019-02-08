package fr.upmc.components.examples.smoothing.rng.interfaces;

import fr.upmc.components.interfaces.DataRequiredI;

/**
 * The component interface <code>RNGDataRequiredI</code> is used to require a
 * random number generator service through a data exchanging API.
 *
 * <p><strong>Description</strong></p>
 * 
 * Data will be exchanged using the standard <code>DataRequiredI.PullI</code>
 * and <code>DataRequiredI.PushI</code>.  The data exchanged through these
 * methods will be typed by the Java interface <code>DoubleRandomNumberI</code>
 * defined in this component interface.
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		RNGDataRequiredI
extends		DataRequiredI
{
	/**
	 * The Java interface <code>DoubleRandomNumberI</code> defines the behaviour
	 * of objects used by the client to receive a random number.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <p>Created on : 28 janv. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	DoubleRandomNumberI
	extends		DataRequiredI.DataI
	{
		public double	getTheRandomNumber() ;
	}
}

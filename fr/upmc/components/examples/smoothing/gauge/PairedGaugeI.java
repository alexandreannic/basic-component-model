package fr.upmc.components.examples.smoothing.gauge;

import fr.upmc.components.interfaces.TwoWayI;

/**
 * The component interface <code>PairedGaugeI</code> defines the service of
 * getting the last displayed value of a gauge, and it is meant to be used by
 * another gauge that is paired with the one receiveing the request.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 4 Feb. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		PairedGaugeI
extends		TwoWayI
{
	/**
	 * return the last displayed value by the gauge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param caller		gauge component requesting the value.
	 * @return				last displayed value by the gauge.
	 * @throws Exception
	 */
	public double		getLastGaugeValue(PairedGaugeI caller)
	throws Exception ;
}

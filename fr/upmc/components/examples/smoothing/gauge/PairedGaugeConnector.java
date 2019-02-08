package fr.upmc.components.examples.smoothing.gauge;

import fr.upmc.components.connectors.AbstractTwoWayConnector;

/**
 * The class <code>PairedGaugeConnector</code> implements a connector between
 * paired gauges to allow them to exchange their displayed values.
 *
 * <p><strong>Description</strong></p>
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
public class			PairedGaugeConnector
extends		AbstractTwoWayConnector
implements	PairedGaugeI
{
	/**
	 * @see fr.upmc.components.examples.smoothing.gauge.PairedGaugeI#getLastGaugeValue(fr.upmc.components.examples.smoothing.gauge.PairedGaugeI)
	 */
	@Override
	public double		getLastGaugeValue(PairedGaugeI caller)
	throws Exception
	{
		// in a two way peer-to-peer exchange, the two peers use the same
		// interface to call each other, so the connector need to know which
		// one is the caller to forward the call to the other.
		if (this.requiring == caller) {
			return ((PairedGaugeI)this.offering).getLastGaugeValue(caller) ;
		} else {
			return ((PairedGaugeI)this.requiring).getLastGaugeValue(caller) ;
		}
	}
}

package fr.upmc.components.examples.smoothing.gauge;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractTwoWayPort;

/**
 * The class <code>PairedGaugePort</code> implements the port that is used to
 * connect paired gauges to allow them to exchange their displayed values.
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
public class			PairedGaugePort
extends		AbstractTwoWayPort
implements	PairedGaugeI
{
	private static final long serialVersionUID = 1L;

	public				PairedGaugePort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, PairedGaugeI.class, owner) ;
		assert owner instanceof Gauge ;
	}

	public				PairedGaugePort(
		ComponentI owner
		) throws Exception
	{
		super(PairedGaugeI.class, owner) ;
		assert owner instanceof Gauge ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.gauge.PairedGaugeI#getLastGaugeValue(fr.upmc.components.examples.smoothing.gauge.PairedGaugeI)
	 */
	@Override
	public double		getLastGaugeValue(PairedGaugeI caller)
	throws Exception
	{
		// in a two way peer-to-peer exchange, the two peers use the same
		// interface to call each other, so the port need to know which
		// one is the caller to forward the call to the other.
		if (caller != this) {
			final Gauge fg = (Gauge) this.owner ;
			return this.owner.handleRequestSync(
						new ComponentService<Double>() {
							@Override
							public Double call() throws Exception {
								return fg.getLastValue() ;
							}
						}) ;
		} else {
			return ((PairedGaugeI)this.connector).getLastGaugeValue(this) ;
		}
	}
}

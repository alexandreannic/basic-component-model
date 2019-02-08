package fr.upmc.components.examples.smoothing.rng.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataOfferedI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI;
import fr.upmc.components.interfaces.DataOfferedI ;
import fr.upmc.components.ports.AbstractDataInboundPort ;

/**
 * The class <code>RNGDataInboundPort</code> implements the inbound port of
 * a component of type <code>RNGProducerI</code> offering a random number
 * generation service through the data offered interface
 * <code>RNGDataOfferedI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * The <code>get</code> method (from the pull interface of the data offered
 * component interface) calls the method <code>produce</code> from its
 * <code>RNGProducerI</code> owner component to get a new random number..
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.owner instanceof RNGProducerI
 * </pre>
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			RNGDataInboundPort
extends		AbstractDataInboundPort
implements	RNGDataOfferedI.PullI,
			RNGDataOfferedI.PushI
{
	private static final long serialVersionUID = 1L;

	public				RNGDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RNGDataOfferedI.PullI.class, RNGDataOfferedI.PushI.class, owner);

		assert	this.owner instanceof RNGProducerI ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI	get() throws Exception
	{
		final RNGProducerI rngp = (RNGProducerI) this.owner ;
		return this.owner.handleRequestSync(
				new ComponentService<DataOfferedI.DataI>() {
					@Override
					public DataOfferedI.DataI call() throws Exception {
						return rngp.produce() ;
					}
				});
	}
}

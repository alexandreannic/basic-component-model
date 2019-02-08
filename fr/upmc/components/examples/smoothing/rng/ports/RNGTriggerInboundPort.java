package fr.upmc.components.examples.smoothing.rng.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGTriggerI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * The class <code>RNGTriggerInboundPort</code> implements the inbound port of
 * a component of type <code>RNGProducerI</code> offering a trigger service for
 * random number generation service in push mode, through the offered interface
 * <code>RNGTriggerI</code>.
 *
 * <p><strong>Description</strong></p>
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
public class			RNGTriggerInboundPort
extends		AbstractInboundPort
implements	RNGTriggerI
{
	private static final long serialVersionUID = 1L;

	public				RNGTriggerInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RNGTriggerI.class, owner) ;

		assert	this.owner instanceof RNGProducerI ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGTriggerI#trigger()
	 */
	@Override
	public void			trigger() throws Exception
	{
		final RNGProducerI rngp = (RNGProducerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						rngp.generate() ;
						return null ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGTriggerI#stop()
	 */
	@Override
	public void			stop() throws Exception
	{
		final RNGProducerI rngp = (RNGProducerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						rngp.stopGenerate() ;
						return null ;
					}
				}) ;
	}
}

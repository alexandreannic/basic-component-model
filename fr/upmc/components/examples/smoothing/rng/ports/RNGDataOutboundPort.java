package fr.upmc.components.examples.smoothing.rng.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.examples.smoothing.rng.NormalRNGenerator;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataRequiredI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataRequiredI.DoubleRandomNumberI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.components.ports.AbstractDataOutboundPort;

/**
 * The class <code>RNGDataOutboundPort</code> implements a random number data
 * outbound port for components of type <code>NormalRNGenerator</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * The port calls the method <code>acceptFromRNGProducerPorts</code> on its
 * <code>NormalRNGenerator</code> owner component.  As a normal random number
 * generator uses two uniform random number generator to provide its required
 * inputs, the method not only passes the random number but also the URI of the
 * port, so that the component can known from which provider component the
 * number is coming.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			RNGDataOutboundPort
extends		AbstractDataOutboundPort
implements	RNGDataRequiredI.PullI,
			RNGDataRequiredI.PushI
{
	private static final long serialVersionUID = 1L;

	public				RNGDataOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RNGDataRequiredI.PullI.class,
			  RNGDataRequiredI.PushI.class,
			  owner) ;
		assert	this.owner instanceof NormalRNGenerator ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void			receive(DataRequiredI.DataI d)
	throws Exception
	{
		final NormalRNGenerator rnga = (NormalRNGenerator) this.owner ;
		final RNGDataRequiredI.DoubleRandomNumberI rn = (DoubleRandomNumberI) d ;
		final String portURI = this.getPortURI() ;
		rnga.handleRequestAsync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						rnga.acceptFromRNGProducerPorts(rn, portURI) ;
						return null ;
					}
				}) ;
	}
}

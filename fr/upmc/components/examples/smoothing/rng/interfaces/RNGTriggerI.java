package fr.upmc.components.examples.smoothing.rng.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The component interface <code>RNGTriggerI</code> offers a trigger service for
 * random number generation in push mode.
 *
 * <p><strong>Description</strong></p>
 * 
 * This component interface defines two services, <code>trigger</code> to
 * trigger the random number generation service in push mode, and
 * <code>stop</code> that stops the service if a stream of number is being
 * produced since the last call to <code>trigger</code>.
 * 
 * The interface is made both required and offered, a design decision that can
 * be made when the interface designer controls both the requiring and the
 * offering sides of a service.  Making required and offered interfaces
 * separate is handy when client and server components are developed
 * independently, but the possibility for interfaces to be both at the
 * same time simplifies things when interface designers can impose hte calling
 * protocol to both sides at the same time.
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		RNGTriggerI
extends		RequiredI,
			OfferedI
{
	/**
	 * trigger the random number generation, by making it push at least one
	 * random number.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			trigger() throws Exception ;

	/**
	 * stop the generation of random numbers if a steady stream is being
	 * produced since a previous call to <code>trigger</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			stop() throws Exception ;
}

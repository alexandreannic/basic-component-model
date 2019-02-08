package fr.upmc.components.examples.smoothing.rng.interfaces;

/**
 * The Java interface <code>RNGProducerI</code> defines the service
 * implementation methods for any kind of component generating random numbers.
 *
 * <p><strong>Description</strong></p>
 * 
 * This is a component implementation interface that eases the use of different
 * component implementations of the random number generation service.
 * 
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		RNGProducerI
{
	/**
	 * in pull mode, return a random number when called.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the produced random number.
	 * @throws Exception
	 */
	public	RNGDataOfferedI.DoubleRandomNumberI	produce()
	throws Exception ;

	/**
	 * in push mode, send at least one new random number when called.
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
	public void			generate()
	throws Exception ;

	/**
	 * in push mode, stop the sending of new random numbers by
	 * <code>generate</code>, if necessary, otherwise do nothing.
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
	public void			stopGenerate()
	throws Exception ;
}

package fr.upmc.components.examples.smoothing.rng;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataOfferedI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGTriggerI;
import fr.upmc.components.examples.smoothing.rng.ports.RNGDataInboundPort;
import fr.upmc.components.examples.smoothing.rng.ports.RNGTriggerInboundPort;

/**
 * The class <code>UniformRNGenerator</code> implements a component that
 * generates U(lowerBound, upperBound) random numbers and provides this
 * service through a data offered interface called <code>RNGDataOfferedI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * The component uses the random number generator of the package
 * <code>org.apache.commons.math3</code>.
 * 
 * The components can provide random numbers in pull or push mode.  In pull
 * mode, it is called on its <code>RNGDataOfferedI.PullI</code> interface.
 * The service is then provided by the component <code>produce</code> method
 * which returns a random number at a time.
 * 
 * In the push mode, the push is triggered by a call on its interface
 * <code>RNGTriggerI</code>.  The service is then provided by the
 * component <code>generate</code> method that will push random numbers through
 * the <code>RNGDataOfferedI.PushI</code> interface.  This method can be
 * implemented in different ways.  It can either push one random number or
 * start a stream of random numbers to be pushed at some time interval.  In
 * this latter case, one need to be able to stop the stream, which is the
 * role of the method <code>stopGenerate</code>.  Here, <code>generate</code>
 * pushes one number at a time, so <code>stopGenerate</code> does nothing.
 * 
 * The component service implementation methods are declared by the
 * <code>RNGProducerI</code> Java interface, which eases the use of different
 * implementations of components providing the random number generation service.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.lowerBound <= this.upperBound && this.rg != null
 * </pre>
 * 
 * <p>Created on : 28 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			UniformRNGenerator
extends		AbstractComponent
implements	RNGProducerI
{
	/**
	 * The class <code>IntRandomNumber</code> implements that data interface
	 * defined in the data offered interface <code>RNGDataOfferedI</code>
	 * that is provided by this component.
	 *
	 * <p>Created on : 28 janv. 2014</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public static class	DoubleRandomNumber
	implements	RNGDataOfferedI.DoubleRandomNumberI
	{
		private static final long	serialVersionUID = 1L ;
		protected double			rn ;

		public			DoubleRandomNumber(double rn)	{ this.rn = rn; }

		@Override
		public double	getTheRandomNumber()			{ return rn ; }			
	}

	// ------------------------------------------------------------------------
	// Constructors and instance variables
	// ------------------------------------------------------------------------

	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg ;
	/**	lowest number that can be generated.								*/
	protected final double				lowerBound ;
	/**	largest number that can be generated.								*/
	protected final double				upperBound ;
	/**	the port through which random numbers are provided.					*/
	protected RNGDataInboundPort		rngPort ;

	/**
	 * create the component, create and publish its ports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	lowerBound <= upperBound
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param isConcurrent	true if the component is created with its own thread.
	 * @param lowerBound	lowest number that can be generated.
	 * @param upperBound	largest number that can be generated.
	 * @throws Exception
	 */
	public				UniformRNGenerator(
		boolean isConcurrent,
		double lowerBound,
		double upperBound
		) throws Exception
	{
		super(isConcurrent);
		this.addOfferedInterface(RNGDataOfferedI.PullI.class) ;
		this.addRequiredInterface(RNGDataOfferedI.PushI.class) ;
		this.addOfferedInterface(RNGTriggerI.class);
		this.rngPort = new RNGDataInboundPort(this) ;
		this.rngPort.localPublishPort() ;
		this.addPort(this.rngPort) ;
		RNGTriggerInboundPort p = new RNGTriggerInboundPort(this) ;
		this.addPort(p) ;
		p.localPublishPort() ;

		assert	lowerBound <= upperBound ;

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.rg = new RandomDataGenerator() ;
		this.rg.reSeedSecure() ;

		assert	this.lowerBound <= this.upperBound && this.rg != null ;
	}

	//-------------------------------------------------------------------------
	// Component internal service implementation methods
	//-------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI#produce()
	 */
	@Override
	public	RNGDataOfferedI.DoubleRandomNumberI	produce()
	throws Exception
	{
		return new DoubleRandomNumber(rg.nextUniform(lowerBound, upperBound)) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI#generate()
	 */
	@Override
	public void			generate()
	throws Exception
	{
		// use the common-math generator to provide the service
		this.rngPort.send(new DoubleRandomNumber(
									rg.nextUniform(lowerBound, upperBound))) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI#stopGenerate()
	 */
	@Override
	public void			stopGenerate()
	throws Exception
	{
		// As this RNG producer only produces one random number when called
		// on generate, do nothing.
	}
}

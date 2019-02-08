package fr.upmc.components.examples.smoothing.rng;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataOfferedI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataRequiredI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI;
import fr.upmc.components.examples.smoothing.rng.ports.RNGDataInboundPort;
import fr.upmc.components.examples.smoothing.rng.ports.RNGDataOutboundPort;

/**
 * The class <code>NormalRNGenerator</code> implements a component that
 * generates N(0, 1) random numbers and provides this service through a data
 * offered interface called <code>RNGDataOfferedI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * The component uses the Box-Muller method to generate values from normal
 * distribution.  The method generates two U(0,1) random numbers U and V
 * from U(0, 1) and uses the fact that the values X and Y defined as:
 * 
 *     X = sqrt(-2 ln U) cos (2 pi V)
 *     Y = sqrt(-2 ln U) sin (2 pi V)
 * 
 * are two N(0, 1) random variables.  To get U and V, the component requires
 * two components generating uniform random numbers.
 * 
 * The components can provide random numbers in pull or push mode.  In pull
 * mode, it is called on its <code>RNGDataOfferedI.PullI</code> interface.
 * The service is then provided by the component <code>produce</code> method
 * which returns a random number at a time.
 * 
 * In the push mode, the push is triggered by a call on its component interface
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
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 28 Jan. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			NormalRNGenerator
extends		AbstractComponent
implements	RNGProducerI
{
	/**
	 * The class <code>DoubleRandomNumber</code> define a data object that is
	 * exchanged through the component data interfaces.
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public static class	DoubleRandomNumber
	implements	RNGDataOfferedI.DoubleRandomNumberI,
				RNGDataRequiredI.DoubleRandomNumberI
	{
		private static final long	serialVersionUID = 1L;
		protected final double		rnumber ;

		public			DoubleRandomNumber(double rnumber)
		{
			this.rnumber = rnumber;
		}

		@Override
		public double	getTheRandomNumber()
		{
			return this.rnumber ;
		}
	}

	// ------------------------------------------------------------------------
	// Constructors and instance variables
	// ------------------------------------------------------------------------

	/**	port connecting to the first uniform rng component.					*/
	protected RNGDataOutboundPort	urngPort1 ;
	/**	port connecting to the second uniform rng component.				*/
	protected RNGDataOutboundPort	urngPort2 ;
	/**	last uniform random number received from the first rng.				*/
	protected DoubleRandomNumber	urng1LastValue ;
	/**	last uniform random number received from the second rng.			*/
	protected DoubleRandomNumber	urng2LastValue ;
	/**	port connecting to the client of this normal rng.					*/
	protected RNGDataInboundPort	portToConsumer ;

	/**
	 * create the component and its ports, and initialises the local variables.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param isConcurrent
	 * @throws Exception
	 */
	public				NormalRNGenerator(
		boolean isConcurrent
		) throws Exception
	{
		super(isConcurrent) ;
		this.addOfferedInterface(RNGDataOfferedI.PullI.class) ;
		this.addRequiredInterface(RNGDataOfferedI.PushI.class) ;
		this.addRequiredInterface(RNGDataRequiredI.PullI.class) ;
		this.addOfferedInterface(RNGDataRequiredI.PushI.class) ;
		this.urngPort1 = new RNGDataOutboundPort(this) ;
		this.addPort(this.urngPort1) ;
		this.urngPort1.localPublishPort() ;
		this.urngPort2 = new RNGDataOutboundPort(this) ;
		this.addPort(this.urngPort2) ;
		this.urngPort2.localPublishPort() ;
		this.portToConsumer = new RNGDataInboundPort(this) ;
		this.addPort(this.portToConsumer) ;
		this.portToConsumer.localPublishPort() ;

		this.urng1LastValue = null ;
		this.urng2LastValue = null ;
	}

	// ------------------------------------------------------------------------
	// Service implementation methods
	// ------------------------------------------------------------------------

	/**
	 * compute first random number from the Box-Muller method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param urn1	U(0,1) random number
	 * @param urn2	U(0,1) random number
	 * @return		N(0,1) random number
	 */
	protected double	computeNormalRandomNumber(
		double urn1,
		double urn2
		)
	{
		// compute first random number from the Box-Muller method
		return Math.sqrt(-2.0 * Math.log(urn1)) *
											Math.cos(2.0 * Math.PI * urn2) ;
	}

	/**
	 * in push mode, accept values from the RNG producers asynchronously, but
	 * when two values are available, push their sum to the consumer port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param rn			the received random number
	 * @param portURI		the URI of the port from which the number was received.
	 * @throws Exception
	 */
	public void			acceptFromRNGProducerPorts(
		RNGDataRequiredI.DoubleRandomNumberI rn,
		String portURI
		) throws Exception
	{
		if (portURI.equals(this.urngPort1.getPortURI())) {
			this.urng1LastValue = (DoubleRandomNumber) rn ;
		} else if (portURI.equals(this.urngPort2.getPortURI())) {
			this.urng2LastValue = (DoubleRandomNumber) rn ;
		} else {
			throw new Exception("unknown port " + portURI) ;
		}
		if (this.urng1LastValue != null && this.urng2LastValue != null) {
			RNGDataRequiredI.DoubleRandomNumberI drn1 = this.urng1LastValue ;
			RNGDataRequiredI.DoubleRandomNumberI drn2 = this.urng2LastValue ;

			this.portToConsumer.send(
				new DoubleRandomNumber(this.computeNormalRandomNumber(
												drn1.getTheRandomNumber(),
												drn2.getTheRandomNumber()))) ;
			this.urng1LastValue = null ;
			this.urng2LastValue = null ;
		}
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI#produce()
	 */
	@Override
	public	RNGDataOfferedI.DoubleRandomNumberI	produce() throws Exception
	{
		RNGDataRequiredI.DoubleRandomNumberI drn1 =
			(RNGDataRequiredI.DoubleRandomNumberI) this.urngPort1.request() ;
		RNGDataRequiredI.DoubleRandomNumberI drn2 =
			(RNGDataRequiredI.DoubleRandomNumberI) this.urngPort2.request() ;

		return new DoubleRandomNumber(
					this.computeNormalRandomNumber(drn1.getTheRandomNumber(),
												   drn2.getTheRandomNumber())) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI#generate()
	 */
	@Override
	public void			generate()
	throws Exception
	{
		// pull two new unifprm random numbers
		RNGDataRequiredI.DoubleRandomNumberI drn1 =
			(RNGDataRequiredI.DoubleRandomNumberI) this.urngPort1.request() ;
		RNGDataRequiredI.DoubleRandomNumberI drn2 =
			(RNGDataRequiredI.DoubleRandomNumberI) this.urngPort2.request() ;

		// push the result to the consumer
		this.portToConsumer.send(new DoubleRandomNumber(
				this.computeNormalRandomNumber(drn1.getTheRandomNumber(),
											   drn2.getTheRandomNumber()))) ;
	}

	/**
	 * @see fr.upmc.components.examples.smoothing.rng.interfaces.RNGProducerI#stopGenerate()
	 */
	@Override
	public void			stopGenerate()
	throws Exception
	{
		// As this component generates only one random number when called on
		// generate, do nothing.
	}
}

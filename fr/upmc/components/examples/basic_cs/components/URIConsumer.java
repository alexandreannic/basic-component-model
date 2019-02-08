package fr.upmc.components.examples.basic_cs.components;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.examples.basic_cs.interfaces.URIConsumerI;
import fr.upmc.components.examples.basic_cs.ports.URIGetterOutboundPort;
import fr.upmc.components.exceptions.ComponentStartException;

/**
 * The class <code>URIConsumer</code> implements a component that gets URI
 * from a URI provider component.
 *
 * <p><strong>Description</strong></p>
 * 
 * The component declares its required service through the required interface
 * <code>URIConsumerI</code> which has a <code>getURI</code> requested service
 * signature.  The internal method <code>getURIandPrint</code> implements the
 * main task of the component, as it calls the provider component through the
 * outbound port implementing the connection.  It does that repeatedly ten
 * times then disconnect and halt.  The <code>start</code> method initiates
 * this process. 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 22 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			URIConsumer
extends		AbstractComponent
{
	// ------------------------------------------------------------------------
	// Constructors and instance variables
	// ------------------------------------------------------------------------

	/**	the outbound port used to call the service.							*/
	protected URIGetterOutboundPort	uriGetterPort ;
	/**	counting service invocations.										*/
	protected int					counter ;

	/**
	 * @param outboundPortURI	 URI of the URI getter otbound port.
	 * @throws Exception
	 */
	public				URIConsumer(
		String 	outboundPortURI
		) throws Exception
	{
		// false = no simple thread ; true = one schedulable thread
		super(false, true) ;
		// put the required interface in the set of interfaces required by
		// the component.
		this.addRequiredInterface(URIConsumerI.class) ;
		// create the port that exposes the required interface
		this.uriGetterPort =
						new URIGetterOutboundPort(outboundPortURI, this) ;
		// add the port to the set of ports of the component
		this.addPort(this.uriGetterPort) ;
		// publish the port (an outbound port is always local)
		this.uriGetterPort.localPublishPort() ;
		this.counter = 0 ;
	}

	//-------------------------------------------------------------------------
	// Component internal services
	//-------------------------------------------------------------------------

	/**
	 * method that implements the component's behaviour: call the URI service
	 * ten times and print the URI on the terminal, waiting a second between
	 * each call.
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
	public void			getURIandPrint() throws Exception
	{
		this.counter++ ;
		if (this.counter <= 10) {
			// Get the next URI and print it
			this.logMessage("consumer getting a new URI.") ;
			String uri = this.uriGetterPort.getURI() ;
			System.out.println("URI no " + this.counter + ": " + uri) ;

			// Schedule the next service method invocation in one second.
			// All tasks and services of a component must be called through
			// the methods for running tasks and handling requests.  These
			// methods (from the CVM) handles the internal concurrency of
			// the component when required, and therefore ensure their good
			// properties (like synchronisation).
			final URIConsumer uc = this ;
			this.scheduleTask(
					new ComponentTask() {
						@Override
						public void run() {
							try {
								uc.getURIandPrint() ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					1000, TimeUnit.MILLISECONDS) ;
		} else {
			// When finished, disconnect from the server component.
			this.uriGetterPort.doDisconnection() ;
		}
	}

	//-------------------------------------------------------------------------
	// Component life-cycle
	//-------------------------------------------------------------------------

	/**
	 * a component is always started by calling this method, so intercept the
	 * call and make sure the task of the component is executed.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("starting consumer component.");
		final URIConsumer uc = this ;
		// Schedule the first service method invocation in one second.
		this.scheduleTask(
			new ComponentTask() {
				@Override
				public void run() {
					try {
						uc.getURIandPrint() ;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			},
			1000, TimeUnit.MILLISECONDS);
	}
}

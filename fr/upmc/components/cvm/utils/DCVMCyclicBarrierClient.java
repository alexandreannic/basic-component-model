package fr.upmc.components.cvm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * The class <code>DCVMCyclicBarrierClient</code> implements the client
 * side of the distributed cyclic wait barrier mechanism.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 21 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DCVMCyclicBarrierClient
{
	/** socket used to exchange signals with the wait barrier.				*/
	protected Socket			cyclicBarrierSignalingSocket ;
	/**	a buffered reader to read from the socket.							*/
	protected BufferedReader	cyclicBarrierBR ;
	/** a print stream to write onto the socket.							*/
	protected PrintStream		cyclicBarrierPS ;
	/**	name of the host that executes the process to be synchronised.		*/
	protected String			hostname ;
	/**	URI of the JVM that is executing the process to be synchronised.	*/
	protected String			jvmURI ;

	/**
	 * create the client side object implementing the wait behaviour.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param cyclicBarrierHostname	name of the host running the cyclic barrier.
	 * @param cyclicBarrierPort		port number listen by the cyclic barrier.
	 * @throws IOException 
	 */
	public				DCVMCyclicBarrierClient(
		String cyclicBarrierHostname,
		int cyclicBarrierPort,
		String hostname,
		String jvmURI
		) throws IOException
	{
		super();
		this.hostname = hostname ;
		this.jvmURI = jvmURI ;
		this.cyclicBarrierSignalingSocket =
						new Socket(cyclicBarrierHostname, cyclicBarrierPort) ;
		this.cyclicBarrierPS =
			new PrintStream(
				this.cyclicBarrierSignalingSocket.getOutputStream(), true) ;
		this.cyclicBarrierBR =
			new BufferedReader(
				new InputStreamReader(
					this.cyclicBarrierSignalingSocket.getInputStream())) ;
	}

	/**
	 * signal the current virtual machine to the central distributed cyclic
	 * barrier and then wait for the signal from the barrier before resuming
	 * execution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.cyclicBarrierSignalingSocket.isConnected()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws IOException
	 */
	public void			waitBarrier()
	throws IOException
	{
		assert	this.cyclicBarrierSignalingSocket.isConnected() ;

		// send the necessary information to allow the cyclic barrier to call
		// back to release the client process.
		this.cyclicBarrierPS.println(
				this.jvmURI + " " + this.hostname + " " +
				this.cyclicBarrierSignalingSocket.getLocalPort()) ;
		// this call waits until something is written by the cyclic barrier.
		this.cyclicBarrierBR.readLine() ;
	}

	/**
	 * closing the connection with the central distributed cyclic barrier.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.cyclicBarrierSignalingSocket.isConnected()
	 * post	this.cyclicBarrierSignalingSocket.isClosed()
	 * </pre>
	 *
	 * @throws IOException
	 */
	public void			closeBarrier() throws IOException
	{
		assert	this.cyclicBarrierSignalingSocket.isConnected() ;
		
		this.cyclicBarrierSignalingSocket.close() ;

		assert	this.cyclicBarrierSignalingSocket.isClosed() ;
	}
}

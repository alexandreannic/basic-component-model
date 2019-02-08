package fr.upmc.components.extensions.synchronizer.components.syncTools;

import java.io.Serializable;
import java.util.UUID;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeI;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionInboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutInboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeInboundPort;
import fr.upmc.components.ports.PortI;

public class ArrayBlockingQueue
extends AbstractComponent
{
	protected String 					arrayBlockingQueueURI;
	protected int 						arraySize;
	protected boolean 					isDistributed;
	
	protected java.util.concurrent.ArrayBlockingQueue<Serializable>
										array;
	
	
	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param arrayBlockingQueueURI
	 * @param arraySize
	 * @param isDistributed
	 * @throws Exception
	 */
	public		ArrayBlockingQueue(
		String arrayBlockingQueueURI,
		Integer arraySize,
		Boolean isDistributed
	) throws Exception
	{
		super(false);
		
		// Put the offered interface in the set of interfaces offered
		this.addOfferedInterface(ArrayBlockingQueueConnectionI.class);
		
		PortI p = new ArrayBlockingQueueConnectionInboundPort(
												arrayBlockingQueueURI, this);
		// Add the port to the set of ports of the component
		this.addPort(p);
		// Publish the port
		if(isDistributed) {
			p.publishPort();
		}
		else {
			p.localPublishPort();
		}
		
		this.arrayBlockingQueueURI	= arrayBlockingQueueURI;
		this.arraySize				= arraySize;
		this.isDistributed			= isDistributed;
		this.array					= 
			new java.util.concurrent.ArrayBlockingQueue<Serializable>(arraySize);
	}


	
	public void 			put(
		Serializable e
	) throws Exception {
		array.put(e);
	}
	
	
	public Serializable	take()
	throws Exception
	{
		return (Serializable)array.take();
	}
	
	
	/**
	 * Create a new port for the calling component to use the ArrayBlockingQueuq.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public synchronized String			getOwnPortURI(
		boolean take
	) throws Exception
	{
		String newURI = arrayBlockingQueueURI + "_" + UUID.randomUUID().toString();
		
		PortI p = null;
		
		if (take) {
			this.addOfferedInterface(ArrayBlockingQueueTakeI.class) ;
			p = new ArrayBlockingQueueTakeInboundPort(newURI, this) ;
		}
		else {
			this.addOfferedInterface(ArrayBlockingQueuePutI.class) ;
			p = new ArrayBlockingQueuePutInboundPort(newURI, this) ;
		}
		this.addPort(p) ;
		
		if(isDistributed) {
			p.publishPort();
		}
		else {
			p.localPublishPort();
		}
		
		return newURI;
	}
}

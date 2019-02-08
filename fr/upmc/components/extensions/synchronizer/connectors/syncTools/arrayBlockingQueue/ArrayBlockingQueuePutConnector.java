package fr.upmc.components.extensions.synchronizer.connectors.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutI;

public class ArrayBlockingQueuePutConnector
extends AbstractConnector
implements ArrayBlockingQueuePutClientI
{

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutClientI#put(java.io.Serializable)
	 */
	@Override
	public void put(Serializable e) throws Exception {
		((ArrayBlockingQueuePutI)this.offering).put(e);
	}

}

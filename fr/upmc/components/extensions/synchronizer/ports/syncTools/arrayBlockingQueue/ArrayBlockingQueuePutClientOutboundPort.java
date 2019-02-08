package fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class ArrayBlockingQueuePutClientOutboundPort
extends AbstractOutboundPort
implements ArrayBlockingQueuePutClientI
{
	public 		ArrayBlockingQueuePutClientOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, ArrayBlockingQueuePutClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutClientI#put(java.io.Serializable)
	 */
	@Override
	public void put(Serializable e) throws Exception {
		((ArrayBlockingQueuePutClientI)this.connector).put(e);
	}
}

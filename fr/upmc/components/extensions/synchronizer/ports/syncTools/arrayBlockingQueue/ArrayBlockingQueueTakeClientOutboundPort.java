package fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class ArrayBlockingQueueTakeClientOutboundPort
extends AbstractOutboundPort
implements ArrayBlockingQueueTakeClientI
{
	public 		ArrayBlockingQueueTakeClientOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, ArrayBlockingQueueTakeClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeClientI#take()
	 */
	@Override
	public Serializable take() throws Exception {
		return ((ArrayBlockingQueueTakeClientI)this.connector).take();
	}
}

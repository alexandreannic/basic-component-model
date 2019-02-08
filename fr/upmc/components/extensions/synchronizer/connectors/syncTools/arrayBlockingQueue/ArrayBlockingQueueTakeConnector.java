package fr.upmc.components.extensions.synchronizer.connectors.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeI;

public class ArrayBlockingQueueTakeConnector
extends AbstractConnector
implements ArrayBlockingQueueTakeClientI
{

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeClientI#take()
	 */
	@Override
	public Serializable take() throws Exception {
		return ((ArrayBlockingQueueTakeI)this.offering).take();
	}

}

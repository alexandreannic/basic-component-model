package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.interfaces.OfferedI;

public interface ArrayBlockingQueueTakeI
extends OfferedI
{
	/**
	 * Retrieves and removes the head of this queue, waiting if necessary
	 * until an element becomes available.
	 * @return the object removed from the array
	 * @throws Exception
	 */
	public Serializable 		take() throws Exception;
}

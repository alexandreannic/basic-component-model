package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.interfaces.RequiredI;

public interface ArrayBlockingQueueTakeClientI
extends RequiredI
{
	/**
	 * Retrieves and removes the head of this queue, waiting if necessary
	 * until an element becomes available.
	 * @return the object removed from the array
	 * @throws Exception
	 */
	public Serializable take() throws Exception;
}

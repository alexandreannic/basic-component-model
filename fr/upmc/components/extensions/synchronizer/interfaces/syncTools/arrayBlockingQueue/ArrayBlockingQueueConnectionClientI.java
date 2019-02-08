package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue;

import fr.upmc.components.interfaces.RequiredI;

public interface ArrayBlockingQueueConnectionClientI
extends RequiredI
{
	/**
	 * Create a new port for the calling component to use the ArrayBlockingQueue.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public String		getOwnPortURI(
			boolean take
		) throws Exception;
}

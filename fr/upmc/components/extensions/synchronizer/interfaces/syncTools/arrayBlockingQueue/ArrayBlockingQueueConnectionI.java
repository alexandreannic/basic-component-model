package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue;

import fr.upmc.components.interfaces.OfferedI;

public interface ArrayBlockingQueueConnectionI
extends OfferedI
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

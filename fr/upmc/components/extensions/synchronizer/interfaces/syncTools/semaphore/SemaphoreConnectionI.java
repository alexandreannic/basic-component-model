package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore;

import fr.upmc.components.interfaces.OfferedI;

public interface SemaphoreConnectionI extends OfferedI {
	
	/**
	 * Create a new port for the calling component to use the Semaphore.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public String getOwnPortURI() throws Exception;
}

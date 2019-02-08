package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore;

import fr.upmc.components.interfaces.RequiredI;

public interface SemaphoreConnectionClientI extends RequiredI {
	/**
	 * Create a new port for the calling component to use the Semaphore.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public String getOwnPortURI() throws Exception;
}

package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier;

import fr.upmc.components.interfaces.RequiredI;

public interface CyclicBarrierConnectionClientI extends RequiredI {
	/**
	 * Create a new port for the calling component to use the CyclicBarrier.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public String getOwnPortURI() throws Exception;
}

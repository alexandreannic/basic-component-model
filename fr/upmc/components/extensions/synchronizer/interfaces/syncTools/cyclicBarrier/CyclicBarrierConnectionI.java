package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier;

import fr.upmc.components.interfaces.OfferedI;

public interface CyclicBarrierConnectionI extends OfferedI {
	/**
	 * Create a new port for the calling component to use the CountDownLatch.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public String getOwnPortURI() throws Exception;
}

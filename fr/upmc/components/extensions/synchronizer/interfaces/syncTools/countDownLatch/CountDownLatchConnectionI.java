package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch;

import fr.upmc.components.interfaces.OfferedI;


public interface CountDownLatchConnectionI
extends OfferedI
{
	/**
	 * Create a new port for the calling component to use the CountDownLatch.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public String		getOwnPortURI(
	) throws Exception;
}

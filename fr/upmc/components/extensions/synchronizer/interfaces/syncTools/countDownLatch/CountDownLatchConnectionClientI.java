package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch;

import fr.upmc.components.interfaces.RequiredI;


public interface CountDownLatchConnectionClientI
extends RequiredI
{
	/**
	 * Create a new port for the calling component to use the CountDownLatch.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public String		getOwnPortURI(
	) throws Exception;
}

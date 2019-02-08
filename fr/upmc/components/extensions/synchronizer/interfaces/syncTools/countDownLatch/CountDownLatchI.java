package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch;

import fr.upmc.components.interfaces.OfferedI;


public interface CountDownLatchI
extends OfferedI
{
	/**
	 * The caller component will wait until there is no more component to wait.
	 * @throws Exception
	 */
	public void		await(
	) throws Exception;
	
	/**
	 * Decrement the number of waited components.
	 * @throws Exception
	 */
	public void		count(
	) throws Exception;
	
	/**
	 * @return Number of component waited.
	 * @throws Exception
	 */
	public int		getCount(
	) throws Exception;
}

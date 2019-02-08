package fr.upmc.components.extensions.synchronizer.connectors.syncTools.countDownLatch;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchI;


public class CountDownLatchConnector
extends AbstractConnector
implements CountDownLatchClientI
{
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI#await()
	 */
	@Override
	public void		await(
	) throws Exception
	{
		((CountDownLatchI)this.offering).await();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI#count()
	 */
	@Override
	public void		count(
	) throws Exception
	{
		((CountDownLatchI)this.offering).count();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI#getCount()
	 */
	@Override
	public int		getCount(
	) throws Exception
	{
		return ((CountDownLatchI)this.offering).getCount();
	}
}

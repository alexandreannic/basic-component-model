package fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI;
import fr.upmc.components.ports.AbstractOutboundPort;


public class 		CountDownLatchClientOutboundPort
extends AbstractOutboundPort
implements CountDownLatchClientI
{
	public 			CountDownLatchClientOutboundPort(
		String uri,
		ComponentI owner
	) throws Exception
	{
		super(uri, CountDownLatchClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI#await()
	 */
	@Override
	public void		await(
	) throws Exception
	{
		((CountDownLatchClientI)this.connector).await();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI#count()
	 */
	@Override
	public void		count(
	) throws Exception
	{
		((CountDownLatchClientI)this.connector).count();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI#getCount()
	 */
	@Override
	public int		getCount(
	) throws Exception
	{
		return ((CountDownLatchClientI)this.connector).getCount();
	}
}

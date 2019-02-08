package fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.extensions.synchronizer.components.syncTools.CountDownLatch;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchI;
import fr.upmc.components.ports.AbstractInboundPort;

public class CountDownLatchInboundPort
extends AbstractInboundPort
implements CountDownLatchI
{
	private static final long serialVersionUID = 1L;
	
	
	
	public		CountDownLatchInboundPort(
		String uri,
		ComponentI owner
	) throws Exception
	{
		super(uri, CountDownLatchI.class, owner);
		
		assert uri != null && owner instanceof CountDownLatch;
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchI#await()
	 */
	@Override
	public void			await(
	) throws Exception
	{
		final CountDownLatch cdl = (CountDownLatch) this.owner;
		
		cdl.handleRequestSync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						cdl.await();
						return null;
					}
				});
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchI#count()
	 */
	@Override
	public void			count(
	) throws Exception
	{
		final CountDownLatch cdl = (CountDownLatch) this.owner;
		
		cdl.count();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchI#getCount()
	 */
	@Override
	public int			getCount() throws Exception {
		
		final CountDownLatch cdl = (CountDownLatch) this.owner;
		
		return cdl.getCount();
	}
}

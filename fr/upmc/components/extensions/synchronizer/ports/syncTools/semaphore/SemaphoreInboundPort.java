package fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.extensions.synchronizer.components.syncTools.Semaphore;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI;
import fr.upmc.components.ports.AbstractInboundPort;

public class SemaphoreInboundPort
extends AbstractInboundPort
implements SemaphoreI
{

	private static final long serialVersionUID = 1L;

	public 			SemaphoreInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
			{
		super(uri, SemaphoreI.class, owner);

		assert uri != null && owner instanceof Semaphore;
			}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#acquire()
	 */
	@Override
	public void acquire() throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		sp.handleRequestSync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						sp.acquire();
						return  null;
					}
				});
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#acquire(int permits)
	 */
	@Override
	public void acquire(final int permits) throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		sp.handleRequestSync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						sp.acquire(permits);
						return  null;
					}
				});

	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#release()
	 */
	@Override
	public void release() throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		sp.release();

	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#release(int permits)
	 */
	@Override
	public void release(int permits) throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		sp.release(permits);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#availablePermits()
	 */
	@Override
	public int availablePermits() throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		return sp.availablePermits();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#getQueueLength()
	 */
	@Override
	public int getQueueLength() throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		return sp.getQueueLength();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#tryAcquire()
	 */
	@Override
	public boolean tryAcquire() throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		return sp.handleRequestSync(
				new ComponentService<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return sp.tryAcquire();
					}
				});

	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI#tryAcquire(int permits, long timeout, TimeUnit unit)
	 */
	@Override
	public boolean tryAcquire(final int permits, final long timeout, final TimeUnit unit)
			throws Exception {
		final Semaphore sp = (Semaphore) this.owner;
		return sp.handleRequestSync(
				new ComponentService<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return sp.tryAcquire(permits,timeout,unit);
					}
				});
	}


}

package fr.upmc.components.extensions.synchronizer.connectors.syncTools.semaphore;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI;

public class SemaphoreConnector
extends AbstractConnector
implements SemaphoreClientI
{

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#acquire()
	 */
	@Override
	public void acquire() throws Exception {
		((SemaphoreI)this.offering).acquire();
		
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#acquire(int permits)
	 */
	@Override
	public void acquire(int permits) throws Exception {
		((SemaphoreI)this.offering).acquire(permits);
		
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#release()
	 */
	@Override
	public void release() throws Exception {
		((SemaphoreI)this.offering).release();
		
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#release(int permits)
	 */
	@Override
	public void release(int permits) throws Exception {
		((SemaphoreI)this.offering).release(permits);
		
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#availablePermits()
	 */
	@Override
	public int availablePermits() throws Exception {
		return ((SemaphoreI)this.offering).availablePermits();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#getQueueLength()
	 */
	@Override
	public int getQueueLength() throws Exception {
		return ((SemaphoreI)this.offering).getQueueLength();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#tryAcquire()
	 */
	@Override
	public boolean tryAcquire() throws Exception {
		return ((SemaphoreI)this.offering).tryAcquire();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#tryAcquire(int permits, long timeout, TimeUnit unit)
	 */
	@Override
	public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
			throws Exception {
		return ((SemaphoreI)this.offering).tryAcquire(permits, timeout, unit);
	}

}

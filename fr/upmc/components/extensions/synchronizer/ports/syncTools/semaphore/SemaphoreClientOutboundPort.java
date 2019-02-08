package fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class SemaphoreClientOutboundPort 
extends AbstractOutboundPort
implements SemaphoreClientI
{
	
	public SemaphoreClientOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, SemaphoreClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#acquire()
	 */
	@Override
	public void acquire() throws Exception {
		 ((SemaphoreClientI)this.connector).acquire();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#acquire(int permits)
	 */
	@Override
	public void acquire(int permits) throws Exception {
		((SemaphoreClientI)this.connector).acquire(permits);
		
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#release()
	 */
	@Override
	public void release() throws Exception {
		((SemaphoreClientI)this.connector).release();
		
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#release(int permits)
	 */
	@Override
	public void release(int permits) throws Exception {
		((SemaphoreClientI)this.connector).release(permits);
		
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#availablePermits()
	 */
	@Override
	public int availablePermits() throws Exception {
		return ((SemaphoreClientI)this.connector).availablePermits();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#getQueueLength()
	 */
	@Override
	public int getQueueLength() throws Exception {
		return ((SemaphoreClientI)this.connector).getQueueLength();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#tryAcquire()
	 */
	@Override
	public boolean tryAcquire() throws Exception {
		return ((SemaphoreClientI)this.connector).tryAcquire();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI#tryAcquire(int permits, long timeout, TimeUnit unit)
	 */
	@Override
	public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
			throws Exception {
		return ((SemaphoreClientI)this.connector).tryAcquire(permits, timeout, unit);
	}

}

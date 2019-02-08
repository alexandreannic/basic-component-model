package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore;

import java.util.concurrent.TimeUnit;

import fr.upmc.components.interfaces.RequiredI;

public interface SemaphoreClientI extends RequiredI {
	
	/**Acquires a permit from this semaphore, blocking until one is available, or the thread is interrupted.
	 * @throws Exception
	 */
	public void acquire() throws Exception;
	
	
	/**Acquires the given number of permits from this semaphore, blocking until all are available, or the thread is interrupted.
	 * @param permits
	 * @throws Exception
	 */
	public void acquire(int permits) throws Exception;
	
	
	/**Releases a permit, returning it to the semaphore.
	 * @throws Exception
	 */
	public void release()throws Exception;
	
	
	/**Releases the given number of permits, returning them to the semaphore.
	 * @param permits
	 * @throws Exception
	 */
	public void release(int permits)throws Exception;
	
	
	/**Returns the current number of permits available in this semaphore.
	 * @return	??
	 * @throws Exception
	 */
	public int availablePermits() throws Exception;
	
	
	/**Returns an estimate of the number of threads waiting to acquire.
	 * @return	??
	 * @throws Exception
	 */
	public int getQueueLength() throws Exception;
	
	
	/**Acquires a permit from this semaphore, only if one is available at the time of invocation.
	 * @return	??
	 * @throws Exception
	 */
	public boolean tryAcquire()throws Exception;
	
	
	/**Acquires the given number of permits from this semaphore, if all become available within
	 *  the given waiting time and the current thread has not been interrupted.
	 * @param permits
	 * @param timeout
	 * @param unit
	 * @return	??
	 * @throws Exception
	 */
	public boolean tryAcquire(int permits, long timeout, TimeUnit unit)throws Exception;
}

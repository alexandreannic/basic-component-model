package fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier;

import fr.upmc.components.interfaces.RequiredI;

public interface CyclicBarrierClientI extends RequiredI {

	
	/**  Waits until all parties have invoked await on this barrier.
	 * @return	??
	 * @throws Exception
	 */
	public int await() throws Exception;
	
	
	/**Returns the number of parties currently waiting at the barrier.
	 * @return	??
	 * @throws Exception
	 */
	public int getNumberWaiting() throws Exception;
	
	
	/**Returns the number of parties currently missing at the barrier.
	 * @return	??
	 * @throws Exception
	 */
	public int getNumberToWait() throws Exception;
}

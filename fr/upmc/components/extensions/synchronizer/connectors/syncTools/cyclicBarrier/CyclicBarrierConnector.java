package fr.upmc.components.extensions.synchronizer.connectors.syncTools.cyclicBarrier;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierI;

public class CyclicBarrierConnector extends AbstractConnector implements
		CyclicBarrierClientI {

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI#await()
	 */
	@Override
	public int await() throws Exception {
		return ((CyclicBarrierI)this.offering).await();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI#getNumberWaiting()
	 */
	@Override
	public int getNumberWaiting() throws Exception {
		return ((CyclicBarrierI)this.offering).getNumberWaiting();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI#getNumberToWait()
	 */
	@Override
	public int getNumberToWait() throws Exception {
		return ((CyclicBarrierI)this.offering).getNumberToWait();
	}

}

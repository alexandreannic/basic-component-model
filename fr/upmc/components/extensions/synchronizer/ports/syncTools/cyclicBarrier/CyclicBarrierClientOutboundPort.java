package fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class 		CyclicBarrierClientOutboundPort
extends AbstractOutboundPort
implements CyclicBarrierClientI
{

	public 			CyclicBarrierClientOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, CyclicBarrierClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI#await()
	 */
	@Override
	public int await() throws Exception {
		return ((CyclicBarrierClientI)this.connector).await();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI#getNumberWaiting()
	 */
	@Override
	public int getNumberWaiting() throws Exception {
		return ((CyclicBarrierClientI)this.connector).getNumberWaiting();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI#getNumberToWait()
	 */
	@Override
	public int getNumberToWait() throws Exception {
		return ((CyclicBarrierClientI)this.connector).getNumberToWait();
	}
}

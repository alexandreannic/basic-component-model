package fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.extensions.synchronizer.components.syncTools.CyclicBarrier;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierI;
import fr.upmc.components.ports.AbstractInboundPort;

public class 		CyclicBarrierInboundPort
extends AbstractInboundPort
implements CyclicBarrierI
{

	
	private static final long serialVersionUID = 1L;

	public 			CyclicBarrierInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, CyclicBarrierI.class, owner);
		
		assert uri != null && owner instanceof CyclicBarrier;
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierI#await()
	 */
	@Override
	public int 	await() throws Exception {
		
		final CyclicBarrier cb = (CyclicBarrier) this.owner;
		
		// Méthode bloquante qui attend que la barrière soit relâchée.
		return cb.handleRequestSync(
				new ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return cb.await();
					}
				});
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierI#getNumberWaiting()
	 */
	@Override
	public int getNumberWaiting() throws Exception {
		
		final CyclicBarrier cb = (CyclicBarrier) this.owner;
		
		return cb.getNumberWaiting();
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierI#getNumberToWait()
	 */
	@Override
	public int getNumberToWait() throws Exception {
		
		final CyclicBarrier cb = (CyclicBarrier) this.owner;
		
		return cb.getNumberToWait();
	}
}

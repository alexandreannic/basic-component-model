package fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.extensions.synchronizer.components.syncTools.ArrayBlockingQueue;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ArrayBlockingQueueTakeInboundPort
extends AbstractInboundPort
implements ArrayBlockingQueueTakeI
{
	private static final long serialVersionUID = 1L;
	
	public		ArrayBlockingQueueTakeInboundPort(
			String uri,
			ComponentI owner
		) throws Exception
		{
			super(uri, ArrayBlockingQueueTakeI.class, owner);
			
			assert uri != null && owner instanceof ArrayBlockingQueue;
		}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeI#take()
	 */
	@Override
	public Serializable take() throws Exception {
		
		final ArrayBlockingQueue abq = (ArrayBlockingQueue) this.owner;
		
		return abq.handleRequestSync(
				new ComponentService<Serializable>() {
					@Override
					public Serializable call() throws Exception {
							return abq.take();
					}
				});
	}
	
	
}

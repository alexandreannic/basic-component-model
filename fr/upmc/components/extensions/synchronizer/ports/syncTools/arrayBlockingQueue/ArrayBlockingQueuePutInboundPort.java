package fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue;

import java.io.Serializable;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.extensions.synchronizer.components.syncTools.ArrayBlockingQueue;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ArrayBlockingQueuePutInboundPort
extends AbstractInboundPort
implements ArrayBlockingQueuePutI
{
	private static final long serialVersionUID = 1L;
	
	public		ArrayBlockingQueuePutInboundPort(
			String uri,
			ComponentI owner
		) throws Exception
		{
			super(uri, ArrayBlockingQueuePutI.class, owner);
			
			assert uri != null && owner instanceof ArrayBlockingQueue;
		}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutI#put(java.io.Serializable)
	 */
	@Override
	public void put(final Serializable e) throws Exception {

		final ArrayBlockingQueue abq = (ArrayBlockingQueue) this.owner;
		
		abq.handleRequestSync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						abq.put(e);
						return null;
					}
				});
	}
	
	
}

package fr.upmc.components.extensions.synchronizer.connectors.syncTools.arrayBlockingQueue;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionI;

public class ArrayBlockingQueueConnectionConnector
extends AbstractConnector
implements ArrayBlockingQueueConnectionClientI
{

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionClientI#getOwnPortURI(boolean)
	 */
	@Override
	public String 		getOwnPortURI(
			boolean take
	) throws Exception
	{
		return ((ArrayBlockingQueueConnectionI)this.offering)
				.getOwnPortURI(take);
	}

}

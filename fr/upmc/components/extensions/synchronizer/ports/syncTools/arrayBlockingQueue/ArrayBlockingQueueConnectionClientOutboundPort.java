package fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * La classe <code>ArrayBlockingQueueConnectionClientOutboundPort</code>
 * implémente le port outbound pour les composants clients du composant 
 * <code>ArrayBlockingQueue</code>. Le service implanté est spécifié à travers
 * l'interface <code>ArrayBlockingQueueConnectionClientI</code>.
 */
public class ArrayBlockingQueueConnectionClientOutboundPort
extends AbstractOutboundPort
implements ArrayBlockingQueueConnectionClientI
{

	public		ArrayBlockingQueueConnectionClientOutboundPort(
		String uri, 
		ComponentI owner
	) throws Exception
	{
		super(uri, ArrayBlockingQueueConnectionClientI.class, owner);
	}
	
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionClientI#getOwnPortURI(boolean)
	 */
	@Override
	public String getOwnPortURI(
		boolean take
	) throws Exception
	{
		return ((ArrayBlockingQueueConnectionClientI)this.connector)
				.getOwnPortURI(take);
	}

}

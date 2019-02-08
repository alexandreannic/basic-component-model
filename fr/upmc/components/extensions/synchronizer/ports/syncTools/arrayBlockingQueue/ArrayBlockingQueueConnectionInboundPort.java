package fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.components.syncTools.ArrayBlockingQueue;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * La classe <code>ArrayBlockingQueueConnectionInboundPort</code> implémente le
 * port inbound d'un composant de type <code>ArrayBlockingQueue</code>. 
 * Ce port offre un service de récupération d'URI du port "d'utilisation" du 
 * composant <code>ArrayBlockingQueue</code>.
 * Ce service est spécifié à travers l'interface 
 * <code>ArrayBlockingQueueConnectionI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * Chaque méthode du service appelle la méthode correspondante du composant
 * owner de type <code>ArrayBlockingQueue</code>
 */
public class ArrayBlockingQueueConnectionInboundPort
extends AbstractInboundPort
implements ArrayBlockingQueueConnectionI
{

	private static final long serialVersionUID = 1L;
	
	
	public 			ArrayBlockingQueueConnectionInboundPort(
			String uri,
			ComponentI owner
		) throws Exception
		{
			super(uri, ArrayBlockingQueueConnectionI.class, owner);
			
			assert uri != null && owner instanceof ArrayBlockingQueue;
		}
			
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionI#getOwnPortURI(boolean)
	 */
	@Override
	public String getOwnPortURI(boolean take) throws Exception {
		
		final ArrayBlockingQueue cb = (ArrayBlockingQueue) this.owner;
		
		return cb.getOwnPortURI(take);
	}

}

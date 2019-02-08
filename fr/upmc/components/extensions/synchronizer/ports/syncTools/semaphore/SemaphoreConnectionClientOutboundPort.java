package fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * La classe <code>SemaphoreConnectionClientOutboundPort</code>
 * implémente le port outbound pour les composants clients du composant 
 * <code>Semaphore</code>. Le service implanté est spécifié à travers
 * l'interface <code>SemaphoreConnectionClientI</code>.
 */
public class SemaphoreConnectionClientOutboundPort 
extends AbstractOutboundPort
implements SemaphoreConnectionClientI{

	
	public SemaphoreConnectionClientOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, SemaphoreConnectionClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionClientI#getOwnPortURI()
	 */
	@Override
	public String getOwnPortURI() throws Exception {
		return ((SemaphoreConnectionClientI)this.connector).getOwnPortURI();
	}
	
}

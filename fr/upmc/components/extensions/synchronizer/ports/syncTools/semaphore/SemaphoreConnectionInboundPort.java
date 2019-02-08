package fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.components.syncTools.Semaphore;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * La classe <code>SemaphoreConnectionInboundPort</code> implémente le
 * port inbound d'un composant de type <code>Semaphore</code>. 
 * Ce port offre un service de récupération d'URI du port "d'utilisation" du 
 * composant <code>Semaphore</code>.
 * Ce service est spécifié à travers l'interface 
 * <code>SemaphoreConnectionI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * Chaque méthode du service appelle la méthode correspondante du composant
 * owner de type <code>Semaphore</code>
 */
public class SemaphoreConnectionInboundPort 
extends AbstractInboundPort
implements SemaphoreConnectionI
{
	
	private static final long serialVersionUID = 1L;
	
	public SemaphoreConnectionInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, SemaphoreConnectionI.class, owner);
			
			assert uri != null && owner instanceof Semaphore;
		}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionI#getOwnPortURI()
	 */
	@Override
	public String getOwnPortURI() throws Exception {
		
		final Semaphore sm = (Semaphore) this.owner;
		return sm.getOwnPortURI();
	}
	
	

}

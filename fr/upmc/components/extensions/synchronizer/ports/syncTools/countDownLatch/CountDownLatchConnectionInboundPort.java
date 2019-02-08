package fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.components.syncTools.CountDownLatch;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * La classe <code>CountDownLatchConnectionInboundPort</code> implémente le
 * port inbound d'un composant de type <code>CountDownLatch</code>. 
 * Ce port offre un service de récupération d'URI du port "d'utilisation" du 
 * composant <code>CountDownLatch</code>.
 * Ce service est spécifié à travers l'interface 
 * <code>CountDownLatchConnectionI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * Chaque méthode du service appelle la méthode correspondante du composant
 * owner de type <code>CountDownLatch</code>
 */
public class CountDownLatchConnectionInboundPort
extends AbstractInboundPort
implements CountDownLatchConnectionI
{
	
	private static final long serialVersionUID = 1L;
	
	
	public 			CountDownLatchConnectionInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, CountDownLatchConnectionI.class, owner);
			
			assert uri != null && owner instanceof CountDownLatch;
		}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionI#getOwnPortURI()
	 */
	@Override
	public String getOwnPortURI() throws Exception {

		final CountDownLatch cb = (CountDownLatch) this.owner;
		
		return cb.getOwnPortURI();
	}

}

package fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.components.syncTools.CyclicBarrier;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * La classe <code>CyclicBarrierConnectionInboundPort</code> implémente le
 * port inbound d'un composant de type <code>CyclicBarrier</code>. 
 * Ce port offre un service de récupération d'URI du port "d'utilisation" du 
 * composant <code>CyclicBarrier</code>.
 * Ce service est spécifié à travers l'interface 
 * <code>CyclicBarrierConnectionI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * Chaque méthode du service appelle la méthode correspondante du composant
 * owner de type <code>CyclicBarrier</code>
 */
public class CyclicBarrierConnectionInboundPort
extends AbstractInboundPort
implements CyclicBarrierConnectionI
{
	
	private static final long serialVersionUID = 1L;
	
	
	public 			CyclicBarrierConnectionInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, CyclicBarrierConnectionI.class, owner);
			
			assert uri != null && owner instanceof CyclicBarrier;
		}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionI#getOwnPortURI()
	 */
	@Override
	public String getOwnPortURI() throws Exception {

		final CyclicBarrier cb = (CyclicBarrier) this.owner;
		
		return cb.getOwnPortURI();
	}

}

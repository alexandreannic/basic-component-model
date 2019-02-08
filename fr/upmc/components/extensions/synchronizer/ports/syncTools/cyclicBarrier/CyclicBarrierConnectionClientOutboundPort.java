package fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * La classe <code>CyclicBarrierConnectionClientOutboundPort</code>
 * implémente le port outbound pour les composants clients du composant 
 * <code>CyclicBarrier</code>. Le service implanté est spécifié à travers
 * l'interface <code>CyclicBarrierConnectionClientI</code>.
 */
public class CyclicBarrierConnectionClientOutboundPort
extends AbstractOutboundPort
implements CyclicBarrierConnectionClientI
{

	public CyclicBarrierConnectionClientOutboundPort(
			String uri, 
			ComponentI owner
			) throws Exception
	{
		super(uri, CyclicBarrierConnectionClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionClientI#getOwnPortURI()
	 */
	@Override
	public String getOwnPortURI() throws Exception {
		return ((CyclicBarrierConnectionClientI)this.connector).getOwnPortURI();
	}

}

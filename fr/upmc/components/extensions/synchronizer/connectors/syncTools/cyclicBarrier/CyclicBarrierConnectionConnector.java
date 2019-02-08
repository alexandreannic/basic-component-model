package fr.upmc.components.extensions.synchronizer.connectors.syncTools.cyclicBarrier;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionI;

public class CyclicBarrierConnectionConnector
extends AbstractConnector
implements CyclicBarrierConnectionClientI 
{

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionClientI#getOwnPortURI()
	 */
	@Override
	public String getOwnPortURI() throws Exception {
		return ((CyclicBarrierConnectionI)this.offering).getOwnPortURI();
	}

}

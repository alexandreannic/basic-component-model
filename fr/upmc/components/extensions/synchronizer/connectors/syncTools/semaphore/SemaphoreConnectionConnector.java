package fr.upmc.components.extensions.synchronizer.connectors.syncTools.semaphore;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionI;

public class SemaphoreConnectionConnector 
extends AbstractConnector
implements SemaphoreConnectionClientI
{

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionClientI#getOwnPortURI()
	 */
	@Override
	public String getOwnPortURI() throws Exception {
		return ((SemaphoreConnectionI)this.offering).getOwnPortURI();
	}

}

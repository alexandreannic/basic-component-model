package fr.upmc.components.extensions.synchronizer.connectors.syncTools.countDownLatch;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionI;


public class CountDownLatchConnectionConnector
extends AbstractConnector
implements CountDownLatchConnectionClientI 
{
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionClientI#getOwnPortURI()
	 */
	@Override
	public String		getOwnPortURI(
	) throws Exception
	{
		return ((CountDownLatchConnectionI)this.offering).getOwnPortURI();
	}
}

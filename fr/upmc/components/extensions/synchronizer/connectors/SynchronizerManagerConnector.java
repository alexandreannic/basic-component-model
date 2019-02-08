package fr.upmc.components.extensions.synchronizer.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerI;

/**
 * BarrierManagerConnector is the connector between the client component
 * and the BarrierManager component.
 */
public class SynchronizerManagerConnector
extends AbstractConnector
implements SynchronizerManagerClientI
{
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI#provideCountDownLatchURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideCountDownLatchURI(
		String groupID,
		String componentID
	) throws Exception
	{
		return ((SynchronizerManagerI)this.offering)
				.provideCountDownLatchURI(groupID, componentID);
	}
	
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI#provideCyclicBarrierURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideCyclicBarrierURI(
		String groupID,
		String componentID
	) throws Exception
	{
		return ((SynchronizerManagerI)this.offering)
				.provideCyclicBarrierURI(groupID, componentID);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI#provideArrayBlockingQueueURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideArrayBlockingQueueURI(
			String groupID,
			String componentID
	) throws Exception
	{
		return ((SynchronizerManagerI)this.offering)
				.provideArrayBlockingQueueURI(groupID, componentID);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI#provideSemaphoreURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideSemaphoreURI(String groupID, String componentID)
			throws Exception {
		return ((SynchronizerManagerI)this.offering)
				.provideSemaphoreURI(groupID, componentID);
	}
}

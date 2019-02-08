package fr.upmc.components.extensions.synchronizer.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.components.SynchronizerManager;
import fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * La classe <code>SynchronizerManagerClientOutboundPort</code> implémente le
 * port outbound pour les composants clients du composant 
 * <code>SynchronizerManager</code>. Le service implanté est spécifié à travers
 * l'interface <code>SynchronizerManagerClientI</code>.
 */
public class SynchronizerManagerClientOutboundPort
extends AbstractOutboundPort
implements SynchronizerManagerClientI
{
	public		SynchronizerManagerClientOutboundPort(
		String uri,
		ComponentI owner
	) throws Exception {
		super(uri, SynchronizerManagerClientI.class, owner);
		
		assert uri != null && owner instanceof SynchronizerManager;
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
		return ((SynchronizerManagerClientI)this.connector)
				.provideArrayBlockingQueueURI(groupID, componentID);
	}
	
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI#provideCountDownLatchURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideCountDownLatchURI(
		String groupID,
		String componentID
	) throws Exception
	{
		return ((SynchronizerManagerClientI)this.connector)
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
		return ((SynchronizerManagerClientI)this.connector)
				.provideCyclicBarrierURI(groupID, componentID);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerClientI#provideSemaphoreURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideSemaphoreURI(
			String groupID,
			String componentID
	) throws Exception
	{
		return ((SynchronizerManagerClientI)this.connector)
				.provideSemaphoreURI(groupID, componentID);
	}
}

package fr.upmc.components.extensions.synchronizer.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.extensions.synchronizer.components.SynchronizerManager;
import fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * La classe <code>SynchronizerManagerInboundPort</code> implémente le
 * port inbound d'un composant de type <code>SynchronizerManager</code> 
 * offrant un service de récupération d'URI des composants de synchronisation.
 * Ce service est spécifié à travers l'interface 
 * <code>SynchronizerManagerI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * Chaque méthode du service appelle la méthode correspondante du composant
 * owner de type <code>SynchronizerManager</code>
 */
public class SynchronizerManagerInboundPort
extends AbstractInboundPort
implements SynchronizerManagerI
{
	private static final long serialVersionUID = 1L;
	
	
	
	public		SynchronizerManagerInboundPort(
		String uri,
		ComponentI owner
	) throws Exception {
		super(uri, SynchronizerManagerI.class, owner);
		
		assert uri != null && owner instanceof SynchronizerManager;
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerI#provideCountDownLatchURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String		provideCountDownLatchURI(
		final String groupID,
		final String componentID
	) throws Exception {
		final SynchronizerManager sm = (SynchronizerManager) this.owner;
		
		return sm.handleRequestSync(
			new ComponentService<String>() {
				@Override
				public String call() throws Exception {
					return sm.provideCountDownLatchURI(
							groupID,
							componentID);
				}
			});
	}
	
	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerI#provideCyclicBarrierURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String		provideCyclicBarrierURI(
		final String groupID,
		final String componentID
	) throws Exception {
		final SynchronizerManager sm = (SynchronizerManager) this.owner;
		
		return sm.handleRequestSync(
			new ComponentService<String>() {
				@Override
				public String call() throws Exception {
					return sm.provideCyclicBarrierURI(
							groupID,
							componentID);
				}
			});
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerI#provideArrayBlockingQueueURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideArrayBlockingQueueURI(
			final String groupID,
			final String componentID
	) throws Exception {
		final SynchronizerManager sm = (SynchronizerManager) this.owner;

		return sm.handleRequestSync(
				new ComponentService<String>() {
					@Override
					public String call() throws Exception {
						return sm.provideArrayBlockingQueueURI(
								groupID, componentID);
					}
				});
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerI#provideSemaphoreURI(java.lang.String, java.lang.String)
	 */
	@Override
	public String provideSemaphoreURI(
			final String groupID,
			final String componentID
	) throws Exception {
		final SynchronizerManager sm = (SynchronizerManager) this.owner;
		
		return sm.handleRequestSync(
				new ComponentService<String>() {
					@Override
					public String call() throws Exception {
						return sm.provideSemaphoreURI(
								groupID, componentID);
					}
				});
	}
}

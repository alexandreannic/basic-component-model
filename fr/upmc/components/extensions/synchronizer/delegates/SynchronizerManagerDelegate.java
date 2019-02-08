package fr.upmc.components.extensions.synchronizer.delegates;

import java.util.UUID;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.connectors.ConnectionBuilder;
import fr.upmc.components.extensions.synchronizer.connectors.SynchronizerManagerConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.countDownLatch.CountDownLatchConnectionConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.countDownLatch.CountDownLatchConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.cyclicBarrier.CyclicBarrierConnectionConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.cyclicBarrier.CyclicBarrierConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.semaphore.SemaphoreConnectionConnector;
import fr.upmc.components.extensions.synchronizer.connectors.syncTools.semaphore.SemaphoreConnector;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI;
import fr.upmc.components.extensions.synchronizer.ports.SynchronizerManagerClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue.ArrayBlockingQueueConnectionClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch.CountDownLatchClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch.CountDownLatchConnectionClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier.CyclicBarrierClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier.CyclicBarrierConnectionClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore.SemaphoreClientOutboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore.SemaphoreConnectionClientOutboundPort;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.components.ports.PortI;

public class SynchronizerManagerDelegate
{
	// ------------------------------------------------------------------------
	// Méthode utilisée par le composant client
	// ------------------------------------------------------------------------
	/**
	 * Fournit le port d'utilisation de l'outil de synchronisation demandé
	 * en paramètre.
	 * @param synchronizerManagerOutboundPortURI
	 * @param synchronizerManagerURI
	 * @param component 
	 * @param groupSyncID ID du groupe de synchronisation
	 * @param clas La classe du composant de synchronisation voulu
	 * @return le port d'utilisation du service de l'outil de synchronisation
	 * 			(qu'il faudra caster pour pouvoir l'utiliser)
	 * @throws Exception
	 */
	public static PortI getPort(
			String synchronizerManagerOutboundPortURI,
			String synchronizerManagerURI,
			AbstractComponent component,
			String groupSyncID,
			Class<?> clas
		) throws Exception
	{
		if(clas != null) {
			SynchronizerManagerClientOutboundPort synchronizerManagerPort =
				connectSynchronizerManager(
						synchronizerManagerOutboundPortURI,
						synchronizerManagerURI,
						component);
			component.addRequiredInterface(clas);
			
			if(clas.equals(CyclicBarrierClientI.class)) {
				return connectCyclicBarrier(
						synchronizerManagerPort,
						synchronizerManagerOutboundPortURI,
						synchronizerManagerURI,
						component,
						groupSyncID);
			}
			else if(clas.equals(CountDownLatchClientI.class)) {
				return connectCountDownLatch(
						synchronizerManagerPort,
						synchronizerManagerOutboundPortURI,
						synchronizerManagerURI,
						component,
						groupSyncID);
			}
			else if(clas.equals(ArrayBlockingQueuePutClientI.class)) {
				return connectArrayBlockingQueuePut(
						synchronizerManagerPort,
						synchronizerManagerOutboundPortURI,
						synchronizerManagerURI,
						component,
						groupSyncID);
			}
			else if(clas.equals(ArrayBlockingQueueTakeClientI.class)) {
				return connectArrayBlockingQueueTake(
						synchronizerManagerPort,
						synchronizerManagerOutboundPortURI,
						synchronizerManagerURI,
						component,
						groupSyncID);
			}
			else if(clas.equals(SemaphoreClientI.class)) {
				return connectSemaphore(
						synchronizerManagerPort,
						synchronizerManagerOutboundPortURI,
						synchronizerManagerURI,
						component,
						groupSyncID);
			}
			else {
				throw new Exception("The interface " + clas.getName() +
					" is not managed by the delegate.");
			}
		}
		else {
			throw new Exception("Class null.");
		}
	}

	
	// ------------------------------------------------------------------------
	// Méthode de connexion au SynchronizerManager
	// ------------------------------------------------------------------------
	/**
	 * Renvoie le port outbound d'utilisation du service du SynchronizerManager.
	 * @param synchronizerManagerOutboundPortURI
	 * @param synchronizerManagerURI
	 * @param component
	 * @return	??
	 * @throws Exception
	 */
	private static SynchronizerManagerClientOutboundPort connectSynchronizerManager(
		String synchronizerManagerOutboundPortURI,
		String synchronizerManagerURI,
		AbstractComponent component
	) throws Exception
	{
		PortI port =
			component.findPortFromURI(synchronizerManagerOutboundPortURI);

		if(port == null) {
			SynchronizerManagerClientOutboundPort sm =
				new SynchronizerManagerClientOutboundPort(
					synchronizerManagerOutboundPortURI,
					component
				);
			component.addPort(sm);
			sm.localPublishPort();
			// create the connector
			SynchronizerManagerConnector c = new SynchronizerManagerConnector();
			// do the connection
			ConnectionBuilder.SINGLETON.connectWith(
				synchronizerManagerURI,
				synchronizerManagerOutboundPortURI,
				c);
			
			return sm;
		}
		else {
			return (SynchronizerManagerClientOutboundPort) port;
		}
	}
	
	
	// ------------------------------------------------------------------------
	// Méthodes de simplification
	// ------------------------------------------------------------------------
	/**
	 * @param component
	 * @param cop
	 * @throws Exception
	 */
	private static void connectStep_addPort_localPublishPort(
		AbstractComponent component,
		AbstractOutboundPort cop
	) throws Exception
	{
		component.addPort(cop);
		cop.localPublishPort();
	}
	
	private static void connectStep_connectWith(
		String connPortURI,
		String synchronizerManagerOutboundPortURI,
		AbstractConnector ac
	)
	throws Exception
	{
		ConnectionBuilder.SINGLETON.connectWith(
			connPortURI,
			synchronizerManagerOutboundPortURI,
			ac
		);
	}

	// ------------------------------------------------------------------------
	// Méthodes de connexion aux outils de synchronization
	// ------------------------------------------------------------------------
	/** Permet la connexion à un outil de synchronisation de type 
	 * ArrayBlockingQueuePut
	 * @param synchronizerManagerPort
	 * @param synchronizerManagerOutboundPortURI
	 * @param synchronizerManagerURI
	 * @param component
	 * @param groupSyncID
	 * @return	??
	 * @throws Exception
	 */
	private static PortI		connectArrayBlockingQueuePut(
		SynchronizerManagerClientOutboundPort synchronizerManagerPort,
		String synchronizerManagerOutboundPortURI,
		String synchronizerManagerURI,
		AbstractComponent component,
		String groupSyncID
	) throws Exception
	{
		ArrayBlockingQueueConnectionClientOutboundPort connectionClientPort =
			new ArrayBlockingQueueConnectionClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_connectionClient",
				component);

		connectStep_addPort_localPublishPort(component, connectionClientPort);

		ArrayBlockingQueueConnectionConnector connectionConnector =
			new ArrayBlockingQueueConnectionConnector();

		String ArrayBlockingQueueURI =
			synchronizerManagerPort.provideArrayBlockingQueueURI(
				groupSyncID,
				"MyComponentName");

		connectStep_connectWith(
			ArrayBlockingQueueURI,
			synchronizerManagerOutboundPortURI + "_connectionClient",
			connectionConnector);

		String uuid = UUID.randomUUID().toString();
		
		ArrayBlockingQueuePutClientOutboundPort clientPort =
			new ArrayBlockingQueuePutClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_client" + uuid,
				component);

		connectStep_addPort_localPublishPort(component, clientPort);

		ArrayBlockingQueuePutConnector connector = new ArrayBlockingQueuePutConnector();
		
		connectStep_connectWith(
			connectionClientPort.getOwnPortURI(false),
			synchronizerManagerOutboundPortURI + "_client" + uuid,
			connector);
		
		return clientPort;
	}

	
	/**Permet la connexion à un outil de synchronisation de type 
	 * ArrayBlockingQueueTake
	 * @param synchronizerManagerPort
	 * @param synchronizerManagerOutboundPortURI
	 * @param synchronizerManagerURI
	 * @param component
	 * @param groupSyncID
	 * @return	??
	 * @throws Exception
	 */
	private static PortI		connectArrayBlockingQueueTake(
		SynchronizerManagerClientOutboundPort synchronizerManagerPort,
		String synchronizerManagerOutboundPortURI,
		String synchronizerManagerURI,
		AbstractComponent component,
		String groupSyncID
	) throws Exception
	{
		ArrayBlockingQueueConnectionClientOutboundPort connectionClientPort =
			new ArrayBlockingQueueConnectionClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_connectionClient",
				component);

		connectStep_addPort_localPublishPort(component, connectionClientPort);

		ArrayBlockingQueueConnectionConnector connectionConnector =
			new ArrayBlockingQueueConnectionConnector();

		String ArrayBlockingQueueURI =
			synchronizerManagerPort.provideArrayBlockingQueueURI(
				groupSyncID,
				"MyComponentName");

		connectStep_connectWith(
			ArrayBlockingQueueURI,
			synchronizerManagerOutboundPortURI + "_connectionClient",
			connectionConnector);

		String uuid = UUID.randomUUID().toString();
		
		ArrayBlockingQueueTakeClientOutboundPort clientPort =
			new ArrayBlockingQueueTakeClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_client" + uuid,
				component);

		connectStep_addPort_localPublishPort(component, clientPort);

		ArrayBlockingQueueTakeConnector connector = new ArrayBlockingQueueTakeConnector();
		
		connectStep_connectWith(
			connectionClientPort.getOwnPortURI(true),
			synchronizerManagerOutboundPortURI + "_client" + uuid,
			connector);
		
		return clientPort;
	}
	
	
	/**Permet la connexion à un outil de synchronisation de type 
	 * CyclicBarrier
	 * @param synchronizerManagerPort
	 * @param synchronizerManagerOutboundPortURI
	 * @param synchronizerManagerURI
	 * @param component
	 * @param groupSyncID
	 * @return	??
	 * @throws Exception
	 */
	private static PortI connectCyclicBarrier(
		SynchronizerManagerClientOutboundPort synchronizerManagerPort,
		String synchronizerManagerOutboundPortURI,
		String synchronizerManagerURI,
		AbstractComponent component,
		String groupSyncID) 
	throws Exception
	{
		CyclicBarrierConnectionClientOutboundPort connectionClientPort =
			new CyclicBarrierConnectionClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_connectionClient",
				component);

		connectStep_addPort_localPublishPort(component, connectionClientPort);

		CyclicBarrierConnectionConnector connectionConnector =
			new CyclicBarrierConnectionConnector();

		String CyclicBarrierURI =
			synchronizerManagerPort.provideCyclicBarrierURI(
				groupSyncID,
				"MyComponentName");

		connectStep_connectWith(
			CyclicBarrierURI,
			synchronizerManagerOutboundPortURI + "_connectionClient",
			connectionConnector);

		String uuid = UUID.randomUUID().toString();
		
		CyclicBarrierClientOutboundPort clientPort =
			new CyclicBarrierClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_client" + uuid,
				component);

		connectStep_addPort_localPublishPort(component, clientPort);

		CyclicBarrierConnector connector = new CyclicBarrierConnector();
		
		connectStep_connectWith(
			connectionClientPort.getOwnPortURI(),
			synchronizerManagerOutboundPortURI + "_client" + uuid,
			connector);
		
		return clientPort;
	}
	

	/**Permet la connexion à un outil de synchronisation de type 
	 * CountDownLatch
	 * @param synchronizerManagerPort
	 * @param synchronizerManagerOutboundPortURI
	 * @param synchronizerManagerURI
	 * @param component
	 * @param groupSyncID
	 * @return	??
	 * @throws Exception
	 */
	private static PortI connectCountDownLatch(
		SynchronizerManagerClientOutboundPort synchronizerManagerPort,
		String synchronizerManagerOutboundPortURI,
		String synchronizerManagerURI,
		AbstractComponent component,
		String groupSyncID) 
	throws Exception
	{
		CountDownLatchConnectionClientOutboundPort connectionClientPort =
			new CountDownLatchConnectionClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_connectionClient",
				component);

		connectStep_addPort_localPublishPort(component, connectionClientPort);

		CountDownLatchConnectionConnector connectionConnector =
			new CountDownLatchConnectionConnector();

		String CountDownLatchURI =
			synchronizerManagerPort.provideCountDownLatchURI(
				groupSyncID,
				"MyComponentName");

		connectStep_connectWith(
			CountDownLatchURI,
			synchronizerManagerOutboundPortURI + "_connectionClient",
			connectionConnector);

		String uuid = UUID.randomUUID().toString();
		
		CountDownLatchClientOutboundPort clientPort =
			new CountDownLatchClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_client" + uuid,
				component);

		connectStep_addPort_localPublishPort(component, clientPort);

		CountDownLatchConnector connector = new CountDownLatchConnector();
		
		connectStep_connectWith(
			connectionClientPort.getOwnPortURI(),
			synchronizerManagerOutboundPortURI + "_client" + uuid,
			connector);
		
		return clientPort;
	}
	

	/**Permet la connexion à un outil de synchronisation de type 
	 * Semaphore
	 * @param synchronizerManagerPort
	 * @param synchronizerManagerOutboundPortURI
	 * @param synchronizerManagerURI
	 * @param component
	 * @param groupSyncID
	 * @return	??
	 * @throws Exception
	 */
	private static PortI connectSemaphore(
		SynchronizerManagerClientOutboundPort synchronizerManagerPort,
		String synchronizerManagerOutboundPortURI,
		String synchronizerManagerURI,
		AbstractComponent component,
		String groupSyncID) 
	throws Exception
	{
		SemaphoreConnectionClientOutboundPort connectionClientPort =
			new SemaphoreConnectionClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_connectionClient",
				component);

		connectStep_addPort_localPublishPort(component, connectionClientPort);

		SemaphoreConnectionConnector connectionConnector =
			new SemaphoreConnectionConnector();

		String SemaphoreURI =
			synchronizerManagerPort.provideSemaphoreURI(
				groupSyncID,
				"MyComponentName");
		connectStep_connectWith(
			SemaphoreURI,
			synchronizerManagerOutboundPortURI + "_connectionClient",
			connectionConnector);
		
		String uuid = UUID.randomUUID().toString();
		
		SemaphoreClientOutboundPort clientPort =
			new SemaphoreClientOutboundPort(
				synchronizerManagerOutboundPortURI + "_client" + uuid,
				component);
		connectStep_addPort_localPublishPort(component, clientPort);

		SemaphoreConnector connector = new SemaphoreConnector();
		
		connectStep_connectWith(
			connectionClientPort.getOwnPortURI(),
			synchronizerManagerOutboundPortURI + "_client" + uuid,
			connector);
		
		return clientPort;
	}
}
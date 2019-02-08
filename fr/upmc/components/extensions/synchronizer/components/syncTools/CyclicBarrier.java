package fr.upmc.components.extensions.synchronizer.components.syncTools;

import java.util.UUID;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierConnectionI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierI;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier.CyclicBarrierConnectionInboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.cyclicBarrier.CyclicBarrierInboundPort;
import fr.upmc.components.ports.PortI;

public class CyclicBarrier extends AbstractComponent {

	protected int 			nbComponentsToSync ;
	protected int 			nbComponentsArrived ;
	protected String 		barrierURI ;
	protected boolean 		isDistributed ;
				
	/**
	 * Met en attente le composant appelant.
	 * Si tous les composants attendus sont arrivés, alors ils sont tous
	 * relâchés.
	 * @return Position d'arrivée sur la barrière du composant appelant
	 * @throws Exception
	 */
	public synchronized Integer await() throws Exception
	{	
		int positionOfTheCaller ;
		
		// Appel d'un composant
		// TODO: identifier le composant appelant
		this.nbComponentsArrived++ ;
		
		positionOfTheCaller = this.nbComponentsArrived ;
		
		if (nbComponentsArrived < nbComponentsToSync) {
			// On se met en attente si tous les composants ne sont pas encore
			// arrivés.
			this.wait() ;
			
		} else {
			// Si tous les composants sont arrivés, on débloque tout le monde
			this.notifyAll() ;

			// On relance le compteur de composants en attente sur la barrière
			this.nbComponentsArrived = 0 ;
		}
		
		return positionOfTheCaller ;
	}
	
	/**
	 * Give the number of components that are waiting on the barrier
	 * @return number of waiting components
	 * @throws Exception
	 */
	public synchronized int getNumberWaiting() throws Exception {
		return this.nbComponentsArrived ;
	}

	/**
	 * Give the number of components that are expected
	 * @return number of expected components
	 * @throws Exception
	 */
	public synchronized int getNumberToWait() throws Exception {
		return this.nbComponentsToSync ;
	}
	
	/**
	 * Create a new use port of the cyclic barrier for the calling component.
	 * @return the URI of the dedicated port to use for the calling component
	 * @throws Exception
	 */
	public synchronized String getOwnPortURI() throws Exception {
		
		String newURI = this.generateNewURI() ;
		
		this.addOfferedInterface(CyclicBarrierI.class) ;
		PortI p = new CyclicBarrierInboundPort(newURI, this) ;
		this.addPort(p) ;
		
		if (this.isDistributed) {
			p.publishPort() ;
		} else {
			p.localPublishPort() ;
		}
		
		return newURI ;
	}
	
	/**
	 * Create a new string (unique) to use it as a new URI
	 * @return the new URI
	 */
	private String generateNewURI() {
		return barrierURI + "_" + UUID.randomUUID().toString();
	}
	
	
	/**
	 * Create a component which have the role of a CyclicBarrier
	 * (java.util.conccurent.CyclicBarrier).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param barrierURI
	 * @param nbComponentsToSync	number of components to wait before release the barrier
	 * @param isDistributed
	 * @throws Exception
	 */
	public CyclicBarrier(
		String barrierURI,
		Integer nbComponentsToSync,
		Boolean isDistributed
		) throws Exception
	{
		// Require as many threads as there are components to synchronize.
		// Each thread have to wait until the barrier will be released.
		// TODO: Peut-être pas besoin de threads en fait, on peut utiliser ceux
		// créés par les appels RMI sur chacun des ports. Ici ça fait doublon...
		//super(nbComponentsToSync) ;
		
		// Update : no need of requiring threads here. Threads created by each
		// RMI method call will be use to synchronize the client calling.
		super(false);
		
		// Put the offered interface in the set of interfaces offered by
		// the component.
		this.addOfferedInterface(CyclicBarrierConnectionI.class) ;
		PortI p = new CyclicBarrierConnectionInboundPort(barrierURI, this) ;
		// Add the port to the set of ports of the component
		this.addPort(p) ;
		// Publish the port
		if (isDistributed) {
			// if distributed, publish on registry
			p.publishPort() ;
		} else {
			// if not distributed, a local publication is sufficient
			p.localPublishPort() ;
		}
		
		
		this.isDistributed 			= isDistributed ;
		this.barrierURI 			= barrierURI ;
		this.nbComponentsToSync 	= nbComponentsToSync ;
		this.nbComponentsArrived 	= 0 ;
	}
}

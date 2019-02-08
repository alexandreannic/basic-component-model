package fr.upmc.components.extensions.synchronizer.components.syncTools;

import java.util.UUID;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchI;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch.CountDownLatchConnectionInboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch.CountDownLatchInboundPort;
import fr.upmc.components.ports.PortI;


/**
 * A component representing a CountDownLatch
 * (java.util.concurrent.CountDownLatch).
 * 
 * La classe CountDownLatch va permettre la création d'un composant de synchronisation.
 * Cette classe va permettre à un ou plusieurs threads d'attendre que d'autres threads ont complété leurs taches
 * (en appelant la méthode await).
 * Le CountDownLatch fonctionne avec un compteur initialisé par le nombre de thread qui vont se synchroniser.
 * Ce compoteur va être décrémenté à chaque fois qu'un thread à complété son exectution (en appelant la méthode
 * count). Quand le compteur va atteindre 0, cela signifie que tout les threads ont complété leurs taches et donc
 * que les threads en attente vont pouvoir reprendre leurs taches.
 * 
 * Voici un exemple simple en pseudo code:
 * - on crée le CountDownLatch pour N count
 * - un ou plusieurs thread se mettent en attente sur le CountDownLatch en appelant await
 * - chaque thread qui termine sa tache appelle la méthode count (chaque thread peux l'appeler plusieurs fois)
 * - un ou plusieurs thread on complété leurs taches (count = 0)
 * - on relache les threads mis en attentes
 * 
 * 
 */
public class CountDownLatch
extends AbstractComponent
{
	protected String 		coutdownLatchURI;
	protected int 			nbComponentsToSync;
	protected boolean 		isDistributed;
	
	
	
	/**
	 * @param coutdownLatchURI
	 * @param nbComponentsToSync
	 * @param isDistributed
	 * @throws Exception
	 */
	public		CountDownLatch(
		String coutdownLatchURI,
		Integer nbComponentsToSync,
		Boolean isDistributed
	) throws Exception
	{
		super(false);
		
		// Put the offered interface in the set of interfaces offered
		this.addOfferedInterface(CountDownLatchConnectionI.class);
		
		PortI p = new CountDownLatchConnectionInboundPort(coutdownLatchURI, this);
		// Add the port to the set of ports of the component
		this.addPort(p);
		// Publish the port
		if(isDistributed) {
			p.publishPort();
		}
		else {
			p.localPublishPort();
		}
		
		this.coutdownLatchURI		= coutdownLatchURI;
		this.nbComponentsToSync		= nbComponentsToSync;
		this.isDistributed			= isDistributed;
	}
	
	
	
	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public synchronized void		await(
	) throws Exception
	{
		while(nbComponentsToSync > 0) {
			this.wait();
		}
	}
	
	
	public synchronized void			count(
	) throws Exception
	{
		if (nbComponentsToSync > 0) {
			// On décrémente dans tous les cas, et on regarde si ça vaut 0
			if (--nbComponentsToSync == 0) {
				this.notifyAll();
			}
		}
	}
	
	
	public synchronized int			getCount(
	) throws Exception
	{
		return nbComponentsToSync;
	}
	
	
	/**
	 * Create a new port for the calling component to use the CountDownLatch.
	 * @return The generated URI.
	 * @throws Exception
	 */
	public synchronized String		getOwnPortURI(
	) throws Exception
	{
		String newURI = coutdownLatchURI + "_" + UUID.randomUUID().toString();
		this.addOfferedInterface(CountDownLatchI.class) ;
		PortI p = new CountDownLatchInboundPort(newURI, this) ;
		this.addPort(p) ;
		
		if(isDistributed) {
			p.publishPort();
		}
		else {
			p.localPublishPort();
		}
		
		return newURI;
	}
}

package fr.upmc.components.extensions.synchronizer.components;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.extensions.synchronizer.components.syncTools.ArrayBlockingQueue;
import fr.upmc.components.extensions.synchronizer.components.syncTools.CountDownLatch;
import fr.upmc.components.extensions.synchronizer.components.syncTools.CyclicBarrier;
import fr.upmc.components.extensions.synchronizer.components.syncTools.Semaphore;
import fr.upmc.components.extensions.synchronizer.interfaces.SynchronizerManagerI;
import fr.upmc.components.extensions.synchronizer.ports.SynchronizerManagerInboundPort;
import fr.upmc.components.ports.PortI;

/**
 * Le SynchronizerManager va assurer la création de composants de synchronisation.
 * Il va ouvrir un port qui va recevoir toutes les demandes de création
 * de composants de synchronisation.
 * Ce port offre l'interface SynchronizerManagerClientI.
 * Pour chaque nouvelle requête, nous allons recevoir un appel de méthode qui va
 * correspondre au type de composant de synchronisation que nous voulons créer
 * avec en paramètre un identifiant de groupe de synchronisation.
 * Pour chaque type de composant de synchronisation, nous allons sauvegarder
 * un dictionnaire contenant les ID de groupe reçus
 * associés à l'URI du composant créé pour ce groupe.
 * A chaque requête reçue, si l'ID de groupe donné existe déjà dans la liste,
 * nous retournons juste l'URI de connexion du composant de synchronisation.
 * Sinon on crée le composant de synchronisation avant de retourner son URI.
 * Cette procédure est bien entendu thread safe.
 */
public class SynchronizerManager
extends AbstractComponent
{
//	Nous avons créé le SynchronizerManager pour des problèmes de non déterminisme
//	et de connaissance de localisation de l'objet de synchronisation.
//	Pour que les composants clients puissent se connecter aux composants
//	de synchronisation, il faut qu'ils connaissent l'URI de ce composant.
//	Hors à l'initialisation des composants, ils ne connaissent que l'identifiant
//	du groupe de synchronisation et les paramètres de l'objet de synchro
//	(nombre de clients à attendre, de permis, taille de la pile...).
	
	protected String						synchronizerManagerPortURI;
	protected static String					parser = ";;;;";
	private Boolean							distributed;
	
	// ------------------------------------------------------------------------
	// Champs à ajouter pour chaque nouvel outil de synchronisation
	// ------------------------------------------------------------------------
	// ArrayBlockingQueue
	protected static final String			arrayBlockingQueueURI = 
													"ARRAYBLOCKINGQUEUE_";
	protected Hashtable<String, String>		arrayBlockingQueueList;
	protected int							arrayBlockingQueueCount;
	
	// CountDownLatch
	protected static final String			countdownLatchURI =
													"COUNTDOWNLATCH_";
	protected Hashtable<String, String>		countDownLatchList;
	protected int							countDownLatchCount;
	
	// CyclicBarrier
	protected static final String			cyclicBarrierURI = "CYCLICBARRIER_";
	protected Hashtable<String, String>		cyclicBarrierList;
	protected int							cyclicBarrierCount;
	
	// Semaphore
	protected static final String			semaphoreURI = "SEMAPHORE_";
	protected Hashtable<String, String>		semaphoreList;
	protected int							semaphoreCount;
	
	
	
	public						SynchronizerManager(
		String synchronizerManagerPortURI,
		boolean isDistributed
	) throws Exception
	{
		super(true);
		
		this.synchronizerManagerPortURI = synchronizerManagerPortURI;

		this.addOfferedInterface(SynchronizerManagerI.class);
		PortI p = new SynchronizerManagerInboundPort(this.synchronizerManagerPortURI, this);
		this.addPort(p);
		if(isDistributed) {
			p.publishPort();
		}
		else {
			p.localPublishPort();
		}
		
		// On doit conserver cette valeur pour la création distribuée ou non
		// des objets de synchronisation
		distributed = isDistributed;
		
		// Initialisation des champs utilisés pour les outils
		this.arrayBlockingQueueList = new Hashtable<String, String>();
		this.arrayBlockingQueueCount = 0;
		this.countDownLatchList = new Hashtable<String, String>();
		this.countDownLatchCount = 0;
		this.cyclicBarrierList = new Hashtable<String, String>();
		this.cyclicBarrierCount = 0;
		this.semaphoreList = new Hashtable<String, String>();
		this.semaphoreCount = 0;
	}
	
	
	/**
	 * Cette méthode instancie le composant demandé et le démarre.
	 * @param clas Classe à créer.
	 * @param constructorParams Paramètres du constructeur de la classe.
	 * @throws Exception
	 */
	protected void				createComponent(
			Class<?> clas,
			Object[] constructorParams
	) throws Exception 
	{
		assert clas != null;
		assert constructorParams != null;
		
		Class<?>[] parameterTypes = new Class[constructorParams.length] ;
		for(int i = 0 ; i < constructorParams.length ; i++) {
			parameterTypes[i] = constructorParams[i].getClass() ;
		}
		
		Constructor<?> cons = null;
		cons = clas.getConstructor(parameterTypes);
		
		AbstractComponent component = 
				(AbstractComponent) cons.newInstance(constructorParams);
		
		component.start();
		
		// Ajouter ce composant aux composants déployés de l'assembly ? //
		// FIXME: Réference à l'assembly pour faire un shutdown propre
	}
	
	/**
	 * Décode un ID de groupe de synchronisation
	 * et retourne les informations qu'il contient sous forme de tableau.
	 * @param groupID Un ID de groupe.
	 * @return Les informations de l'ID.
	 */
	public static String[]		decodeID(
		String groupID
	) {
		assert groupID != null;
		return groupID.split(parser);
	}
	
	
	// ------------------------------------------------------------------------
	// Méthodes de mise à disposition des différents objets de synchronisation
	// ------------------------------------------------------------------------
	/**
	 * Reçoit un ID de groupe de synchronisation, la taille de la pile
	 * et retourne un ID valide pour générer un ArrayBlockingQueue.
	 * @param id Un ID de groupe de synchronisation.
	 * @param size La taille de la pile.
	 * @return Un ID valide pour générer un ArrayBlockingQueue.
	 */
	public static String		encodeArrayBlockingQueueID(
		String id,
		int size
	) {
		return size + parser + id;
	}
	
	/**
	 * see fr.upmc.components.extensions.barrier.interfaces
	 * 		.SynchronizerManagerI#provideArrayBlockingQueueURI
	 */
	public String				provideArrayBlockingQueueURI(
		String groupID,
		String componentID
	) throws Exception
	{
		String uri = null;
		synchronized(this.arrayBlockingQueueList) {
			uri = arrayBlockingQueueList.get(groupID);
			if(uri == null) {
				uri = arrayBlockingQueueURI + (++arrayBlockingQueueCount);
				String groupID_parsed[] = decodeID(groupID);
				if(groupID_parsed.length < 2) {
					throw new Exception("The group's ID doesn't seem to contain the size of the array.");
				}
				
				int arraySize = Integer.valueOf(groupID_parsed[0]);
				createComponent(
					ArrayBlockingQueue.class,
					new Object[] {
						uri,
						new Integer(arraySize),
						distributed
					}
				);
				
				arrayBlockingQueueList.put(groupID, uri);
			}
		}
		assert uri != null;
		return uri;
	}
	

	/**
	 * Reçoit un ID de groupe de synchronisation, le nombre de composants attendus
	 * et retourne un ID valide pour générer un CountDownLatch.
	 * @param id Un ID de groupe de synchronisation.
	 * @param nbWaited Le nombre de composants attendus.
	 * @return Un ID valide pour générer un CountDownLatch.
	 */
	public static String		encodeCountDownLatchID(
		String id,
		int nbWaited
	) {
		return nbWaited + parser + id;
	}
	
	/**
	 * see fr.upmc.components.extensions.barrier.interfaces
	 * 		.SynchronizerManagerI#provideCountDownLatchURI
	 */
	public String				provideCountDownLatchURI(
		String groupID,
		String componentID
	) throws Exception
	{
		String uri = null;
		synchronized(this.countDownLatchList) {
			uri = countDownLatchList.get(groupID);
			if(uri == null) {
				uri = countdownLatchURI + (++countDownLatchCount);
				String groupID_parsed[] = decodeID(groupID);
				if(groupID_parsed.length < 2) {
					throw new Exception("The group's ID doesn't seem to contain the number of components to wait.");
				}
				
				int nbComponents = Integer.valueOf(groupID_parsed[0]);
				createComponent(
					CountDownLatch.class,
					new Object[] {
						uri,
						new Integer(nbComponents),
						distributed
					}
				);
				
				countDownLatchList.put(groupID, uri);
			}
		}
		assert uri != null;
		return uri;
	}


	/**
	 * Reçoit un ID de groupe de synchronisation, le nombre de composants attendus
	 * et retourne un ID valide pour générer un CyclicBarrier.
	 * @param id Un ID de groupe de synchronisation.
	 * @param nbWaited Le nombre de composants attendus.
	 * @return Un ID valide pour générer un CyclicBarrier.
	 */
	public static String		encodeCyclicBarrierID(
		String id,
		int nbWaited
	) {
		return nbWaited + parser + id;
	}
	
	/**
	 * see fr.upmc.components.extensions.barrier.interfaces
	 * 		.SynchronizerManagerI#provideCyclicBarrierURI
	 */
	public String				provideCyclicBarrierURI(
		String groupID,
		String componentID
	) throws Exception
	{
		String uri = null;
		synchronized(this.cyclicBarrierList) {
			uri = cyclicBarrierList.get(groupID);
			if(uri == null) {
				uri = cyclicBarrierURI + (++cyclicBarrierCount);
				String groupID_parsed[] = decodeID(groupID);
				if(groupID_parsed.length < 2) {
					throw new Exception("The group's ID doesn't seem to contain the number of components to wait.");
				}
				
				int nbComponents = Integer.valueOf(groupID_parsed[0]);
				createComponent(
					CyclicBarrier.class,
					new Object[] {
						uri,
						new Integer(nbComponents),
						distributed
					}
				);
				
				cyclicBarrierList.put(groupID, uri);
			}
		}
		assert uri != null;
		return uri;
	}
	

	/**
	 * Reçoit un ID de groupe de synchronisation, le nombre de permis alloués
	 * et retourne un ID valide pour générer un Semaphore.
	 * @param id Un ID de groupe de synchronisation.
	 * @param nbPermits Le nombre de permis alloués.
	 * @return Un ID valide pour générer un Semaphore.
	 */
	public static String		encodeSemaphoreID(
		String id,
		int nbPermits
	) {
		return nbPermits + parser + id;
	}
	
	/**
	 * see fr.upmc.components.extensions.barrier.interfaces
	 * 		.SynchronizerManagerI#provideSemaphoreURI
	 */
	public String				provideSemaphoreURI(
		String groupID,
		String componentID
	) throws Exception
	{
		String uri = null;
		synchronized(this.semaphoreList) {
			uri = semaphoreList.get(groupID);
			if(uri == null) {
				uri = semaphoreURI + (++semaphoreCount);
				String groupID_parsed[] = decodeID(groupID);
				if(groupID_parsed.length < 2) {
					throw new Exception("The group's ID doesn't seem to contain the number of permits allowed.");
				}
				
				int nbPermits = Integer.valueOf(groupID_parsed[0]);
				createComponent(
					Semaphore.class,
					new Object[] {
						uri,
						new Integer(nbPermits),
						distributed
					}
				);
				
				semaphoreList.put(groupID, uri);
			}
		}
		assert uri != null;
		return uri;
	}
}

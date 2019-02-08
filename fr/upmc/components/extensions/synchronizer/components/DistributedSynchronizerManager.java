package fr.upmc.components.extensions.synchronizer.components;

import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationOutboundPort;
import fr.upmc.components.connectors.ConnectionBuilder;
import fr.upmc.components.connectors.ConnectorI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.AbstractDistributedCVM;


/**
 * Le DistributedSynchronizerManager est une sous-classe du SynchronizerManager.
 * Il est utilisé à la place de celui-ci pour créer les outils de synchronisation
 * sur différentes JVM, alors que le SynchronizerManager créé les outils sur sa JVM.
 * Cette version utilise le DynamicComponentCreator pour instancier les outils.
 * C'est dans cette classe que l'on pourra définir des politiques de déploiement,
 * par exemple pour répartir la charge sur différentes JVM.
 */
public class DistributedSynchronizerManager
extends SynchronizerManager
{	
	public							DistributedSynchronizerManager(
		String barrierManagerPortURI,
		boolean isDistributed
	) throws Exception
	{
		super(barrierManagerPortURI, isDistributed);
	}
	
	
	/**
	 * Retourne l'URI de la JVM sur laquelle le composant de synchronization 
	 * doit être déployé.
	 * Info : cette méthode n'est appelée qu'une seule fois par demande de 
	 * création d'objet de synchronisation.
	 * Cette méthode pourra être redéfinie afin de ne pas garder le déploiment
	 * en local (sur la JVM du DistributedSynchronizerManager).
	 * @return URI de la JVM ciblée.
	 */
	protected String				getJVMURIToDeploy()
	{
		// On pourra par exemple créer ici un algorithme de déploiement
		// aléatoire des composants de synchronisation sur l'ensemble des JVM
		// utilisées.
		return AbstractDistributedCVM.thisJVMURI; // local
	}
	
	
	/**
	 * Cette méthode crée le composant demandé.
	 * @param clas Classe a créer
	 * @param constructorParams Paramètres du constructeur de la classe.
	 * @throws Exception
	 */
	@Override
	protected void 					createComponent(
			Class<?> clas,
			Object[] constructorParams
	) throws Exception 
	{
		assert clas != null;
		assert constructorParams != null;
		
		// Création du composant sur le serveur de composant
		DynamicComponentCreationOutboundPort portToDCCServer =
			connectToDCCServer(getJVMURIToDeploy());
		portToDCCServer.createComponent(clas.getName(), constructorParams);
		disconnectFromDCCServer(portToDCCServer);
	}

	
	/**
	 * Etablit la connexion avec le serveur de création de composant dont l'URI
	 * est retournée par la méthode getJVMURIToDeploy.
	 * @throws Exception
	 * @return Un objet DCC_Connection_Data contenant les informations de la
	 * connexion établie.
	 */
	private DynamicComponentCreationOutboundPort 	connectToDCCServer(
			String server_uri
	) throws Exception
	{
		// Création du port et connexion au serveur de création de composants
		DynamicComponentCreationOutboundPort portToDCCServer = 
			new DynamicComponentCreationOutboundPort(this) ;
		portToDCCServer.localPublishPort() ;
	
		this.addPort(portToDCCServer);
		ConnectorI c = new DynamicComponentCreationConnector() ;
		ConnectionBuilder.SINGLETON.connectWith(
			server_uri + "-" +
				AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
			portToDCCServer.getPortURI(),
			c);

		return portToDCCServer;
	}
	
	
	/**
	 * Ferme la connexion précédemment établie avec le serveur DCC.
	 * @throws Exception
	 */
	private void 					disconnectFromDCCServer(
			DynamicComponentCreationOutboundPort portToDCCServer
	) throws Exception
	{
		// Déconnexion
		portToDCCServer.doDisconnection();
		
		// On retire le port du composant manager
		portToDCCServer.destroyPort();
	}
}

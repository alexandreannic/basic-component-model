package fr.upmc.components.extensions.examples.cyclicBarrier;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.extensions.synchronizer.components.SynchronizerManager;
import fr.upmc.components.extensions.examples.cyclicBarrier.components.ClientComponentSynchronized;


public class CVM
extends AbstractCVM
{
	protected final String	synchronizerManagerURI = "sm_uri";
	
	
	// Premier groupe de synchronisation
	protected final String	clientComponent1URI = "client1_uri";
	protected final String	clientComponent2URI = "client2_uri";
	
	protected final String groupID1 = 
			SynchronizerManager.encodeCyclicBarrierID("group1_uri", 2);
	
	
	// Deuxième groupe de synchronisation
	protected final String	clientComponent3URI = "client3_uri";
	protected final String	clientComponent4URI = "client4_uri";

	protected final String groupID2 = 
			SynchronizerManager.encodeCyclicBarrierID("group2_uri", 2);
	
	
	
	public void				deploy() throws Exception
	{
		List<ComponentI> list_cps = new ArrayList<ComponentI>();

		// Création du manager
		list_cps.add(new SynchronizerManager(
						synchronizerManagerURI,
						isDistributed));
		
		
		
		// Premier groupe de synchronisation

		list_cps.add(new ClientComponentSynchronized(
						clientComponent1URI,
						synchronizerManagerURI,
						groupID1, 4));
		list_cps.add(new ClientComponentSynchronized(
						clientComponent2URI,
						synchronizerManagerURI,
						groupID1, 6));
		
		
		// Deuxième groupe de synchronisation
		
		list_cps.add(new ClientComponentSynchronized(
						clientComponent3URI,
						synchronizerManagerURI,
						groupID2, 8));
		list_cps.add(new ClientComponentSynchronized(
						clientComponent4URI,
						synchronizerManagerURI,
						groupID2, 2));
		
		
		
		// Ajout aux composants déployés
		for (ComponentI cps : list_cps) {
			this.deployedComponents.add(cps);
		}

		
		// deployment done
		super.deploy();
	}
	
	@Override
	public void				shutdown() throws Exception
	{
		//TODO: refléchir sur comment eteindre les connection ouverte grace au deleger.
		/*ConnectionBuilder.SINGLETON.disconnectWith(barrierManagerURI,
												   clientComponent1URI,
												   c1);
		ConnectionBuilder.SINGLETON.disconnectWith(barrierManagerURI,
												   clientComponent1URI,
												   c2);
		*/
		super.shutdown();
	}
	
	
	public static void		main(String[] args)
	{
		CVM a = new CVM();
		try {
			a.deploy();
			System.out.println("starting...");
			a.start() ;
			Thread.sleep(25000L) ;
			System.out.println("shutting down...") ;
			System.out.print("\007"); System.out.flush();
			a.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

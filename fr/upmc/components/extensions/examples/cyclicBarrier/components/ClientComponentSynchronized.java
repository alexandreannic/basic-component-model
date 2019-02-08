package fr.upmc.components.extensions.examples.cyclicBarrier.components;


import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.extensions.synchronizer.delegates.SynchronizerManagerDelegate;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.cyclicBarrier.CyclicBarrierClientI;

public class ClientComponentSynchronized
extends AbstractComponent
{
	protected String									baseURI;
	protected String									synchronizerManagerURI;
	protected String									groupID;
	protected int										secondsToWait;
	
	
	public			ClientComponentSynchronized(
		String baseURI,
		String synchronizerManagerURI,
		String groupID,
		int secondsToWait
	) throws Exception
	{
		super(true);
		
		this.baseURI = baseURI;
		this.synchronizerManagerURI = synchronizerManagerURI;
		this.groupID = groupID;
		this.secondsToWait = secondsToWait;
	}
	
	
	public Void 	doSometing()
	throws Exception
	{
		System.out.println(Thread.currentThread().getId() + " : Beginning the business code");
		
		/* Pour récuperer l'adresse du SynchronizerManager,
		 * on devrait la mettre dans le fichier config.xml
		 * puisque pour le moment elle ne bouge pas.
		 * L'autre solution serait de la passer en parametre au constructeur
		 * du composant qui voudrait l'utiliser
		 * (mais on perdrait le coté dynamique de la chose).
		 */
		CyclicBarrierClientI barrierClient =
			(CyclicBarrierClientI)
				SynchronizerManagerDelegate.getPort(
					this.baseURI,
					this.synchronizerManagerURI,
					this,
					this.groupID,
					CyclicBarrierClientI.class);
		
		
		System.out.println(Thread.currentThread().getId() + " : Waiting " + secondsToWait + " seconds before sync");
		Thread.sleep(secondsToWait * 1000);
		System.out.println(Thread.currentThread().getId() + " : Synchronizing with the group");
		barrierClient.await();
		System.out.println(Thread.currentThread().getId() + " : Synchronization done");
		
		
		System.out.println(Thread.currentThread().getId() + " : Waiting " + secondsToWait + " seconds before sync");
		Thread.sleep(secondsToWait * 1000);
		System.out.println(Thread.currentThread().getId() + " : Synchronizing with the group");
		barrierClient.await();
		System.out.println(Thread.currentThread().getId() + " : Synchronization done");
		
		
		
		System.out.println(Thread.currentThread().getId() + " : Ending the business code");
	
		return null;
	}
	
	
	@Override
	public void		start()
	throws ComponentStartException
	{
		super.start();
		
		System.out.println("Starting component synchronized ...");
		final ClientComponentSynchronized ccs = this;
		
		try {
			this.handleRequestAsync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						return ccs.doSometing();
					}
				}
			);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

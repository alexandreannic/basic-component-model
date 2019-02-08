package fr.upmc.components.extensions.examples.arrayBlockingQueue.components;


import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.extensions.synchronizer.delegates.SynchronizerManagerDelegate;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueuePutClientI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.arrayBlockingQueue.ArrayBlockingQueueTakeClientI;

public class ClientComponentSynchronized
extends AbstractComponent
{
	protected String									baseURI;
	protected String									synchronizerManagerURI;
	protected String									groupID;
	protected int										secondsToWait;
	protected boolean									isConsummer;
	
	
	public			ClientComponentSynchronized(
		String baseURI,
		String synchronizerManagerURI,
		String groupID,
		int secondsToWait,
		boolean isConsummer
	) throws Exception
	{
		super(true);
		
		this.baseURI = baseURI;
		this.synchronizerManagerURI = synchronizerManagerURI;
		this.groupID = groupID;
		this.secondsToWait = secondsToWait;
		this.isConsummer = isConsummer;
	}
	
	
	public Void 	doSometing()
	throws Exception
	{
		System.out.println(Thread.currentThread().getId() + 
				" : Beginning the business code");
			
		if (isConsummer) {
			
			// CONSOMMATEUR
			
			ArrayBlockingQueueTakeClientI takeClient =
					(ArrayBlockingQueueTakeClientI)
						SynchronizerManagerDelegate.getPort(
							this.baseURI,
							this.synchronizerManagerURI,
							this,
							this.groupID,
							ArrayBlockingQueueTakeClientI.class);
			
			System.out.println(Thread.currentThread().getId() + 
					" : Attend " + secondsToWait + 
					" secondes avant de demander une consommation");
			Thread.sleep(secondsToWait * 1000);
			
			Integer entier = null;
			System.out.println(Thread.currentThread().getId() +
					" : Demande de consommation");
			
			try {
				entier = (Integer)takeClient.take();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			System.out.println(Thread.currentThread().getId() + 
					" : Entier consommé : " + entier);
			
		} else {
			
			// PRODUCTEUR
			
			ArrayBlockingQueuePutClientI putClient =
					(ArrayBlockingQueuePutClientI)
						SynchronizerManagerDelegate.getPort(
							this.baseURI,
							this.synchronizerManagerURI,
							this,
							this.groupID,
							ArrayBlockingQueuePutClientI.class);
			
			System.out.println(Thread.currentThread().getId() + 
					" : Attend " + secondsToWait + 
					" secondes avant de finir sa production");
			Thread.sleep(secondsToWait * 1000);
			
			Integer entier = 5 * secondsToWait;
			System.out.println(Thread.currentThread().getId() +
					" : Envoi d'un entier : " + entier);
			
			try {
				putClient.put(entier);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			System.out.println(Thread.currentThread().getId() +
					" : Production envoyée");
		}
		
		
		System.out.println(Thread.currentThread().getId() +
				" : Ending the business code");
		
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

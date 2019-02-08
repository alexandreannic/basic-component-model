package fr.upmc.components.extensions.examples.countDownLatch.components;


import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.extensions.synchronizer.delegates.SynchronizerManagerDelegate;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchClientI;

public class ClientComponentSynchronized
extends AbstractComponent
{
	protected String									baseURI;
	protected String									synchronizerManagerURI;
	protected String									groupID;
	protected int										secondsToWait;
	protected boolean									isWorker;
	
	
	public			ClientComponentSynchronized(
		String baseURI,
		String synchronizerManagerURI,
		String groupID,
		int secondsToWait,
		boolean isWorker
	) throws Exception
	{
		super(true);
		
		this.baseURI = baseURI;
		this.synchronizerManagerURI = synchronizerManagerURI;
		this.groupID = groupID;
		this.secondsToWait = secondsToWait;
		this.isWorker = isWorker;
	}
	
	
	public Void 	doSometing()
	throws Exception
	{
		System.out.println(Thread.currentThread().getId() + 
				" : Beginning the business code");
		
		CountDownLatchClientI port =
			(CountDownLatchClientI)
				SynchronizerManagerDelegate.getPort(
					this.baseURI,
					this.synchronizerManagerURI,
					this,
					this.groupID,
					CountDownLatchClientI.class);
			
		if (isWorker) {
			
			System.out.println(Thread.currentThread().getId() + 
					" : Attend " + secondsToWait + 
					" secondes avant de finir le travail");
			Thread.sleep(secondsToWait * 1000);
			
			try {
				port.count();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			System.out.println(Thread.currentThread().getId() + 
					" : Fin du travail pour ce worker");
			
		} else {
			
			System.out.println(Thread.currentThread().getId() +
					" : Attend la fin des workers");
			
			try {
				port.await();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			System.out.println(Thread.currentThread().getId() +
					" : Relâché car tous les workers ont fini ...");
		}
		
		
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

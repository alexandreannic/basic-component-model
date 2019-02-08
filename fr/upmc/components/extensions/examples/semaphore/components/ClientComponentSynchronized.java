package fr.upmc.components.extensions.examples.semaphore.components;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.extensions.synchronizer.delegates.SynchronizerManagerDelegate;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreClientI;

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
		System.out.println(Thread.currentThread().getId() + 
				" : Beginning the business code");

		SemaphoreClientI port = 
				(SemaphoreClientI)
				SynchronizerManagerDelegate.getPort(
						this.baseURI,
						this.synchronizerManagerURI,
						this,
						this.groupID,
						SemaphoreClientI.class);

		System.out.println(port == null);

		try{
		port.availablePermits();
		}catch(Exception e){
			System.out.println(e.toString());
		}
		
		System.out.println(Thread.currentThread().getId() + 
				" : Attend " + secondsToWait + 
				" secondes avant de finir le travail");
		Thread.sleep(secondsToWait * 1000);

		port.release();

		System.out.println(Thread.currentThread().getId() + 
				" : Fin du travail pour ce worker");




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

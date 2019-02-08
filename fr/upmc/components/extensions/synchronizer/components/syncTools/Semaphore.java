package fr.upmc.components.extensions.synchronizer.components.syncTools;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreConnectionI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.semaphore.SemaphoreI;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore.SemaphoreConnectionInboundPort;
import fr.upmc.components.extensions.synchronizer.ports.syncTools.semaphore.SemaphoreInboundPort;
import fr.upmc.components.ports.PortI;

public class Semaphore extends AbstractComponent {
	public java.util.concurrent.Semaphore semaphore;
	protected String 		barrierURI ;
	protected boolean 		isDistributed ;

	public Semaphore(String barrierURI,
			Integer permits,
			Boolean isDistributed
			) throws Exception{
		super(false);

		this.semaphore = new java.util.concurrent.Semaphore(permits);
		this.isDistributed 			= isDistributed ;
		this.barrierURI 			= barrierURI ;


		// Put the offered interface in the set of interfaces offered by
		// the component.
		this.addOfferedInterface(SemaphoreConnectionI.class);
		PortI p = new SemaphoreConnectionInboundPort(barrierURI, this);
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
	}

	public void acquire() throws Exception{
		semaphore.acquire();
	}

	public void acquire(int permits) throws Exception{
		semaphore.acquire(permits);
	}

	public void release()throws Exception{
		semaphore.release();
	}

	public void release(int permits)throws Exception{
		semaphore.release(permits);
	}

	public int availablePermits() throws Exception{
		return semaphore.availablePermits();
	}

	public int getQueueLength() throws Exception{
		return semaphore.getQueueLength();
	}

	public boolean tryAcquire()throws Exception{
		return semaphore.tryAcquire();
	}

	public boolean tryAcquire(int permits, long timeout, TimeUnit unit)throws Exception{
		return semaphore.tryAcquire(permits, timeout, unit);
	}

	public synchronized String getOwnPortURI() throws Exception {
		String newURI =  barrierURI + "_" + UUID.randomUUID().toString();
		this.addOfferedInterface(SemaphoreI.class);
		PortI p = new SemaphoreInboundPort(newURI, this);
		this.addPort(p) ;

		if (this.isDistributed) {
			p.publishPort() ;
		} else {
			p.localPublishPort() ;
		}

		return newURI ;
	}

}

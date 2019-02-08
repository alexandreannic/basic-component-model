package fr.upmc.components.extensions.synchronizer.interfaces;

import fr.upmc.components.interfaces.RequiredI;


public interface SynchronizerManagerClientI
extends RequiredI
{
	/**
	 * Receive a ID of a synchronization group and the ID of the component
	 * calling the method.
	 * Returns the URI of the ArrayBlockingQueue associated with the groupID.
	 * @param groupID ID of the synchronization group.
	 * @param componentID ID of the calling component.
	 * @return CyclicBarrier's URI.
	 * @throws Exception
	 */
	public String		provideArrayBlockingQueueURI(
		String groupID,
		String componentID
	) throws Exception;
	
	/**
	 * Receive a ID of a synchronization group and the ID of the component
	 * calling the method.
	 * Returns the URI of the CountDownLatch associated with the group D.
	 * @param groupID ID of the synchronization group.
	 * @param componentID ID of the calling component.
	 * @return CountDownLatch's URI.
	 * @throws Exception
	 */
	public String		provideCountDownLatchURI(
		String groupID,
		String componentID
	) throws Exception;
	
	/**
	 * Receive a ID of a synchronization group and the ID of the component
	 * calling the method.
	 * Returns the URI of the CyclicBarrier associated with the group D.
	 * @param groupID ID of the synchronization group.
	 * @param componentID ID of the calling component.
	 * @return CyclicBarrier's URI.
	 * @throws Exception
	 */
	public String		provideCyclicBarrierURI(
		String groupID,
		String componentID
	) throws Exception;
	
	/**
	 * Receive a ID of a synchronization group and the ID of the component
	 * calling the method.
	 * Returns the URI of the ArrayBlockingQueue associated with the groupID.
	 * @param groupID ID of the synchronization group.
	 * @param componentID ID of the calling component.
	 * @return CyclicBarrier's URI.
	 * @throws Exception
	 */
	public String		provideSemaphoreURI(
		String groupID,
		String componentID
	) throws Exception;
}

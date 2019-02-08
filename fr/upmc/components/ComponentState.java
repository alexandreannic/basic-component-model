package fr.upmc.components;

/**
 * The class <code>ComponentState</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 12 mai 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public enum				ComponentState
implements	ComponentStateI
{
	INITIALISED,		// created, awaiting start
	STARTED,			// may run tasks and services
	SHUTTINGDOWN,		// engaging shutdown, do not accept further tasks or requests
	SHUTDOOWN,			// all tasks finished
	TERMINATED			// closed, may not be used anymore
}

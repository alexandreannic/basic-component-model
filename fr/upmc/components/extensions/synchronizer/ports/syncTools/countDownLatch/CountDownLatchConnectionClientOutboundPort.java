package fr.upmc.components.extensions.synchronizer.ports.syncTools.countDownLatch;

import fr.upmc.components.ComponentI;
import fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionClientI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * La classe <code>CountDownLatchConnectionClientOutboundPort</code>
 * implémente le port outbound pour les composants clients du composant 
 * <code>CountDownLatch</code>. Le service implanté est spécifié à travers
 * l'interface <code>CountDownLatchConnectionClientI</code>.
 */
public class CountDownLatchConnectionClientOutboundPort
extends AbstractOutboundPort
implements CountDownLatchConnectionClientI
{
	
	public		CountDownLatchConnectionClientOutboundPort(
		String uri, 
		ComponentI owner
	) throws Exception
	{
		super(uri, CountDownLatchConnectionClientI.class, owner);
	}

	/**
	 * @see fr.upmc.components.extensions.synchronizer.interfaces.syncTools.countDownLatch.CountDownLatchConnectionClientI#getOwnPortURI()
	 */
	@Override
	public String		getOwnPortURI(
	) throws Exception
	{
		return ((CountDownLatchConnectionClientI)this.connector).getOwnPortURI();
	}
}

package fr.upmc.components.examples.smoothing.filter;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.examples.smoothing.sensor.SensorI;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorData;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataRequiredI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.components.ports.AbstractDataOutboundPort;

/**
 * The class <code>SensorDataOutboundPort</code> implements a port connecting
 * a fliter to the sensor.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 30 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			SensorDataOutboundPort
extends		AbstractDataOutboundPort
implements	SensorDataRequiredI.PullI,
			SensorDataRequiredI.PushI
{
	private static final long serialVersionUID = 1L;

	public				SensorDataOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(SensorDataRequiredI.PullI.class,
			  SensorDataRequiredI.PushI.class,
			  owner) ;
		assert	owner instanceof Filter ;
	}

	public				SensorDataOutboundPort(
		String portURI,
		ComponentI owner
		) throws Exception
	{
		super(portURI,
			  SensorDataRequiredI.PullI.class,
			  SensorDataRequiredI.PushI.class,
			  owner) ;
		assert	owner instanceof SensorI ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void			receive(DataRequiredI.DataI d)
	throws Exception
	{
		final Filter ib = (Filter) this.owner ;
		final SensorData sd = (SensorData) d ;
		this.owner.handleRequestAsync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ib.acceptData(sd) ;
						return null;
					}
				}) ;
	}
}

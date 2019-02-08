package fr.upmc.components.examples.smoothing.sensor;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.examples.smoothing.sensor.interfaces.SensorDataOfferedI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.ports.AbstractDataInboundPort;

/**
 * The class <code>SensorDataInboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 31 janv. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			SensorDataInboundPort
extends		AbstractDataInboundPort
implements	SensorDataOfferedI.PullI,
			SensorDataOfferedI.PushI
{
	private static final long serialVersionUID = 1L;

	public				SensorDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri,
			  SensorDataOfferedI.PullI.class,
			  SensorDataOfferedI.PushI.class,
			  owner) ;
		assert	owner instanceof SensorI ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI	get() throws Exception
	{
		final SensorI fs = (SensorI) this.owner ;
		return this.owner.handleRequestSync(
					new ComponentService<DataOfferedI.DataI>() {
						@Override
						public DataOfferedI.DataI call() throws Exception
						{
							return fs.produceSensorData() ;
						}
					}) ;
	}
}

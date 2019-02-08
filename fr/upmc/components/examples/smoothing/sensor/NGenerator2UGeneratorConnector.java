package fr.upmc.components.examples.smoothing.sensor;

import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.examples.smoothing.rng.NormalRNGenerator;
import fr.upmc.components.examples.smoothing.rng.UniformRNGenerator;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataOfferedI;
import fr.upmc.components.examples.smoothing.rng.interfaces.RNGDataRequiredI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;

/**
 * The class <code>NGenerator2UGeneratorConnector</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 29 avr. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			NGenerator2UGeneratorConnector
extends DataConnector
{
	@Override
	public DataOfferedI.DataI required2offered(DataRequiredI.DataI d)
	{
		return new UniformRNGenerator.DoubleRandomNumber(
						((RNGDataRequiredI.DoubleRandomNumberI)d).
											getTheRandomNumber());
	}

	@Override
	public DataRequiredI.DataI offered2required(DataOfferedI.DataI d)
	{
		return new NormalRNGenerator.DoubleRandomNumber(
						((RNGDataOfferedI.DoubleRandomNumberI)d).
											getTheRandomNumber());
	}
}

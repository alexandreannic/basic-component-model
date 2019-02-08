package fr.upmc.components.cvm.pre.dcc;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>DynamicComponentCreationI</code> defines the component
 * creation service offered and required interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This interface is meant to be implemented as an offered interface by
 * dynamic component creator components, and used as required interface by
 * components that want to use this service.
 * 
 * <p>Created on : 13 mars 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		DynamicComponentCreationI
extends OfferedI, RequiredI
{
	/**
	 * create a component from the class of the given class name, invoking its
	 * constructor matching the given parameters ; beware not to have parameters
	 * of base types (<code>int</code>, <code>boolean</code>, etc.) but rather
	 * reified versions (<code>Integer</code>, <code>Boolean</code>, etc.) in
	 * order for the reflection-based instantiation to work smoothly.
	 * .
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param classname
	 * @param constructorParams
	 * @throws Exception
	 */
	public void			createComponent(
		String classname,
		Object[] constructorParams
		) throws Exception ;
}

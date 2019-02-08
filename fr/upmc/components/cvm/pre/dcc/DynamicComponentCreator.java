package fr.upmc.components.cvm.pre.dcc;

import java.lang.reflect.Constructor;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>DynamicComponentCreator</code> defines components that will
 * be automatically added in each of the sites of a distributed component
 * assembly to allow for the dynamic remote creation of components on the
 * virtual where the component is running.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 13 mars 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			DynamicComponentCreator
extends		AbstractComponent
{
	protected AbstractCVM	assembly ;

	/**
	 * create the component, publish its offered interface and its inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	dynamicComponentCreationInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param assembly	assembly running the component
	 * @param dynamicComponentCreationInboundPortURI	URI of the port offering the service
	 * @throws Exception
	 */
	public				DynamicComponentCreator(
		AbstractCVM assembly,
		String dynamicComponentCreationInboundPortURI
		) throws Exception
	{
		super(true) ;

		assert	dynamicComponentCreationInboundPortURI != null ;

		this.assembly = assembly ;

		this.addOfferedInterface(DynamicComponentCreationI.class) ;
		PortI p = new DynamicComponentCreationInboundPort(
								dynamicComponentCreationInboundPortURI, this) ;
		this.addPort(p) ;
		if (AbstractCVM.isDistributed) {
			p.publishPort() ;
		} else {
			p.localPublishPort() ;
		}

		if (AbstractCVM.DEBUG) {
			System.out.println(
				"DynamicComponentCreator created with inbound port: " +
				dynamicComponentCreationInboundPortURI) ;
		}
	}

	/**
	 * create and start a component instantiated from the class of the given
	 * class name and initialised by the constructor which parameters are given.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	classname != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param classname			name of the class from which the component is created.
	 * @param constructorParams	parameters to be passed to the constructor.
	 * @throws Exception		if the creation did not succeed.
	 */
	public void		createComponent(
		String classname,
		Object[] constructorParams
		) throws Exception
	{
		assert	classname != null ;

		if (AbstractCVM.DEBUG) {
			System.out.println("DynamicComponentCreator creates: " + classname) ;
		}

		Class<?> cl = Class.forName(classname) ;
		assert	cl != null ;
		Class<?>[] parameterTypes = new Class[constructorParams.length] ;
		for (int i = 0 ; i < constructorParams.length ; i++) {
			parameterTypes[i] = constructorParams[i].getClass() ;
		}
		Constructor<?> cons = cl.getConstructor(parameterTypes) ;
		assert	cons != null ;
		AbstractComponent component =
					(AbstractComponent) cons.newInstance(constructorParams) ;
		component.start() ;
		this.assembly.addDeployedComponent(component) ;
	}
}

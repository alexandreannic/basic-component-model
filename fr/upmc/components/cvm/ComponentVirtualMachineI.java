package fr.upmc.components.cvm;

// Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
//
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.concurrent.TimeUnit;

import fr.upmc.components.ComponentI;

/**
 * The interface <code>ComponentVirtualMachineI</code> defines the common
 * behaviours of component virtual machines for component-based applications.
 *
 * <p><strong>Description</strong></p>
 * 
 * A CVM is meant to create a set of components, to initialise and to
 * interconnect them before starting them to execute the application.
 * 
 * Applications can include concurrent components, which themselves rely on the
 * Java Executor framework to execute requests with their own pool of threads.
 * Hence, CVM also expose part of the <code>ExecutorService</code> interface
 * regarding the life cycle of components and therefore their own life cycle.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true	// TODO
 * </pre>
 * 
 * <p>Created on : 2011-11-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ComponentVirtualMachineI
{
	/**
	 * instantiate, publish and interconnect the components.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	deploymentDone
	 * </pre>
	 * @throws Exception 
	 */
	public void			deploy() throws Exception ;

	/**
	 * tests if a component is in the set of deployed components on the CVM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	component != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param component	component to be tested.
	 * @return	true if component is deployed, false otherwise.
	 */
	public boolean		isDeployedComponent(ComponentI component) ;

	/**
	 * add a component to the set of deployed components on the CVM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	component != null
	 * pre	!this.isDeployedComponent(component)
	 * post	this.isDeployedComponent(component)
	 * </pre>
	 *
	 * @param component	component to be added.
	 */
	public void			addDeployedComponent(ComponentI component) ;

	/**
	 * remove a component from the set of deployed components on the CVM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	component != null
	 * pre	this.isDeployedComponent(component)
	 * post	!this.isDeployedComponent(component)
	 * </pre>
	 *
	 * @param component	component to be removed.
	 */
	public void			removeDeployedComponent(ComponentI component) ;

	/**
	 * start the execution of the components.
	 * 
	 * <pre>
	 * pre	deploymentDone
	 * post	true				// no more postconditions.
	 * </pre>
	 * @throws Exception 
	 */
	public void			start() throws Exception ;

	/**
	 * shut down the CVM, synchronising with the other JVM when distributed,
	 * i.e. all of the deployed components in the CVM are shut down; inspired
	 * from the Java Executor framework.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.isShutdown()
	 * post	this.isShutdown()
	 * </pre>
	 * 
	 * @throws Exception 
	 */
	public void			shutdown() throws Exception ;

	/**
	 * shut down the CVM now, synchronising with the other sites when
	 * distributed, i.e. all of the locally deployed components in the
	 * CVM are shut down; inspired from the Java Executor framework.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.isShutdown()
	 * post	this.isShutdown()
	 * </pre>
	 * @throws Exception 
	 */
	public void			shutdownNow() throws Exception ;

	/**
	 * check if the CVM has been shut down (i.e. all of the locally
	 * deployed components in the CVM); inspired from the Java Executor
	 * framework.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return			true if the component has been shut down.
	 */
	public boolean		isShutdown() ;

	/**
	 * check if the CVM has terminated (i.e. all of the locally deployed
	 * components in the CVM); inspired from the Java Executor framework.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return			true if the component has terminated.
	 */
	public boolean		isTerminated() ;

	/**
	 * wait for the termination of the CVM (i.e. all of the locally deployed
	 * components in the CVM); inspired from the Java Executor framework.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param timeout	the maximum time to wait.
	 * @param unit		the time unit of the timeout argument.
	 * @return			<code>true</code> if this executor terminated and <code>false</code> if the timeout elapsed before termination.
	 * @throws InterruptedException	if interrupted while waiting.
	 */
	public boolean		awaitTermination(long timeout, TimeUnit unit)
						throws InterruptedException ;
}

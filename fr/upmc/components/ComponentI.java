package fr.upmc.components;

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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.ports.PortI;

/**
 * The interface <code>ComponentI</code> serves as common supertype for all
 * classes that implements components in this component model.
 * 
 * <p><strong>Description</strong></p>
 * 
 * Components offer and require interfaces, and they provide methods to
 * retrieve their required and offered interfaces represented as instances
 * of <code>Class<?></code>.  Components have ports that are used to connect
 * them together through their interfaces.  Outbound ports expose required
 * interfaces, while inbound ones expose the offered interfaces.  Components
 * can also be queried for the ports that expose some given interface.
 * 
 * The model include both sequential and concurrent components.  As the
 * concurrent ones rely on the Java Executor framework to handle requests
 * with a pool of threads, both sequential and concurrent share the same
 * protocol to execute requests: a <code>handleRequest</code> method capable
 * of executing a <code>Callable</code> task.  Tasks then call methods on the
 * object that implement the component; these methods represent the services
 * that the component offers.
 *
 * <p>Created on : 2012-11-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ComponentI
{
	// ------------------------------------------------------------------------
	// Internal behaviour requests
	// ------------------------------------------------------------------------

	/**
	 * return true if the component is in one of the mentioned component states.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	states != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param states	states in which the components is tested to be.
	 * @return			true if the component is in one of the given states.
	 */
	public boolean		isInStateAmong(ComponentStateI[] states) ;

	/**
	 * return true if the component is in none of the mentioned component states.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	states != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param states	states in which the components is tested not to be.
	 * @return			true if the component is in none of the given states.
	 */
	public boolean		notInStateAmong(ComponentStateI[] states) ;

	/**
	 * true if the component executes concurrently with its own thread pool.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the component executes concurrently with its own threads.
	 */
	public boolean		isConcurrent();

	/**
	 * true if the component executes concurrently with its own thread pool and
	 * can schedule tasks running after a specific delay or periodically.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the component can schedule tasks running after a specific delay or periodically.
	 */
	public boolean		canScheduleTasks() ;

	// ------------------------------------------------------------------------
	// Implemented interfaces and port management
	// ------------------------------------------------------------------------

	/**
	 * return all interfaces required or offered by this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	interfaces required and offered by the component.
	 */
	public Class<?>[]	getInterfaces() ;

	/**
	 * return all the required interfaces of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	required interfaces of this component.
	 */
	public Class<?>[]	getRequiredInterfaces() ;

	/**
	 * return all the offered interfaces of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	offered interfaces of this component.
	 */
	public Class<?>[]	getOfferedInterfaces() ;

	/**
	 * add a required interface to the required interfaces of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * pre	RequiredI.class.isAssignableFrom(inter)
	 * pre	!this.isRequiredInterface(inter)
	 * post	this.isRequiredInterface(inter)
	 * </pre>
	 *
	 * @param inter	required interface to be added.
	 */
	public void			addRequiredInterface(Class<?> inter) ;

	/**
	 * remove a required interface from the required interfaces of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * pre	this.isRequiredInterface(inter)
	 * pre	this.findPortsFromInterface(inter) == null || this.findPortsFromInterface(inter).isEmpty()
	 * post	!this.isRequiredInterface(inter)
	 * </pre>
	 *
	 * @param inter required interface to be removed.
	 */
	public void			removeRequiredInterface(Class<?> inter) ;

	/**
	 * add an offered interface to the offered interfaces of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * pre	OfferedI.class.isAssignableFrom(inter)
	 * pre	!this.isOfferedInterface(inter)
	 * post	this.isOfferedInterface(inter)
	 * </pre>
	 *
	 * @param inter offered interface to be added.
	 */
	public void			addOfferedInterface(Class<?> inter) ;

	/**
	 * remove an offered interface from the offered interfaces of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * pre	this.isOfferedInterface(inter)
	 * pre	this.findPortsFromInterface(inter) == null || this.findPortsFromInterface(inter).isEmpty()
	 * post	!this.isOfferedInterface(inter)
	 * </pre>
	 *
	 * @param inter	offered interface ot be removed
	 */
	public void			removeOfferedInterface(Class<?> inter) ;

	/**
	 * check if an interface is one of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param inter	interface to be checked for.
	 * @return		true if inter is an interface of this component.
	 */
	public boolean		isInterface(Class<?> inter) ;

	/**
	 * check if an interface is a required one of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param inter	interface to be checked for.
	 * @return		true if inter is a required interface of this component.
	 */
	public boolean		isRequiredInterface(Class<?> inter) ;

	/**
	 * check if an interface is an offered one of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param inter	interface to be checked for.
	 * @return		true if inter is an offered interface of this component.
	 */
	public boolean		isOfferedInterface(Class<?> inter) ;

	/**
	 * find the ports of this component that expose the interface inter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * post	return == null || forall(PortI p : return) { inter.equals(p.getImplementedInterface()) }
	 * </pre>
	 *
	 * @param inter	interface for which ports are sought.
	 * @return		array of ports exposing inter.
	 */
	public PortI[]		findPortsFromInterface(Class<?> inter) ;

	/**
	 * finds a port of this component from its URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return == null || return.getPortURI().equals(portURI)
	 * </pre>
	 *
	 * @param portURI	the URI a the sought port.
	 * @return			the port with the given URI or null if not found.
	 */
	public PortI		findPortFromURI(String portURI) ;

	/**
	 * add a port to the set of ports of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * pre	this.equals(p.getOwner())
	 * pre	this.isInterface(p.getImplementedInterface())
	 * pre	!exist(PortI p1 : this.findPortsFromInterface(p.getImplementedInterface())) { p.equals(p1) ; }
	 * post	exist(PortI p1 : this.findPortsFromInterface(p.getImplementedInterface())) { p.equals(p1) ; }
	 * </pre>
	 *
	 * @param p		port to be added.
	 * @throws Exception 
	 */
	public void			addPort(PortI p) throws Exception ;

	/**
	 * remove a port from the set of ports of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.notInStateAmong(new ComponentStateI[]{ComponentState.TERMINATED})
	 * pre	exist(PortI p1 : this.findPortsFromInterface(p.getImplementedInterface())) { p1.equals(p)) ; }
	 * post	!exist(PortI p1 : this.findPortsFromInterface(p.getImplementedInterface())) { p1.equals(p)) ; }
	 * </pre>
	 *
	 * @param p		port to be removed.
	 * @throws Exception 
	 */
	public void			removePort(PortI p) throws Exception ;

	// ------------------------------------------------------------------------
	// Component life cycle
	// ------------------------------------------------------------------------

	/**
	 * start the component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.INITIALISED})
	 * post	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * </pre>
	 */
	public void			start() throws ComponentStartException ;

	/**
	 * shutdown the component; inspired from the Java Executor framework.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * post	this.isInStateAmong(new ComponentStateI[]{ComponentState.SHUTTINGDOWN, ComponentState.SHUTDOOWN})
	 * </pre>
	 * @throws Exception 
	 */
	public void			shutdown() throws ComponentShutdownException ;

	/**
	 * shutdown the component now; inspired from the Java Executor framework.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * post	this.isInStateAmong(new ComponentStateI[]{ComponentState.SHUTDOOWN})
	 * </pre>
	 */
	public void			shutdownNow() throws ComponentShutdownException ;

	/**
	 * check if the component has been shut down; inspired from the Java
	 * Executor framework.
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
	 * check if the component has terminated; inspired from the Java
	 * Executor framework.
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
	 * wait for the termination of the component; inspired from the Java
	 * Executor framework.
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

	// ------------------------------------------------------------------------
	// Task execution
	// ------------------------------------------------------------------------

	/**
	 * The interface <code>ComponentTask</code> is meant to group under a
	 * same interface all of the tasks for this component.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * As the Java Executor framework use <code>Callable</code> and
	 * <code>Runnable</code>, this interface is meant to be used to implement
	 * the runnable tasks of this component.
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 2012-06-12</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	ComponentTask extends Runnable {
		// marker for component tasks
	}

	/**
	 * run the component task as main task of the component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	t != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t	component task to be executed as main task.
	 * @return	a <code>Future</code> representing pending completion of the task
	 * @throws RejectedExecutionException	if this task cannot be accepted for execution.
	 */
	public Future<?>	runTask(ComponentTask t) ;

	/**
	 * schedule a task to be run after a given delay.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	this.canScheduleTasks()
	 * pre	t != null && delay > 0 && u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		task to be scheduled.
	 * @param delay	delay after which the task must be run.
	 * @param u		time unit in which the delay is expressed.
	 * @return		a <code>ScheduledFuture</code> representing pending completion of the task, and whose <code>get()</code> method will throw an exception upon cancellation.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 */
	public ScheduledFuture<?>	scheduleTask(
		ComponentTask t,
		long delay, 
		TimeUnit u) ;

	/**
	 * schedule a task that becomes enabled first after the given initial delay,
	 * and subsequently with the given period; that is executions will commence
	 * after <code>initialDelay</code> then <code>initialDelay+period</code>,
	 * then <code>initialDelay + 2 * period</code>, and so on. If any execution
	 * of the task encounters an exception, subsequent executions are suppressed.
	 * Otherwise, the task will only terminate via cancellation or termination
	 * of the executor. If any execution of this task takes longer than its
	 * period, then subsequent executions may start late, but will not
	 * concurrently execute.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	this.canScheduleTasks()
	 * pre	t != null && initialDelay >= 0 && period > 0 && u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t				task to be scheduled.
	 * @param initialDelay	delay after which the task begins to run.
	 * @param period		period between successive executions.
	 * @param u				time unit in which the initial delay and the period are expressed.
	 * @return				a <code>ScheduledFuture</code> representing pending completion of the task, and whose <code>get()</code> method will throw an exception upon cancellation.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 */
	public ScheduledFuture<?>	scheduleTaskAtFixedRate(
		ComponentTask t,
		long initialDelay,
		long period,
		TimeUnit u) ;

	/**
	 * schedule a task that becomes enabled first after the given initial delay,
	 * and subsequently with the given delay between the termination of one
	 * execution and the commencement of the next. If any execution of the task
	 * encounters an exception, subsequent executions are suppressed. Otherwise,
	 * the task will only terminate via cancellation or termination of the
	 * executor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	this.canScheduleTasks()
	 * pre	t != null && initialDelay >= 0 && delay >= 0 && u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t				task to be scheduled.
	 * @param initialDelay	delay after which the task begins to run.
	 * @param delay			delay between the termination of one execution and the beginning of the next.
	 * @param u				time unit in which the initial delay and the delay are expressed.
	 * @return				a <code>ScheduledFuture</code> representing pending completion of the task, and whose <code>get()</code> method will throw an exception upon cancellation.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 */
	public ScheduledFuture<?>	scheduleTaskWithFixedDelay(
		ComponentTask t,
		long initialDelay,
		long delay,
		TimeUnit u) ;

	// ------------------------------------------------------------------------
	// Request handling
	// ------------------------------------------------------------------------

	/**
	 * The interface <code>ComponentService</code> is meant to group under a
	 * same interface all of the requests that return results for this
	 * component.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * As the Java Executor framework use <code>Callable</code> and
	 * <code>Runnable</code>, this interface is meant to be used to implement
	 * the callable tasks representing requests to this component services.
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 2012-06-12</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public interface	ComponentService<V> extends Callable<V> {
		// marker for component requests that return results
	}

	/**
	 * execute a request represented by a <code>Callable<T></code> on the
	 * component.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * Uniform API entry to execute a call on the component.  The call, that
	 * represents a method call on the object representing the component, is
	 * embedded in a <code>Callable</code> object.  In concurrent components,
	 * the Java executor framework is used to handle such requests.  Sequential
	 * components may simply use this method to handle requests, or they may
	 * bypass it by directly calling the method on the object representing the
	 * component for the sought of efficiency.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	task != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param request	service request to be executed on the component.
	 * @return			a future value embedding the result of the task.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 * @throws Exception					if exception raised by the task.
	 */
	public <T> Future<T>	handleRequest(ComponentService<T> request)
	throws Exception ;

	/**
	 * execute a request represented by a <code>Callable<T></code> on the
	 * component, but synchronously, i.e. waiting for the result and returning
	 * it to the caller.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	task != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param request	service request to be executed on the component.
	 * @return			the result of the task.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 * @throws Exception					if exception raised by the task.
	 */
	public <T> T		handleRequestSync(ComponentService<T> request)
	throws Exception ;

	/**
	 * execute a request represented by a <code>Callable<T></code> on the
	 * component, but asynchronously, i.e. without waiting for the result but
	 * returning the control immediately to the caller.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	task != null 
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param request	service request to be executed on the component.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 * @throws Exception					if exception raised by the task.
	 */
	public void			handleRequestAsync(ComponentService<?> request)
	throws Exception ;

	/**
	 * schedule a service for execution after a given delay.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	this.canScheduleTasks()
	 * pre	s != null && delay > 0 && u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param request	service request to be scheduled.
	 * @param delay		delay after which the task must be run.
	 * @param u			time unit in which the delay is expressed.
	 * @return			a scheduled future to synchronise with the task.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 */
	public <T> ScheduledFuture<T>	scheduleRequest(
		ComponentService<T> request,
		long delay, 
		TimeUnit u) ;

	/**
	 * schedule a service for execution after a given delay, forcing the caller
	 * to wait for the result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	this.canScheduleTasks()
	 * pre	s != null && delay > 0 && u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param request	service request to be scheduled.
	 * @param delay		delay after which the task must be run.
	 * @param u			time unit in which the delay is expressed.
	 * @return			a scheduled future to synchronise with the task.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public <T> T		scheduleRequestSync(
		ComponentService<T> request,
		long delay, 
		TimeUnit u) throws InterruptedException, ExecutionException ;

	/**
	 * schedule a service for execution after a given delay, without making the
	 * caller wait of giving it a possibility to synchronise with the result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInStateAmong(new ComponentStateI[]{ComponentState.STARTED})
	 * pre	this.canScheduleTasks()
	 * pre	s != null && delay > 0 && u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param request	service request to be scheduled.
	 * @param delay		delay after which the task must be run.
	 * @param u			time unit in which the delay is expressed.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution
	 */
	public void			scheduleRequestAsync(
		ComponentService<?> request,
		long delay, 
		TimeUnit u) ;
}

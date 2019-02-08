package fr.upmc.components.exceptions;

/**
 * The class <code>ComponentShutdownException</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 18 mars 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ComponentShutdownException
extends		Exception
{
	private static final long serialVersionUID = 1L;

	public				ComponentShutdownException()
	{
		super();
	}

	public				ComponentShutdownException(
		String message,
		Throwable cause
		)
	{
		super(message, cause);
	}

	public				ComponentShutdownException(String message)
	{
		super(message);
	}

	public				ComponentShutdownException(Throwable cause)
	{
		super(cause);
	}
}

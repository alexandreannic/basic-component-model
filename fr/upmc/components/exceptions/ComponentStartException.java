package fr.upmc.components.exceptions;

/**
 * The class <code>ComponentDidNotStartException</code>
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
public class			ComponentStartException
extends		Exception
{
	private static final long serialVersionUID = 1L ;

	public				ComponentStartException()
	{
		super() ;
	}

	public				ComponentStartException(
		String message,
		Throwable cause
		)
	{
		super(message, cause) ;
	}

	public				ComponentStartException(String message)
	{
		super(message) ;
	}

	public				ComponentStartException(Throwable cause)
	{
		super(cause) ;
	}
}

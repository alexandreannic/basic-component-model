package fr.upmc.components.registry.distributedRegistry.exceptions;

/**
 * The exception <code>UncoveredKeyException</code> is thrown because a client does not found any
 * distributed registry able to handle his request.
 */
public class UncoveredKeyException extends Exception {

	private static final long serialVersionUID = 1L;

	public UncoveredKeyException() {
		super();
	}

	public UncoveredKeyException(String key, Throwable cause) {
		super("Key " + key + " is not covered by any registry.", cause);
	}

	public UncoveredKeyException(String key) {
		super("Key " + key + " is not covered by any registry.");
	}

	public UncoveredKeyException(Throwable cause) {
		super(cause);
	}
}

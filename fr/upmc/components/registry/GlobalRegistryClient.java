package fr.upmc.components.registry;

/**
 * The interface <code>GlobalRegistryClient</code> defines the function handled by a global registry
 * (simple or distributed).
 */
public interface GlobalRegistryClient {

	String sendCommand(String command) throws Exception;

	String lookup(String key) throws Exception;

	String put(String key, String value) throws Exception;

	void remove(String key) throws Exception;

	void shutdown() throws Exception;

}

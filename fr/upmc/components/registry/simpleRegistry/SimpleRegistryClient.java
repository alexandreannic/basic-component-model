package fr.upmc.components.registry.simpleRegistry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.upmc.components.registry.GlobalRegistry;
import fr.upmc.components.registry.GlobalRegistryClient;

/**
 * The class <code>RegistryClient</code> provides a convenient intermediary
 * to send requests to the registry and get answers back.
 *
 * <p><strong>Description</strong></p>
 * <p>
 * For the component model, values put in and retrieved from the registry are
 * strings with the format:
 * <p>
 * value ::= rmi=<hostname> | socket=<hostname>:<port>
 */
public class SimpleRegistryClient implements GlobalRegistryClient {

	protected static int BUFFER_SIZE = 512;
	protected InetAddress registryHost;
	protected Socket s;
	protected PrintStream ps;
	protected BufferedReader br;

	/**
	 * create a client, per JVM client object required.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 */
	public SimpleRegistryClient() {
		super();
		this.registryHost = null;
		this.s = null;
		this.ps = null;
		this.br = null;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * send a command to the registry and return the answere as a string.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param command command to be sent.
	 * @throws Exception
	 * @return string representing the result of the request.
	 */
	public String sendCommand(String command)
	throws Exception {
		String result = null;

		if (this.registryHost == null) {
			try {
				this.registryHost =
								InetAddress.getByName(GlobalRegistry.HOSTNAME);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		if (this.s == null) {
			this.s = new Socket(this.registryHost, SimpleRegistry.PORT);
			this.ps = new PrintStream(s.getOutputStream(), true);
			this.br = new BufferedReader(
							new InputStreamReader(s.getInputStream()));
		}
		ps.println(command);
		result = this.br.readLine();
		String[] tokens = result.split("\\s");
		if (!tokens[0].equals("ok")) {
			throw new Exception(result);
		}
		return tokens.length > 1 ? tokens[1] : tokens[0];
	}

	/**
	 * send a lookup command to the registry.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param key key to be looked up.
	 * @throws Exception
	 * @return result of the request.
	 */
	public synchronized String lookup(String key) throws Exception {
		return this.sendCommand("lookup " + key);
	}

	/**
	 * send a put command to the registry.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param key   key under which the information must be stored.
	 * @param value value (information) associated to the key.
	 * @throws Exception
	 */
	public synchronized String put(String key, String value) throws Exception {
		return this.sendCommand("put " + key + " " + value);
	}

	/**
	 * send a remove command to the registry.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param key key under which the value to remove is stored.
	 * @throws Exception
	 */
	public synchronized void remove(String key) throws Exception {
		this.sendCommand("remove " + key);
	}

	/**
	 * send a shutdown command to the registry.  NOT YET WORKING.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public synchronized void shutdown() throws Exception {
		this.sendCommand("shutdown");
	}
}

package fr.upmc.components.registry.distributedRegistry.centralRegistry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.upmc.components.registry.distributedRegistry.DistributedRegistry;

/**
 * The class <code>CentralRegistryClient</code> provides a convenient intermediary to send requests
 * to the central registry and get answers back.
 */
public class CentralRegistryClient {

	protected InetAddress host;
	protected Socket socket;
	protected PrintStream ps;
	protected BufferedReader br;

	public CentralRegistryClient() {
		host = null;
		socket = null;
		ps = null;
		br = null;
	}

	/**
	 * Send a command to the registry and return the answere as a string.
	 *
	 * @param command command to be sent.
	 * @return string representing the result of the request.
	 * @throws Exception
	 */
	protected String sendCommand(String command) throws Exception {
		String result = null;

		if (this.host == null) {
			try {
				// Global registry is created on the same host than the first distributed registry
				this.host = InetAddress.getByName(DistributedRegistry.HOSTNAME);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		if (this.socket == null) {
			this.socket = new Socket(this.host, CentralRegistry.PORT);
			this.ps = new PrintStream(socket.getOutputStream(), true);
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		ps.println(command);
		result = this.br.readLine();

		return result;
	}

	/**
	 * Send a command to seek a free host to the registry.
	 * <p>
	 * If a host is available, return <strong>ok <hostname></strong>, otherwise <strong>nok</strong>
	 *
	 * @param keysCoverage
	 * @return
	 * @throws Exception
	 */
	public synchronized String seekHost(String keysCoverage) throws Exception {
		String result = sendCommand("seekHost " + keysCoverage);
		String[] tokens = result.split("\\s");

		if (tokens[0].equals("ok")) {
			return tokens[1];
		}
		return "nok";
	}

	/**
	 * Send a command to seek the distributed registry which should containing the key
	 * <code>key</code>.
	 * <p>
	 * Return <strong>ok <hostname> <keys coverage></strong> otherwise, throws an exception because
	 * it should never happened.
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public synchronized String seekKey(String key) throws Exception {
		String result = sendCommand("seekKey " + key);
		String[] tokens = result.split("\\s");
		if (tokens[0].equals("ok")) {
			return tokens[1] + " " + tokens[2];
		}
		throw new Exception("Registry does not found any distributed registry containing the key " + key);
	}

	/**
	 * This function will be used by a distributed registry newly created to confirm his creation.
	 *
	 * @param keysCoverage
	 * @return
	 * @throws Exception
	 */
	public synchronized String register(String keysCoverage) throws Exception {
		String result = sendCommand("register " + keysCoverage);
		String[] tokens = result.split("\\s");
		if (tokens[0].equals("ok")) {
			return "ok";
		}
		throw new Exception(result);
	}
}

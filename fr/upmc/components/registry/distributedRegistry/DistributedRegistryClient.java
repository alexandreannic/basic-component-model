package fr.upmc.components.registry.distributedRegistry;

import fr.upmc.components.registry.GlobalRegistryClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The class <code>RegistryClient</code> provides a convenient intermediary to send requests to a
 * distributed registry and get answers back.
 */
public class DistributedRegistryClient implements GlobalRegistryClient {

	/**
	 * When a distributed registry divides, he creates another ditributed registry and transfere
	 * half of this keys on it. He may try to transfere his keys before the new registry is up and
	 * running. In this case, the distributed registry will try several times to connect.
	 */
	protected static final int connectionAttempts = 1000;

	/**
	 * Time to wait before next attempt to connect to a distributed registry.
	 */
	protected static final int timeBeforeNextAttempt = 200;

	/**
	 * Coverage of keys of the distributed registry which this client is connected.
	 */
	protected KeysCoverage keysCoverage;

	/**
	 * Address of the distributed registry
	 */
	protected InetAddress host;

	protected Socket socket;

	protected PrintStream ps;

	protected BufferedReader br;

	public DistributedRegistryClient(String hostname, String keysCoverage) {
		try {
			this.keysCoverage = new KeysCoverage(keysCoverage);
			this.host = InetAddress.getByName(hostname);
			socket = null;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public DistributedRegistryClient() {
		this(DistributedRegistry.HOSTNAME, "a-z");
	}

	public DistributedRegistryClient(String hostname) {
		this();
		try {
			host = InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * send a command to the registry distributed and return the answere as a string.
	 */
	public String sendCommand(String command) throws Exception {
		String result = null;

		if (this.socket == null) {
			boolean scanning = true;

			// Retry to connect for a given number of time
			for (int i = 0; i < connectionAttempts && scanning; i++) {
				try {
					socket = new Socket(host, DistributedRegistry.PORT);
					scanning = false;
				} catch (IOException e) {
					Thread.sleep(timeBeforeNextAttempt);
				}
			}
			if (socket == null) {
				throw new IOException("Connection attempts exceeded. Impossible to connect to " + host + ":"
								+ DistributedRegistry.PORT);
			}

			ps = new PrintStream(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}

		ps.println(command);
		result = br.readLine();

		String[] tokens = result.split("\\s");

		if (tokens[0].equals("sync")) {
			return result;
		} else if (!tokens[0].equals("ok")) {
			throw new Exception(result);
		}

		return tokens.length > 1 ? tokens[1] : tokens[0];
	}

	public synchronized String lookup(String key) throws Exception {
		return sendCommand("lookup " + key);
	}

	public synchronized String put(String key, String value) throws Exception {
		String result = sendCommand("put " + key + " " + value);
		String[] tokens = result.split(" ");

		// If key has not been added on this distributed registry, refresh the keys coverage and
		// return the new host with his coverage to handler
		if (tokens.length > 1) {
			this.keysCoverage = new KeysCoverage(tokens[1]);
			return tokens[2] + " " + tokens[3];
		}
		return result;
	}

	public synchronized void remove(String key) throws Exception {
		sendCommand("remove " + key);
	}

	public synchronized void shutdown() throws Exception {
		sendCommand("shutdown");
	}
}

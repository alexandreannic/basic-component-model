package fr.upmc.components.registry;

/**
 * The class <code>ConnectionData</code> represents the data stored by the
 * registry to know for each port on which host the port is published on the
 * RMI registry.
 */
public class ConnectionData {

	/**
	 * RMI, socket, (to be implemented), others ??
	 */
	protected ConnectionType type;
	/**
	 * host running the RMI registry on which the port is published.
	 */
	protected String hostname;
	/**
	 * port number on which the RMI registry can be called.
	 */
	protected int port;

	/**
	 * create a connection data object from the information received by the
	 * registry through a socket communication (hence strings).
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param type     type of connection
	 * @param hostname name of the host on which RMI registry the port is published.
	 * @param port     port number of the RMI registry.
	 */
	public ConnectionData(
					ConnectionType type,
					String hostname,
					int port
	) {
		super();
		this.type = type;
		this.hostname = hostname;
		this.port = port;
	}

	/**
	 * create a connection data object from the raw information received by the
	 * registry through a socket communication (hence one string).
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param value string containing the information about the publised port.
	 */
	public ConnectionData(
					String value
	) {
		String[] temp1 = value.split("=");
		if (temp1[0].equals("rmi")) {
			this.type = ConnectionType.RMI;
			this.hostname = temp1[1];
		} else {
			assert temp1[0].equals("socket");
			this.type = ConnectionType.SOCKET;
			String[] temp2 = temp1[1].split(":");
			this.hostname = temp2[0];
			this.port = Integer.parseInt(temp2[1]);
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @return the type
	 */
	public ConnectionType getType() {
		return this.type;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return this.hostname;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}
}

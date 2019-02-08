package fr.upmc.components.registry;

/**
 * The class <code>ConnectionType</code> defines an enumerated type of
 * connection that can be used in the componenbt model.
 *
 * <p><strong>Description</strong></p>
 * <p>
 * Currently RMI, that is fully implemented, and socket that is still to be
 * completed before becoming operational.
 * </p>
 */
public enum ConnectionType {
	RMI,
	SOCKET
}

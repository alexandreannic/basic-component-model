package fr.upmc.components.registry;

import java.io.IOException;
import java.io.InputStream;

/**
 * The class <code>SocketUtilities</code> implements utilities for managing
 * socket communications for the registry.
 *
 * <p><strong>Description</strong></p>
 * <p>
 * TODO: to be completed.  Really needed ?
 */
public class SocketUtilities {

	/**
	 * Size of the buffer used to read commands from the sockets.
	 */
	protected static int BUFFER_SIZE = 512;

	/**
	 * Reads the content of a socket input stream and returns it as a String.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param is input stream on which to read.
	 * @throws IOException
	 * @return the string just read.
	 */
	public static String lireInputStream(InputStream is)
	throws IOException {
		StringBuffer sb = new StringBuffer(BUFFER_SIZE);
		char[] tampon = new char[BUFFER_SIZE];

		//		int b = is.read() ;
		char b = (char) is.read();
		int i;
		//		for (i = 0 ; b != -1 ; i++) {
		for (i = 0; b != '\n'; i++) {
			tampon[i] = (char) b;
			if (i >= BUFFER_SIZE - 1) {
				sb.append(tampon, 0, BUFFER_SIZE);
				i = -1;
			}
			b = (char) is.read();
		}
		sb.append(new String(tampon, 0, i));
		return sb.toString();
	}
}

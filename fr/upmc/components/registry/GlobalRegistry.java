package fr.upmc.components.registry;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.upmc.components.cvm.config.ConfigurationFileParser;
import fr.upmc.components.cvm.config.ConfigurationParameters;

/**
 * The abstract class <code>GlobalRegistry</code> defines the common properties of the global
 * registry that registers connection information to remotely access components through their ports.
 */
public abstract class GlobalRegistry {

	/**
	 * Default name of the host running the registry; is configurable.
	 */
	public static String HOSTNAME = "localhost";

	/**
	 * Registry port number listen for commands; is configurable.
	 */
	public static int PORT = 55252;

	/**
	 * If true, an echo of some commands executed is provided on STDOUT.
	 */
	public static final boolean DEBUG0 = true;
	/**
	 * If true, a log of the commands executed is provided on STDOUT.
	 */
	public static final boolean DEBUG1 = false;

	/**
	 * Directory of information, a hashtable with String keys and values.
	 */
	protected Hashtable<String, String> directory;

	/**
	 * Configuration parameters from the configuration file.
	 */
	protected ConfigurationParameters config;

	/**
	 * Number of JVM in the current distributed component virtual machine.
	 */
	protected final int jvmInDCVM_count;

	/**
	 * The server socket used to listen on the port number PORT.
	 */
	protected ServerSocket ss;

	/**
	 * The executor service in charge of handling component requests.
	 */
	protected static ExecutorService REQUEST_HANDLER;

	/**
	 * synchroniser to finish the execution of this global registry.
	 */
	public CountDownLatch finished;

	public GlobalRegistry(String configFileName) throws Exception {
		File configFile = new File(configFileName);
		ConfigurationFileParser cfp = new ConfigurationFileParser();

		if (!cfp.validateConfigurationFile(configFile)) {
			throw new Exception("invalid configuration file " + configFileName);
		}

		config = cfp.parseConfigurationFile(configFile);
		jvmInDCVM_count = this.config.getJvms().length;
		directory = new Hashtable<String, String>(jvmInDCVM_count);
		REQUEST_HANDLER = Executors.newFixedThreadPool(jvmInDCVM_count);
		finished = new CountDownLatch(jvmInDCVM_count);

		try {
			this.ss = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package fr.upmc.components.registry.distributedRegistry;

// Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
// 
// Jacques.Malenfant@lip6.fr
// 
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
// 
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
// 
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
// 
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
// 
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;
import java.util.List;

import fr.upmc.components.registry.distributedRegistry.centralRegistry.CentralRegistryClient;
import fr.upmc.components.registry.distributedRegistry.exceptions.UncoveredKeyException;

/**
 * The class <code>DistributedRegistryHandler</code> allow to communicate with the set of
 * distributed registry. It will handle the request of a composant and redirect it to the right
 * distributed registry.
 */
public class DistributedRegistryHandler {

	protected static int BUFFER_SIZE = 512;
	protected List<DistributedRegistryClient> clients;

	public DistributedRegistryHandler() {
		clients = new ArrayList<DistributedRegistryClient>();
		clients.add(new DistributedRegistryClient());
	}

	/**
	 * This function iterate tthrough the known distributed registry to found which one handle the
	 * key.
	 *
	 * @param key
	 * @return
	 * @throws UncoveredKeyException
	 */
	protected DistributedRegistryClient searchRegistry(String key) throws UncoveredKeyException {
		for (DistributedRegistryClient registry : clients) {
			if (registry.keysCoverage.isIncluded(key)) {
				return registry;
			}
		}
		throw new UncoveredKeyException(key);
	}

	/**
	 * Send a command to the distributed registry identified as a parameter.
	 *
	 * @param command Command to send
	 * @param index   Index of the registry
	 * @return
	 * @throws Exception
	 */
	protected String sendCommand(String command, int index) throws Exception {
		return clients.get(index).sendCommand(command);
	}

	/**
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public synchronized String lookup(String key) throws Exception {
		return searchRegistry(key).lookup(key);
	}

	public synchronized void put(String key, String value) throws Exception {
		String result = searchRegistry(key).put(key, value);
		String[] tokens = result.split(" ");

		if (tokens.length > 1) {
			clients.add(new DistributedRegistryClient(tokens[0], tokens[1]));
			// Now retry to put with the clients refreshed
			this.put(key, value);
		}
	}

	public synchronized void remove(String key) throws Exception {
		searchRegistry(key).remove(key);
	}

	public synchronized void shutdown() throws Exception {
		for (int i = 0; i < clients.size(); i++) {
			this.sendCommand("shutdown", i);
		}
	}

	public static void main(String[] args) throws Exception {
		DistributedRegistryHandler c = new DistributedRegistryHandler();
		c.put("a", "value");
		c.put("b", "value");
		c.put("c", "value");
		c.put("d", "value");
		c.put("e", "value");
		// System.out.println("Lookup e...");
		// c.lookup("e");
		// System.out.println("Rm e...");
		// c.remove("e");
		// System.out.println("Lookup e...");
		// c.lookup("e");
		c.put("aa", "value");
		c.put("aba", "value");
		c.put("abaa", "value");
		c.put("abaaa", "value");
		c.put("abaaaa", "value");
		c.put("fg", "value");
		c.put("xfg", "value");

		// c.put("h", "value");
		// c.put("af", "value");
		System.out.println("END !!");
	}
}

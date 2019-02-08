package fr.upmc.components.registry.distributedRegistry;

import java.util.List;

/**
 * The class <code>KeysCoverage</code> represents the coverage of keys of a distributed registry.
 * The class includes utilities functions used by to split list of keys of a distributed registry.
 * <p>
 * TODO currently, keys must only be letters (or the function findTheCut may failed) : define
 * allowed characters and the order in char[]
 */
public class KeysCoverage {

	protected String from;
	protected String to;

	public KeysCoverage(String from, String to) {
		this.from = from;
		this.to = to;
	}

	public KeysCoverage(String coverage) {
		String[] tokens = coverage.split("-");
		this.from = tokens[0];
		this.to = tokens[1];
	}

	public KeysCoverage() {
		this("a", "z");
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * Works as well for simple key than for coverage defined like ^\s+-\s+$
	 *
	 * @param token
	 * @return
	 */
	public boolean isIncluded(String token) {
		boolean result = false;
		String[] tokens = token.split("-");

		if (from.compareToIgnoreCase(tokens[0]) <= 0 && to.compareToIgnoreCase(tokens[0]) >= 0) {
			result = true;
		}
		if (tokens.length > 1 && from.compareToIgnoreCase(tokens[1]) <= 0 && to.compareToIgnoreCase(tokens[0]) >= 1) {
			result = result && true;
		}
		return result;
	}

	/**
	 * Computes the infimum of the coverage of keys of a registry from his list of keys
	 * <p>
	 * For example, if the lists <code>keys</code> contains the keys <strong>aaa aba aca d</strong>,
	 * his infimum is <strong>ac</strong>. Then this registry will keep the key <strong>aaa</strong>
	 * and <strong>aba</strong>.
	 *
	 * @param keys
	 * @return
	 */
	public static String findTheCut(List<String> keys) {
		keys.sort(null);

		String key_first = (keys.size() <= 3) ? keys.get(0) : keys.get((keys.size()) / 2 - 1);
		String key_mid = (keys.size() <= 3) ? keys.get(1) : keys.get((keys.size()) / 2);

		int i = 0;
		while (key_first.charAt(i) == key_mid.charAt(i)) {
			i++;
		}
		i++;

		return key_mid.substring(0, i);
	}

	/**
	 * Compute the supremum of a distributed registry from the infimum of the distributed registry
	 * which will create him.
	 *
	 * @param boundary
	 * @return
	 */
	public static String nextBoundary(String boundary) {
		return boundary + "a";

		// This following lines implemented an other version of the split. Keep it in comment
		// because this version might be better.

		// char[] new_boundary = boundary.toCharArray();
		// int i = new_boundary.length - 1;
		//
		// while (i >= 0) {
		// if (new_boundary[i] == 'z') {
		// new_boundary[i] = 'a';
		// }
		// else {
		// new_boundary[i] = (char) (new_boundary[i] + 1);
		// break;
		// }
		// i--;
		// }
		//
		// if (i < 0) {
		// new_boundary = new char[new_boundary.length + 1];
		// Arrays.fill(new_boundary, 'a');
		// }
		// return new String(new_boundary);
	}

	@Override
	public String toString() {
		return from + "-" + to;
	}
}

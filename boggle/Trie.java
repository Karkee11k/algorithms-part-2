import java.util.LinkedList;
import java.util.Queue;

/**
 * The class Trie is a special symbol table for string keys. It is
 * faster than hashing and flexible than binary search trees. It
 * supports ordered operations such as floor, ceil, max, min, ...,
 * and character based operations.
 *
 * @author Karthikeyan
 */
public class Trie {
	private static final int R = 26; // value for the R way trie
	private Node root; 			     // root of the trie

	/**
	 * Inserts the key paired with the value in the trie.
	 * 
	 * @param key   the key
	 * @param value the value to be paired with the key
	 */
	public void put(String key, int value) {
		root = put(root, key, value, 0);
	}

	/**
	 * Returns the value of the given key; -1 if key not exists.
	 * 
	 * @param key the key
	 * @return returns the value of the key
	 */
	public int get(String key) {
		Node x = get(root, key, 0);
		return x == null ? -1 : x.value;
	}

	/**
	 * Returns true if the key in the trie, else false.
	 * 
	 * @param key the key to check
	 * @return returns true if the key exists, else false
	 */
	public boolean contains(String key) {
		return get(key) != -1;
	}

	/**
	 * Returns number of keys in the trie.
	 * 
	 * @return returns number of keys in the trie
	 */
	public int size() {
		return size(root);
	}

	/**
	 * Returns true if empty, else false.
	 * 
	 * @return returns true if empty, else false
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns true if any string in the trie has the given prefix
	 * 
	 * @param prefix the prefix
	 * @return returns true if any string in the trie has the given prefix
	 */
	public boolean hasPrefix(String prefix) {
		return get(root, prefix, 0) != null;
	}

	/**
	 * Deletes the key from the trie.
	 * 
	 * @param key the key
	 */
	public void delete(String key) {
		root = delete(root, key, 0);
	}

	/**
	 * Returns all the keys in the trie.
	 * 
	 * @return returns an iterable will all keys
	 */
	public Iterable<String> keys() {
		Queue<String> q = new LinkedList<>();
		collect(root, new StringBuilder(), q);
		return q;
	}

	// returns number of keys from the given node
	private int size(Node x) {
		return x == null ? 0 : x.count;
	}

	// puts the key in the trie
	private Node put(Node x, String key, int value, int d) {
		if (x == null) x = new Node();
		if (d == key.length()) {
			x.value = value;
			x.count = 1;
		} 
		else {
			char c = charAt(key, d);
			int count = size(x.next[c]);  // previous count
			x.next[c] = put(x.next[c], key, value, d + 1);
			x.count += size(x.next[c]) - count;
		}
		return x;
	}

	// returns the char at d in s
	private char charAt(String s, int d) {
		return (char) (s.charAt(d) - 65);
	}

	// returns the dth alphabet
	private char charOf(int d) {
		return (char) (d + 65);
	}

	// adds all keys in the queue in increasing order
	private void collect(Node x, StringBuilder prefix, Queue<String> q) {
		if (x == null) return;
		if (x.value != -1) q.add(prefix.toString());
		int n = prefix.length();
		for (char r = 0; r < R; r++) {
			prefix.append(charOf(r));
			collect(x.next[r], prefix, q);
			prefix.deleteCharAt(n);
		}
	}

	// deletes the key from the trie
	private Node delete(Node x, String key, int d) {
		if (x == null) return x;
		if (d == key.length()) {
			if (x.value != -1) x.count--;
			x.value = -1;
		} 
		else {
			char c = charAt(key, d);
			int count = size(x.next[c]);
			x.next[c] = delete(x.next[c], key, d + 1);
			x.count -= count - size(x.next[c]);
		}
		return x.value == -1 && x.count == 0 ? null : x;
	}

	// returns the end node of the given key
	private Node get(Node x, String key, int d) {
		if (x == null || d == key.length()) return x;
		return get(x.next[charAt(key, d)], key, d + 1);
	}

	// node of the trie data structure
	private static class Node {
		private int value = -1;
		private int count = 0;
		private Node[] next = new Node[R];
	}
}
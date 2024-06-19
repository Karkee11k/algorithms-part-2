import java.util.LinkedList;
import java.util.Queue;

/**
 * The class Trie is a special symbol table for string keys. It is faster 
 * than hashing and flexible than binary search trees. This trie  only for
 * upper case words.
 *
 * @author Karthikeyan
 */
public class Trie {
    private static final int R = 26;
    private Node root;

    /**
     * Inserts the key paired with the value.
     * @param key the key
     * @param value the value to be paired
     */
    public void put(String key, int value) {
        root = put(root, key, value, 0);
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

    /**
     * Returns the value of the given key; -1 if not in the trie.
     * @param key the key
     * @return returns the value of the given key; -1 if not in the trie
     */
    public int get(String key) {
        Node x = get(root, key, 0);
        return x == null ? -1 : x.value;
    }

    /**
     * Returns true if given key is in the trie, else false.
     * @param key the key to check
     * @return returns true if key is in the trie, else false
     */
    public boolean contains(String key) {
        return get(key) != -1;
    }

    /**
     * Returns true if any key in the trie has the given prefix, else false.
     * @param prefix the prefix to check
     * @return returns true if any key in the trie has the given prefix, else false
     */
    public boolean hasPrefix(String prefix) {
        return get(root, prefix, 0) != null;
    }

    // returns the node of the given key
    private Node get(Node x, String key, int d) {
        if (x == null || d == key.length()) return x;
        return get(x.next[charAt(key, d)], key, d + 1);
    }

    /**
     * Returns true if empty, else false.
     * @return returns true if empty, else false
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns number of keys in the trie.
     * @return return number of keys in the trie
     */
    public int size() {
        return size(root);
    }

    // returns the size of the given node
    private int size(Node x) {
        return x == null ? 0 : x.count;
    }

    /**
     * Returns all the keys in the trie.
     * @return returns all the keys in the trie
     */
    public Iterable<String> keys() {
        Queue<String> q = new LinkedList<>();
        collect(root, new StringBuilder(), q);
        return q;
    }

    // adds all keys to the queue in increasing order 
    private void collect(Node x, StringBuilder prefix, Queue<String> q) {
        if (x == null) return;
        if (x.value != -1) q.add(prefix.toString());
        int n = prefix.length();
        for (int r = 0; r < R; r++) {
            prefix.append(alphabet(r));
            collect(x.next[r], prefix, q);
            prefix.deleteCharAt(n);
        }
    }

    /**
     * Deletes the given key from the trie.
     * @param key the key to delete
     */
    public void delete(String key) {
        root = delete(root, key, 0);
    }

    // deletes the given key
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

    // returns the dth alphabet
    private char alphabet(int d) {
        return (char) (d + 'A');
    }

    // returns the char at d in s
    private char charAt(String s, int d) {
        return (char) (s.charAt(d) - 'A');
    }

    // node of the trie
    private static class Node {
        int value = -1;
        int count = 0;
        Node[] next = new Node[R];
    }

    // unit test
    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.put("SHELL", 1);
        trie.put("MAD", 2);
        trie.put("KARKEE", 119);
        trie.put("FRIEND", 1);
        trie.put("FRIED", 9);

        String word = "FRIED";
        System.out.printf("contains(\"%s\"): %s\n", word, trie.contains(word));
        System.out.println("size(): " + trie.size());
        trie.delete(word);
        System.out.printf("\"%s\" is deleted.\n", word);
        System.out.printf("contains(\"%s\"): %s\n", word, trie.contains(word));
        System.out.println("size(): " + trie.size());
        word = "MA";
        System.out.printf("hasPrefix(\"%s\"): %s\n", word, trie.hasPrefix(word));

        System.out.print("\nKeys in the trie: ");
        for (String key : trie.keys())
            System.out.print(key + ", ");
        System.out.println();
    }
}
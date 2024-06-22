import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * The class BurrowsWheeler provides static methods to transform and inverse transform 
 * a sequence of characters using Burrows-Wheeler data compression algorithm.
 * 
 * @author Karthikeyan
 */
public class BurrowsWheeler {
    private static final int R = 256;

    /**
     * Reads from standard input, applies Burrows-Wheeler transform and writes
     * to standard output. 
     */
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int n = csa.length();
        for (int i = 0; i < n; i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        } 
        for (int i = 0; i < n; i++) 
            BinaryStdOut.write(s.charAt((csa.index(i) - 1 + n) % n));
        BinaryStdOut.close();
    }

    /**
     * Reads from standard input, applies Burrows-Wheeler inverse transform and writes
     * to standard output
     */
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();
        char[] s = t.clone();
        int[] next = new int[s.length];
        sort(s, next); 
        
        for (int i = 0; i < t.length; i++, first = next[first]) 
            BinaryStdOut.write((s[first]));
        BinaryStdOut.close();
    }

    // sort the given array
    private static void sort(char[] a, int[] index) {
        int n = a.length;
        int[] count = new int[R + 1];
        char[] aux  = new char[n];

        for (char c : a) count[c + 1]++;
        for (int r = 1; r <= R; r++) 
            count[r] += count[r - 1];
        for (int i = 0; i < n; i++) {
            int j = count[a[i]]++;
            aux[j] = a[i];
            index[j] = i;
        }
        for (int i = 0; i < n; i++) 
            a[i] = aux[i];
    }

    // test client
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else inverseTransform();
    }
}

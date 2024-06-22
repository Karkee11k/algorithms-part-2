import edu.princeton.cs.algs4.StdOut;

/**
 * The class CircularSuffixArray which describes the abstraction of a sorted array
 * of the n circular suffixes of a string of length n.
 * Example: 
 *  string s = "Kira"
 *   i      Original Suffices       Sorted Suffices       index[i]
 *  ---     -----------------       ---------------       --------
 *   0            Kira                    Kira                0
 *   1            iraK                    aKir                3
 *   2            raKi                    iraK                1
 *   3            aKir                    raKi                2
 * 
 * @author Karthikeyan
 */
public class CircularSuffixArray {
    private  final int[] index;

    /**
     * Constructs the circular suffix array of the given string.
     * @param s the String 
     * @throws IllegalArgumentException if s is null
     */
    public CircularSuffixArray(String s) {
        if (s == null) 
            throw new IllegalArgumentException("Null argument not allowed.");
        int n = s.length();
        index = new int[n];
        for (int i = 0; i < n; i++) 
            index[i] = i;
        sort(s, index, 0, n - 1, 0);
    }

    /**
     * Returns the length.
     * @return returns the length
     */
    public int length() {
        return index.length;
    }

    /**
     * Returns index of ith sorted array.
     * @param i the index 
     * @throws IllegalArgumentException if i out of range
     * @return returns index of ith sorted aeeay
     */
    public int index(int i) {
        if (i < 0 || i >= index.length) 
            throw new IllegalArgumentException(i + " out of prescribed range.");
        return index[i];
    }

    // returns the dth char of ith suffix
    private static int charAt(String s, int d, int i) {
        if (d == s.length()) return -1;
        return s.charAt((d + i) % s.length());
    }

    // modified 3-way radix quicksort to sort the index
    private static void sort(String s, int[] index, int lo, int hi, int d) {
        if (hi <= lo) return;
        int lt = lo, rt = hi;
        int v = charAt(s, d, index[lo]);
        int i = lo + 1;
        while (i <= rt) {
            int t = charAt(s, d, index[i]);
            if (t < v)      swap(index, lt++, i++);
            else if (t > v) swap(index, i, rt--);
            else            i++;
        }

        sort(s, index, lo, lt - 1, d);
        if (v >= 0) sort(s, index, lt, rt, d + 1);
        sort(s, index, rt + 1, hi, d);
    }

    // swaps the two elements in the array
    private static void swap(int[] index, int i, int j) {
        int t = index[i];
        index[i] = index[j];
        index[j] = t;
    }

    // unit test (required)
    public static void main(String[] args) {
        String s = "AAA";
        CircularSuffixArray csa = new CircularSuffixArray(s);
        StdOut.println("s: " + s);
        StdOut.println("length(): " + csa.length());
        for (int i = 0; i < csa.length(); i++)
            StdOut.printf("index(%d): %d\n", i, csa.index(i));
    }
}

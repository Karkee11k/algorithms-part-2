import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * The class MoveToFront provides static methods to encode and decode a sequence
 * of characters using move-to-front encoding algorithm.
 * 
 * @author Karthikeyan
 */
public class MoveToFront {
    private static final int R = 256;

    /**
     * Reads from standard input, applies move-to-front encoding and writes to
     * standard output.
     */
    public static void encode() {
        char[] characters = new char[R];
        for (char c = 0; c < R; c++) 
            characters[c] = c;
    
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar(); 
            int i = search(characters, c);
            BinaryStdOut.write(i, 8);  
            for (; i > 0; i--) 
                characters[i] = characters[i-1];
            characters[0] = c; 
        }
        BinaryStdOut.close();
    }

    /**
     * Reads from standard input, applies move-to-front decoding and writes to
     * standard output.
     */
    public static void decode() {
        char[] characters = new char[R];
        for (char c = 0; c < R; c++) {
            characters[c] = c;
        }
    
        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readChar();
            char c = characters[i]; 
            BinaryStdOut.write(c);    
            for (; i > 0; i--) {
                characters[i] = characters[i-1];
            }
            characters[i] = c;
        }
        BinaryStdOut.close();
    }

    // returns the index of first occurence of the target
    private static int search(char[] a, char c) {
        for (int i = 0; i < a.length; i++) 
            if (a[i] == c) return i;
        return -1;
    }

    // test client
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else decode();
    }
}
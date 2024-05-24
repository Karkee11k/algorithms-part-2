import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * The class Outcast provides a method to identify an outcast among a list of
 * WordNet nouns x1, x2, ..., xn. To identify an outcast, compute the sum of
 * the distances between each noun and every other noun:
 *     d(i) = distance(xi, x1) + distance(xi, x2) + ... + distance(xi, xn)
 * Noun with the maximum distance is the outcast.
 * 
 * @author Karthikeyan
 */
public class Outcast {
    private final WordNet wordnet;

    /**
     * Initialises the instance with the given WordNet.
     * @param wordnet the WordNet 
     */
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }
    
    /**
     * Returns the outcast of the given WordNet nouns
     * @param nouns the WordNet nouns
     * @return returns the outcast
     */
    public String outcast(String[] nouns) {
        int[] distance = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            int d = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i == j) continue;
                d += wordnet.distance(nouns[i], nouns[j]);
            }
            distance[i] = d;
        }
        int outcast = 0;
        for (int i = 1; i < nouns.length; i++) 
            if (distance[outcast] < distance[i])
                outcast = i;
        return nouns[outcast];
    }

    // test client
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
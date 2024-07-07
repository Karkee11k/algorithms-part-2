import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * The class WordNet represents a digraph where each vertex is an integer that
 * represents a synset and each directed edge v -> w represents that w is a 
 * hypernym of v. It is a rooted DAG: it is acyclic and has one vertex that is
 * an ancestor of every other vertex.
 * 
 * @author Karthikeyan
 */
public class WordNet {
    // hash tables to pair the nouns with the vertices
    private final HashMap<String, List<Integer>> nouns;
    private final HashMap<Integer, String> vertices;
    private final Digraph G;
    private final SAP sap;

    /**
     * Constructs the WordNet with the given two files.
     * @param synsets the synsets file name
     * @param hypernyms the hypernyms file name
     * @throws IllegalArgumentException if any arguments are null
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) 
            throw new IllegalArgumentException("null args not allowed");

        // maping  vertices to nouns and nouns to vertices
        In in = new In(synsets);
        nouns = new HashMap<>();
        vertices = new HashMap<>();
        while (!in.isEmpty()) {
            String[] tokens = in.readLine().split(",");
            int v = Integer.parseInt(tokens[0]);
            vertices.put(v, tokens[1]);
            for (String noun : tokens[1].split(" ")) {
                List<Integer> ls = nouns.getOrDefault(noun, new LinkedList<>());
                ls.add(v);
                nouns.put(noun, ls);
            }
        }

        G = constructDigraph(hypernyms);
        sap = new SAP(G);
        if (!isRootedDAG()) 
            throw new IllegalArgumentException("Given digraph is not a rooted DAG");
    }

    /**
     * Returns the nouns in the WordNet.
     * @return returns the nouns in the WordNet
     */
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    /**
     * Returns true if the given word is in the WordNet; else false.
     * @param word the word to check
     * @throws IllegalArgumentException if the word is null
     * @return returns true if the WordNet contains the word; else false
     */
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("null args not allowed");
        return nouns.containsKey(word);
    }

    /**
     * Returns length of shortest ancestor of nounA, nounB
     * @param nounA the vertex one
     * @param nounB the vertex two
     * @throws IllegalArgumentException if arguments are null or not in the WordNet
     * @return returns length of shortest ancestor
     */
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounB) || !isNoun(nounA)) 
            throw new IllegalArgumentException("given nouns are not in WordNet");
        List<Integer> v = nouns.get(nounA);
        List<Integer> w = nouns.get(nounB);
        return sap.length(v, w);
    }

    /**
     * Returns shortest ancestor of nounA, nounB
     * @param nounA the vertex one
     * @param nounB the vertex two
     * @throws IllegalArgumentException if arguments are null or not in the WordNet
     * @return returns the shortest ancestor
     */
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounB) || !isNoun(nounA)) 
            throw new IllegalArgumentException("given nouns are not in WordNet");
        List<Integer> v = nouns.get(nounA);
        List<Integer> w = nouns.get(nounB);
        return vertices.get(sap.ancestor(v, w));
    }

    // constructs and returns the digraph with the hypernyms file
    private Digraph constructDigraph(String hypernyms) {
        Digraph G = new Digraph(vertices.size());
        In in = new In(hypernyms);
        while (!in.isEmpty()) {
            String[] tokens = in.readLine().split(",");
            int v = Integer.parseInt(tokens[0]);
            for (int i = 1; i < tokens.length; i++) {
                int w = Integer.parseInt(tokens[i]);
                G.addEdge(v, w);
            }
        }
        return G;
    }

    // returns true if the digraph is a rooted DAG
    private boolean isRootedDAG() {
        if (new DirectedCycle(G).hasCycle()) return false;

        // a rooted DAG can have only one root
        int roots = 0; 
        for (int v = 0; v < G.V(); v++) {
            if (G.outdegree(v) == 0) 
                roots++;
        }
        return roots == 1;
    }

    // unit test the code
    public static void main(String[] args) {
        String synsets = "synsets1.txt";
        String hypernyms = "hypernyms1.txt";
        WordNet wordnet = new WordNet(synsets, hypernyms);
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            String ancestor = wordnet.sap(nounA, nounB);
            int length = wordnet.distance(nounA, nounB);
            StdOut.printf("Ancestor = %s, length = %d\n", ancestor, length);
        }
    }
}

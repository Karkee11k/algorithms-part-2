import java.util.Arrays;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * The class SAP provides methods to find the shortest ancestor path and its
 * length.
 * 
 * @author Karthikeyan
 */
public class SAP {
    private final Digraph G;

    /**
     * Initialises the SAP with the given digraph
     * @param G the digraph 
     */
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("null arguments not allowed");
        this.G = new Digraph(G);    
    }

    /**
     * Returns the length of the shortest ancestor path if it exits; -1 else.
     * @param v the vertex one
     * @param w the vertex two
     * @throws IllegalArgumentException if any of the vertices are out of bounds
     * @return returns the length of the shortest ancestor path
     */
    public int length(int v, int w) {
        if (checkBounds(v) || checkBounds(w)) 
            throw new IllegalArgumentException("vertices are out of bounds");
        
        int[] disTo1 = new int[G.V()];
        int[] disTo2 = new int[G.V()];
        int length = -1;
        BFS(v, disTo1);
        BFS(w, disTo2);
    
        for (int s = 0; s < disTo1.length; s++) {
            if (disTo1[s] == -1 || disTo2[s] == -1) 
                continue;
            int d = disTo1[s] + disTo2[s];
            length = length == -1 ? d : Math.min(length, d);
        }
        return length;
    }

    /**
     * Returns the shortest ancestor of the two vertices if exists; -1 else.
     * @param v the vertex one 
     * @param w the vertex two
     * @throws IllegalArgumentException if vertices are out of bounds
     * @return returns the shortest ancestor
     */
    public int ancestor(int v, int w) {
        if (checkBounds(v) || checkBounds(w)) 
            throw new IllegalArgumentException("vertices are out of bounds");
        int[] disTo1 = new int[G.V()];
        int[] disTo2 = new int[G.V()];
        int ancestor = -1;
        BFS(v, disTo1);
        BFS(w, disTo2);

        for (int s = 0; s < disTo1.length; s++) {
            if (disTo1[s] == -1 || disTo2[s] == -1) 
                continue;
            int d = disTo1[s] + disTo2[s];
            if (ancestor == -1 || d < disTo1[ancestor] + disTo2[ancestor]) 
                ancestor = s;
        }
        return ancestor;
    }

    /**
     * Returns the length of shortest ancestral path between any vertex in v
     * and any vertex in w; -1 if no such path.
     * @param v vertex set one 
     * @param w vertex set two
     * @throws IllegalArgumentException if the arguments are null or contain 
     * null values
     * @return returns the length of shortest ancestor path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (checkNull(v) || checkNull(w)) 
            throw new IllegalArgumentException("null args not allowed");
        int[] disTo1 = new int[G.V()];
        int[] disTo2 = new int[G.V()];
        int length = -1;
        BFS(v, disTo1);
        BFS(w, disTo2);

        for (int s = 0; s < disTo1.length; s++) {
            if (disTo1[s] == Integer.MAX_VALUE || disTo2[s] == Integer.MAX_VALUE) 
                continue;
            int d = disTo1[s] + disTo2[s];
            length = length == -1 ? d : Math.min(length, d);
        }
        return length;
    }

    /**
     * Returns shortest ancestor between any vertex in v and any vertex in w;
     * -1 if no such path
     * @param v the vertex set one
     * @param w the vertex set two
     * @throws IllegalArgumentException if the arguments are null or contain
     * null values
     * @return returns the shortest ancestor
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (checkNull(v) || checkNull(w)) 
            throw new IllegalArgumentException("null args not allowed");

        int[] disTo1 = new int[G.V()];
        int[] disTo2 = new int[G.V()];
        int ancestor = -1;
        BFS(v, disTo1);
        BFS(w, disTo2);
    
        for (int s = 0; s < disTo1.length; s++) {
            if (disTo1[s] == Integer.MAX_VALUE || disTo2[s] == Integer.MAX_VALUE) 
                continue;
            int d = disTo1[s] + disTo2[s];
            if (ancestor == -1 || d < disTo1[ancestor] + disTo2[ancestor]) 
                ancestor = s;
        }
        return ancestor;
    }

    // returns true if the given vertex out of bounds
    private boolean checkBounds(int v) {
        return v < 0 || G.V() <= v;
    }

    // returns true if the iterable is null or contains null values
    private boolean checkNull(Iterable<Integer> v) {
        if (v == null) return true;
        for (Integer i : v)
            if (i == null || checkBounds(i)) 
                return true;
        return false;
    }

    // traverse the graph from the given vertex and calculates the ancestor distance
    private void BFS(Iterable<Integer> sources, int[] disTo) {
        Queue<Integer> q = new Queue<>();
        Arrays.fill(disTo, Integer.MAX_VALUE);
        for (int source : sources) {
            disTo[source] = 0;
            q.enqueue(source);
        }

        while (!q.isEmpty()) {
            int x = q.dequeue();
            for (int w : G.adj(x)) {
                if (disTo[x] + 1 < disTo[w]) {
                    q.enqueue(w);
                    disTo[w] = disTo[x] + 1;
                }
            }
        }
    }

    // traverses the graph and calculates the distance to other vertices
    private void BFS(int v, int[] disTo) {
        Queue<Integer> q = new Queue<>();
        Arrays.fill(disTo, -1);
        disTo[v] = 0;
        q.enqueue(v);

        while (!q.isEmpty()) {
            int x = q.dequeue();
            for (int w : G.adj(x)) {
                if (disTo[w] == -1) {
                    q.enqueue(w);
                    disTo[w] = disTo[x] + 1;
                }
            }
        }
    }

    // test client
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        } 
    }
}
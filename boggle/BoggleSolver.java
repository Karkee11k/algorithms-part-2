import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * The class BoggleSolver finds all valid words in a given Boggle board, using a
 * given dictionary.
 * 
 * @author Karthikeyan
 */
public class BoggleSolver {
    private static final int[] X_DIRECTION = { -1, 0, 1, -1, 1, -1, 0, 1 };
    private static final int[] Y_DIRECTION = { -1, -1, -1, 0, 0, 1, 1, 1 };
    private final Trie trie = new Trie();
    private boolean[][] marked;

    /**
     * Constructs the BoggleSolver with the given dictionary.
     * @param dictionary the dictionary
     * @throws IllegalArgumentException if dictionary is null
     */
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null)
            throw new IllegalArgumentException("Null values not allowed.");
        for (String word : dictionary)
            trie.put(word, score(word));
    }

    /**
     * Returns all the valid words can be made from the given boggle board.
     * @param board the BoggleBoard
     * @throws IllegalArgumentException if board is null
     * @return returns all the valid words can be made
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null)
            throw new IllegalArgumentException("Null values not allowed.");
        int n = board.rows();
        int m = board.cols();
        Set<String> validWords = new HashSet<>();
        StringBuilder path = new StringBuilder("0");
        marked = new boolean[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                marked[i][j] = true;
                char c = board.getLetter(i, j);
                path.setCharAt(0, c);
                if (c == 'Q') path.append('U');
                addValidWords(board, path, i, j, validWords);
                if (c == 'Q') path.deleteCharAt(1);
                marked[i][j] = false;
            }
        }
        return validWords;
    }

    /**
     * Returns the score of the given word.
     * @param word the word
     * @throws IllegalArgumentException if word is invalid
     * @return returns the score
     */
    public int scoreOf(String word) {
        return validWord(word) ? trie.get(word) : 0;
    }

    // adds all the valid words in the given set
    private void addValidWords(BoggleBoard board, StringBuilder path, int row, int col, Set<String> validWords) {
        String word = path.toString();
        if (!trie.hasPrefix(word)) return;
        if (trie.contains(word) && word.length() > 2) validWords.add(word);

        for (int i = 0; i < X_DIRECTION.length; i++) {
            int x = row + X_DIRECTION[i];
            int y = col + Y_DIRECTION[i];
            if (!validIndex(board, x, y) || marked[x][y]) continue;
            marked[x][y] = true;
            char c = board.getLetter(x, y);
            path.append(c);
            if (c == 'Q') path.append("U");
            addValidWords(board, path, x, y, validWords);
            if (c == 'Q') path.deleteCharAt(path.length() - 1);
            path.deleteCharAt(path.length() - 1);
            marked[x][y] = false;
        }
    }

    // returns true if the given index is valid, else false
    private boolean validIndex(BoggleBoard board, int x, int y) {
        return x < board.rows() && x >= 0 && y < board.cols() && y >= 0;
    }

    // returns the score of the given word
    private int score(String word) {
        int n = word.length();
        if (n == 5) return 2;
        if (n == 6) return 3;
        if (n == 7) return 5;
        return n < 5 ? 1 : 11;
    }

    // throw IllegalArgumentException if given word is invalid
    private boolean validWord(String word) {
        return trie.contains(word) && word.length() > 2;
    }

    // test client
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
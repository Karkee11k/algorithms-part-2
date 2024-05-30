import edu.princeton.cs.algs4.Stack;

/**
 * The class TopologicalGrid provides methods to get topological order of the 
 * pixel grid in vertical or horizontal.
 * 
 * @author Karthikeyan
 */
public class TopologicalGrid {
    private final int width;
    private final int height;
    private boolean[][] marked;

    /**
     * Initialises the width and height.
     * @param width the width of the grid
     * @param height the height of the grid
     */
    public TopologicalGrid(int width, int height) {
        this.width  = width;
        this.height = height;
    }
    
    /**
     * Returns the sequences of indices in topological order of vertical DAG of
     * pixel grid.
     * @return returns sequences of indices in topological order of vertical 
     * DAG of pixel grid
     */
    public Iterable<Integer[]> vertical() {
        Stack<Integer[]> order = new Stack<>();
        marked = new boolean[width][height];
        for (int x = 0; x < width; x++)
            DFSVertical(x, 0, order);
        return order;
    }

    /**
     * Returns the sequences of indices in topological order of horizontal DAG
     * of the pixel grid.
     * @return returns the sequences of indices in topological order of 
     * horizontal DAG of the pixel grid.
     */
    public Iterable<Integer[]> horizontal() {
        Stack<Integer[]> order = new Stack<>();
        marked = new boolean[width][height];
        for (int y = 0; y < height; y++)
            DFSHorizontal(0, y, order);
        return order;
    }

    // DFS for vertical DAG
    private void DFSVertical(int x, int y, Stack<Integer[]> order) {
        marked[x][y] = true;
        if (y < height - 1) {
            if (x < width - 1 && !marked[x + 1][y + 1]) DFSVertical(x + 1, y + 1, order);
            if (x > 0 && !marked[x - 1][y + 1])         DFSVertical(x - 1, y + 1, order);
            if (!marked[x][y + 1])                      DFSVertical(x, y + 1, order);
        }
        order.push(new Integer[]{x, y});
    }

    // DFS for horizontal DAG
    private void DFSHorizontal(int x, int y, Stack<Integer[]> order) {
        marked[x][y] = true;
        if (x < width - 1) {
            if (y < height - 1 && !marked[x + 1][y + 1]) DFSHorizontal(x + 1, y + 1, order);
            if (y > 0 && !marked[x + 1][y - 1])          DFSHorizontal(x + 1, y - 1, order);
            if (!marked[x + 1][y])                       DFSHorizontal(x + 1, y, order);
        }
        order.push(new Integer[]{x, y});
    }
}
import java.awt.Color;
import java.util.Arrays;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * The class SeamCarver provides methods to seam-carve a picture. Seam-carving
 * is a content-aware image resizing technique where the image is reduced in
 * one pixel of height or width at a time. A vertical seam in an image is a 
 * path of pixels connected from the top to the bottom with one pixel in each
 * row. A horizontal seam is a path of pixels connected from the left to the
 * right with one pixel in each column.
 * 
 * @author Karthikeyan
 */
public class SeamCarverDP {
    private static final double BORDER_ENERGY = 1000.0;  // border pixel energy
    private Picture picture;                             // picture to seam curve
    private double[][] energy;                           // pixel energy cache

    /**
     * Initialises the seam carver with the given picture.
     * @param picture the picture
     * @throws IllegalArgumentException if picture is null
     */
    public SeamCarverDP(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Picture is null.");
        this.picture = new Picture(picture);
        energy = new double[width()][height()];
        for (int i = 0; i < energy.length; i++)
            Arrays.fill(energy[i], Double.NaN);
    }

    /**
     * Returns the current picture.
     * @return returns the current picture
     */
    public Picture picture() {
        return new Picture(picture);
    }

    /**
     * Returns the width of the current picture.
     * @return returns the width of the current picture
     */
    public int width() {
        return picture.width();
    }

    /**
     * Returns the height of the current picture.
     * @return returns the height of the current picture
     */
    public int height() {
        return picture.height();
    }

    /**
     * Returns energy of pixel at column x and row y.
     * @param x the column index
     * @param y the row index
     * @throws IllegalArgumentException if pixel out of range
     * @return returns the energy of the pixel
     */
    public double energy(int x, int y) {
        validateRange(x, y);
        if (isBorder(x, y)) return BORDER_ENERGY;

        // caching the energy calculation
        if (Double.isNaN(energy[x][y])) {
            int xgradient = gradient(picture.getRGB(x - 1, y), picture.getRGB(x + 1, y));
            int ygradient = gradient(picture.getRGB(x, y - 1), picture.getRGB(x, y + 1));
            energy[x][y] = Math.sqrt(xgradient + ygradient);
        }
        return energy[x][y];
    }

    /**
     * Returne sequence of indices for vertical seam.
     * @return returns an array of length H such that entry y is the column 
     * number of the pixel to be removed from the row y of the image
     */
    public int[] findVerticalSeam() {
        int width = width(), height = height();
        double[][] disTo = new double[width][height];
        int[][] edgeTo   = new int[width][height];

        for (int y = 1; y < height; y++) 
            for (int x = 0; x < width; x++)
                relaxVertical(disTo, edgeTo, x, y);
        return traceVerticalSeam(disTo, edgeTo);  
    }

    /**
     * Returns sequence of indices for horizontal seam. 
     * @return returns an array of length W such that entry x is the row 
     * number of the pixel to be removed from the column x of the image
     */
    public int[] findHorizontalSeam() {
        int width = width(), height = height();
        double[][] disTo = new double[width][height];
        int[][] edgeTo   = new int[width][height];

        for (int x = 1; x < width; x++)
            for (int y = 0; y < height; y++)
                relaxHorizontal(disTo, edgeTo, x, y);
        return traceHorizontalSeam(disTo, edgeTo);
    }

    /**
     * Removes vertical seam from the current picture.
     * @param seam the seam to remove
     * @throws IllegalArgumentException if invalid seam or picture width
     * less than two
     */
    public void removeVerticalSeam(int[] seam) {
        validateVerticalSeam(seam);
        if (width() < 2)
            throw new IllegalArgumentException("Picture width less than 2.");

        Picture pic = new Picture(width() - 1, height());
        for (int y = 0; y < pic.height(); y++) {
            for (int x = 0; x < pic.width(); x++) {
                Color color = picture.get(x < seam[y] ? x : x + 1, y);
                pic.set(x, y, color);

                // avoiding energy recalculation, resetting only affected pixels
                if (x >= seam[y]) energy[x][y] = energy[x+1][y];
                if (seam[y] - 1 == x || seam[y] == x) energy[x][y] = Double.NaN;
            }
        }
        picture = pic;
    }

    /**
     * Removes horizontal seam from the current picture.
     * @param seam the seam to remove
     * @throws IllegalArgumentException if invalid seam or picture height 
     * less than two
     */
    public void removeHorizontalSeam(int[] seam) {
        validateHorizontalSeam(seam);
        if (height() < 2)
            throw new IllegalArgumentException("Picture height less than 2.");

        Picture pic = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < pic.height(); y++) {
                Color color = picture.get(x, y < seam[x] ? y : y + 1);
                pic.set(x, y, color); 

                // avoiding recalculation, resetting only the affected pixels
                if (y >= seam[x]) energy[x][y] = energy[x][y+1];     
                if (seam[x] - 1 == y || seam[x] == y) energy[x][y] = Double.NaN;    
            }
        }
        picture = pic;
    }

    // relax the edge for vertical 
    private void relaxVertical(double[][] disTo, int[][] edgeTo, int x, int y) {
        double e = energy(x, y);
        disTo[x][y] = e + disTo[x][y-1];
        edgeTo[x][y] = 0;

        if (x > 0 && disTo[x-1][y-1] + e < disTo[x][y]) {
            disTo[x][y] = disTo[x-1][y-1] + e;
            edgeTo[x][y] = -1; 
        }
        if (x < width() - 1 && disTo[x+1][y-1] + e < disTo[x][y]) {
            disTo[x][y] = disTo[x+1][y-1] + e;
            edgeTo[x][y] = 1;
        }
    }

    // relax the edge for horizontal
    private void relaxHorizontal(double[][] disTo, int[][] edgeTo, int x, int y) {
        double e = energy(x, y);
        disTo[x][y] = e + disTo[x-1][y];
        edgeTo[x][y] = 0;

        if (y > 0 && disTo[x-1][y-1] + e < disTo[x][y]) {
            disTo[x][y] = disTo[x-1][y-1] + e;
            edgeTo[x][y] = -1; 
        }
        if (y < height() - 1 && disTo[x-1][y+1] + e < disTo[x][y]) {
            disTo[x][y] = disTo[x-1][y+1] + e;
            edgeTo[x][y] = 1;
        }
    }

    // trace back the vertical seam
    private int[] traceVerticalSeam(double[][] disTo, int[][] edgeTo) {
        int[] seam = new int[height()];
        int width = width(), height = height();
        int min = 0;

        // finding minimum energy pixel from the bottom border
        for (int x = 0; x < width; x++) {
            if (disTo[x][height-1] < disTo[min][height-1])
                min = x;
        }
        
        // finding the path from the minimum energy pixel to top 
        for (int y = height - 1; y >= 0; y--) {
            seam[y] = min;
            min = min + edgeTo[min][y];
        }
        return seam;
    }

    // trace back the horizontal seam
    private int[] traceHorizontalSeam(double[][] disTo, int[][] edgeTo) {
        int[] seam = new int[width()];
        int width = width(), height = height();
        int min = 0;

        // finding minimum energy pixel in the right border
        for (int y = 0; y < height; y++) {
            if (disTo[width-1][y] < disTo[width-1][min])
                min = y;
        }
        
        // finding the path from the minimum energy pixel to left border
        for (int x = width - 1; x >= 0; x--) {
            seam[x] = min;
            min = min + edgeTo[x][min];
        }
        return seam;
    }

    // throw IllegalArgumentException if vertical seam is invalid
    private void validateVerticalSeam(int[] seam) {
        if (seam == null) 
            throw new IllegalArgumentException("Null arguments not allowed.");
        if (seam.length != height())
            throw new IllegalArgumentException("Invalid seam length.");
        if (seam[0] < 0 || seam[0] >= width())
            throw new IllegalArgumentException("Seam contains entry out of range.");

        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width())
                throw new IllegalArgumentException("Seam contains entry out of range.");
            if (Math.abs(seam[i] - seam[i-1]) > 1)
                throw new IllegalArgumentException("Invalid seam.");
        }
    }

    // throw IllegalArgumentException if horizontal seam is invalid
    private void validateHorizontalSeam(int[] seam) {
        if (seam == null) 
            throw new IllegalArgumentException("Null arguments not allowed.");
        if (seam.length != width())
            throw new IllegalArgumentException("Invalid seam length.");
        if (seam[0] < 0 || seam[0] >= height())
            throw new IllegalArgumentException("Seam contains entry out of range.");

        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height())
                throw new IllegalArgumentException("Seam contains entry out of range.");
            if (Math.abs(seam[i] - seam[i-1]) > 1)
                throw new IllegalArgumentException("Invalid seam.");
        }
    }

    // returns the gradient of the pixel
    private int gradient(int x, int y) {
        int r = (y >> 16 & 255) - (x >> 16 & 255);
        int g = (y >> 8 & 255) - (x >> 8 & 255);
        int b = (y & 255) - (x & 255);
        return r * r + g * g + b * b;
    }

    // returns true if border pixel
    private boolean isBorder(int x, int y) {
        return x == 0 || y == 0 || x == width() - 1 || y == height() - 1; 
    }

    // throw IllegalArgumentException if pixel out of range
    private void validateRange(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IllegalArgumentException("Out of prescribed range.");
    }

    // unit test the code
    public static void main(String[] args) {
        Picture picture = new Picture("images/gojo.jpg");
        SeamCarverDP seamCarver = new SeamCarverDP(picture);
        StdOut.println(seamCarver.width() + "x" + seamCarver.height());
        
        int w = StdIn.readInt();
        int h = StdIn.readInt();
        
        for (int i = 0; i < w; i++) {
            int[] seam = seamCarver.findVerticalSeam();
            seamCarver.removeVerticalSeam(seam);
        }
        
        for (int i = 0; i < h; i++) {
            int[] seam = seamCarver.findHorizontalSeam();
            seamCarver.removeHorizontalSeam(seam);
        }

        seamCarver.picture().show();
    }
}
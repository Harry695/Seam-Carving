import java.awt.Color;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    Picture picture;
    int[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        energy = new int[picture.height()][picture.width()];
    }
 
    // current picture
    public Picture picture() {
        return new Picture(picture);
    }
 
    // width of current picture
    public int width() {
        return picture.width();
    }
 
    // height of current picture
    public int height() {
        return picture.height();
    }
 
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return 1000;
        }
        Color left = picture.get(x-1, y);
        Color right = picture.get(x+1, y);
        Color top = picture.get(x, y-1);
        Color bottom = picture.get(x, y+1);
        return Math.sqrt(
            Math.pow(left.getRed() - right.getRed(), 2) +
            Math.pow(left.getGreen() - right.getGreen(), 2) + 
            Math.pow(left.getBlue() - right.getBlue(), 2) +
            Math.pow(top.getRed() - bottom.getRed(), 2) +
            Math.pow(top.getGreen() - bottom.getGreen(), 2) +
            Math.pow(top.getBlue() - bottom.getBlue(), 2));
    }
 
    // sequence of indices for horizontal seam
    // public int[] findHorizontalSeam()
 
    // sequence of indices for vertical seam
    // public int[] findVerticalSeam() // traverse each row, relax each pixel, check shortest sum of bottom row pixels
 
    // remove horizontal seam from current picture
    // public void removeHorizontalSeam(int[] seam)
 
    // remove vertical seam from current picture
    // public void removeVerticalSeam(int[] seam)
 
    //  unit testing (optional)
    public static void main(String[] args) {
        SeamCarver seamCarver = new SeamCarver(new Picture("seam\\\\3x4.png"));
        System.out.println(seamCarver.width() + " " + seamCarver.height());
        System.out.println(seamCarver.energy(1, 2));
    }
 
 }
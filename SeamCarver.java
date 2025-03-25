import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
  private Picture picture;
  private double[][] energyField;

  // ArrayList<ArrayList<Double>> energy;

  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    this.picture = new Picture(picture);
    energyField = new double[picture.height()][picture.width()];

    // compute energy of all pixels
    for (int y = 0; y < picture.height(); y++) {
      for (int x = 0; x < picture.width(); x++) {
        energyField[y][x] = calculateEnergy(x, y);
      }
    }
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
    checkBounds(x, y);
    return energyField[y][x];
  }

  private double calculateEnergy(int x, int y) {
    if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
      return 1000;
    }
    Color left = picture.get(x - 1, y);
    Color right = picture.get(x + 1, y);
    Color top = picture.get(x, y - 1);
    Color bottom = picture.get(x, y + 1);

    return Math.sqrt(calculateEnergyComponent(left, right) + calculateEnergyComponent(top, bottom));
  }

  private double calculateEnergyComponent(Color first, Color second) {
    double r = first.getRed() - second.getRed();
    double b = first.getBlue() - second.getBlue();
    double g = first.getGreen() - second.getGreen();
    return r * r + b * b + g * g;
  }

  // sequence of indices for horizontal seam
  public int[] findHorizontalSeam() {
    int[][] edgeTo = new int[picture.height()][picture.width()];
    double[][] distTo = new double[picture.height()][picture.width()];

    // set all distTo to infinity except first col
    for (int x = 0; x < picture.width(); x++) {
      for (int y = 0; y < picture.height(); y++) {
        if (x == 0) {
          distTo[y][x] = energy(x, y);
        } else {
          distTo[y][x] = Double.POSITIVE_INFINITY;
        }
      }
    }

    // traverse picture col major, relax every node
    for (int x = 0; x < picture.width() - 1; x++) {
      for (int y = 0; y < picture.height(); y++) {
        // upper right
        if (y != 0 && distTo[y - 1][x + 1] > distTo[y][x] + energy(x + 1, y - 1)) {
          distTo[y - 1][x + 1] = distTo[y][x] + energy(x + 1, y - 1);
          edgeTo[y - 1][x + 1] = y;
        }

        // middle right
        if (distTo[y][x + 1] > distTo[y][x] + energy(x + 1, y)) {
          distTo[y][x + 1] = distTo[y][x] + energy(x + 1, y);
          edgeTo[y][x + 1] = y;
        }

        // lower right
        if (y != picture.height() - 1
            && distTo[y + 1][x + 1] > distTo[y][x] + energy(x + 1, y + 1)) {
          distTo[y + 1][x + 1] = distTo[y][x] + energy(x + 1, y + 1);
          edgeTo[y + 1][x + 1] = y;
        }
      }
    }

    // go through last col, find the smallest dist to top
    int minIndex = -1;
    double minDist = Double.POSITIVE_INFINITY;
    for (int y = 0; y < picture.height(); y++) {
      if (distTo[y][picture.width() - 1] < minDist) {
        minDist = distTo[y][picture.width() - 1];
        minIndex = y;
      }

      // debug
      // System.out.println("edgeTo");
      // System.out.println(edgeTo[1][x]);
    }

    // backtrack to leftmost col
    int[] sequence = new int[picture.width()];
    int nextY = minIndex;
    for (int x = picture.width() - 1; x >= 0; x--) { // bottom row to top row
      // System.out.println("y = " + y);
      sequence[x] = nextY; // store the y value of vertex in shortest path at current col
      // System.out.println("just added " + sequence[y] + " to sequence");
      nextY =
          edgeTo[nextY][
              x]; // backtrack, find the previous vertex that had the "edge to" this vertex
      // System.out.println("next x: " + nextX);
    }

    // debug
    // for (int i : sequence) {
    // 	System.out.println(i);
    // }

    return sequence;
  }

  // sequence of indices for vertical seam
  // traverse each row, relax each pixel, check shortest sum of bottom row of pixels
  public int[] findVerticalSeam() {
    int[][] edgeTo = new int[picture.height()][picture.width()];
    double[][] distTo = new double[picture.height()][picture.width()];

    // set all distTo to infinity except first row
    for (int y = 0; y < picture.height(); y++) {
      for (int x = 0; x < picture.width(); x++) {
        if (y == 0) {
          distTo[y][x] = energy(x, y);
        } else {
          distTo[y][x] = Double.POSITIVE_INFINITY;
        }
      }
    }

    // traverse picture row major, relax every node
    for (int y = 0; y < picture.height() - 1; y++) {
      for (int x = 0; x < picture.width(); x++) {

        // lower left
        if (x != 0 && distTo[y + 1][x - 1] > distTo[y][x] + energy(x - 1, y + 1)) {
          distTo[y + 1][x - 1] = distTo[y][x] + energy(x - 1, y + 1);
          edgeTo[y + 1][x - 1] = x;
        }

        // lower middle
        if (distTo[y + 1][x] > distTo[y][x] + energy(x, y + 1)) {
          distTo[y + 1][x] = distTo[y][x] + energy(x, y + 1);
          edgeTo[y + 1][x] = x;
        }

        // lower right
        if (x != picture.width() - 1
            && distTo[y + 1][x + 1] > distTo[y][x] + energy(x + 1, y + 1)) {
          distTo[y + 1][x + 1] = distTo[y][x] + energy(x + 1, y + 1);
          edgeTo[y + 1][x + 1] = x;
        }
      }
    }

    // go through last row, find the smallest dist to top
    int minIndex = -1;
    double minDist = Double.POSITIVE_INFINITY;
    for (int x = 0; x < picture.width(); x++) {
      if (distTo[picture.height() - 1][x] < minDist) {
        minDist = distTo[picture.height() - 1][x];
        minIndex = x;
      }

      // debug
      // System.out.println("edgeTo");
      // System.out.println(edgeTo[1][x]);
    }

    // backtrack to top
    int[] sequence = new int[picture.height()];
    int nextX = minIndex;
    for (int y = picture.height() - 1; y >= 0; y--) { // bottom row to top row
      // System.out.println("y = " + y);
      sequence[y] = nextX; // store the x value of vertex in shortest path at current row
      // System.out.println("just added " + sequence[y] + " to sequence");
      nextX = edgeTo[y][nextX]; // backtrack, find the previous vertex that had the "edge to" this vertex
      // System.out.println("next x: " + nextX);
    }

    // debug
    // for (int i : sequence) {
    // 	System.out.println(i);
    // }

    return sequence;
  }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    if (seam == null || seam.length != width() || height() <= 1) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < seam.length-1; i++) {
      if (Math.abs(seam[i] - seam[i+1]) > 1) {
        throw new IllegalArgumentException();
      }
    }

    Picture newPicture = new Picture(picture.width(), picture.height()-1);
    double[][] newEnergy = new double[picture.height()-1][picture.width()];

    // put & shift pixels to new image
    for (int x = 0; x < newPicture.width(); x++) {
      int removeY = seam[x];
      if (removeY < 0 || removeY >= height()) {
        throw new IllegalArgumentException();
      }

      for (int y = 0; y < newPicture.height(); y++) {
        if (y < removeY) {
          newPicture.setARGB(x, y, picture.getARGB(x, y));
        } else {
          newPicture.setARGB(x, y, picture.getARGB(x, y+1));
        }
      }
    }
    picture = newPicture;

    // recalculate energy for along seam & move energy over
    for (int x = 0; x < newPicture.width(); x++) {
      int removeY = seam[x];
      for (int y = 0; y < newPicture.height(); y++) {
        if (y < removeY-1) {
          newEnergy[y][x] = energyField[y][x];
        } else if (y > removeY) {
          newEnergy[y][x] = energyField[y+1][x];
        } else {
          newEnergy[y][x] = calculateEnergy(x, y);
        }
      }
    }
    energyField = newEnergy;
  }

  // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    if (seam == null || seam.length != height() || width() <= 1) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < seam.length-1; i++) {
      if (Math.abs(seam[i] - seam[i+1]) > 1) {
        throw new IllegalArgumentException();
      }
    }

    Picture newPicture = new Picture(picture.width() - 1, picture.height());
    double[][] newEnergy = new double[picture.height()][picture.width()-1];

    // put & shift pixels to new image
    for (int y = 0; y < newPicture.height(); y++) {
      int removeX = seam[y];
      if (removeX < 0 || removeX >= width()) {
        throw new IllegalArgumentException();
      }
      
      for (int x = 0; x < newPicture.width(); x++) {
        if (x < removeX) {
          newPicture.setARGB(x, y, picture.getARGB(x, y));
        } else {
          newPicture.setARGB(x, y, picture.getARGB(x+1, y));
        }
      }
    }
    picture = newPicture;

    // recalculate energy for along seam & move energy over
    for (int y = 0; y < newPicture.height(); y++) {
      int removeX = seam[y];
      for (int x = 0; x < newPicture.width(); x++) {
        if (x < removeX-1) {
          newEnergy[y][x] = energyField[y][x];
        } else if (x > removeX) {
          newEnergy[y][x] = energyField[y][x+1];
        } else {
          newEnergy[y][x] = calculateEnergy(x, y);
        }
      }
    }
    energyField = newEnergy;
  }

  private void checkBounds(int x, int y) {
    if (x < 0 || x >= width() || y < 0 || y >= height()) {
      throw new IllegalArgumentException();
    }
  }

  //  unit testing (optional)
  public static void main(String[] args) {
    SeamCarver seamCarver = new SeamCarver(new Picture("seam\\\\3x4.png"));
    System.out.println(seamCarver.width() + " " + seamCarver.height());
    System.out.println(seamCarver.energy(1, 2));
  }
 }
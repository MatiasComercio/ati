package ar.edu.itba.ati.idp;

import static ar.edu.itba.ati.idp.utils.Doubles.equal;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

public enum TestHelper {
  TEST_HELPER;

  private static final ThreadLocalRandom LOCAL_RANDOM = ThreadLocalRandom.current();
  private static final int BOUND_VALUE = 100;
  private static final int MIN_SIZE = 3;
  private static final int MAX_EXCLUSIVE_SIZE = 11;

  public double[][] buildRandomMatrix(final int width, final int height) {
    final double[][] matrix = new double[height][width];
    for (int y = 0; y < matrix.length; y++) {
      for (int x = 0; x < matrix[y].length; x++) {
        matrix[y][x] = LOCAL_RANDOM.nextDouble(-BOUND_VALUE, BOUND_VALUE);
      }
    }
    return matrix;
  }

  public int randomWidth() {
    return randomSize();
  }

  public int randomHeight() {
    return randomSize();
  }

  private int randomSize() {
    return LOCAL_RANDOM.nextInt(MIN_SIZE, MAX_EXCLUSIVE_SIZE);
  }

  public double[][] buildRandomMatrix() {
    return buildRandomMatrix(TEST_HELPER.randomWidth(), TEST_HELPER.randomHeight());
  }

  public void assertEqual(final double expected, final double actual, final int x, final int y) {
    final String msg = String.format("At [x,y] = [%d,%d] - expected: %f, actual: %f",
                                     x, y, expected, actual);
    assertTrue(msg, equal(expected, actual));
  }

  public ThreadLocalRandom getLocalRandom() {
    return LOCAL_RANDOM;
  }

  public void assertEqual(final double expected, final double actual) {
    final String msg = String.format("Expected: %f, Actual: %f", expected, actual);
    assertTrue(msg, equal(expected, actual));
  }
}

package ar.edu.itba.ati.idp.utils;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ArrayUtilsTest {

  private double[][] matrix;

  @Before
  public void initialize() {
    matrix = TEST_HELPER.buildRandomMatrix();
  }

  @Test
  public void clampedSafePixelTest() {
    int y;
    int x;
    final int height = matrix.length;
    final int width = matrix[0].length;
    // Out of bound cases => clamp should work OK
    y = -1;
    for (x = 0; x < width; x++) {
      assertEquals(matrix[0][x], ArrayUtils.getClampedValue(matrix, x, y), 1e-10);
    }

    y = height;
    for (x = 0; x < width; x++) {
      assertEquals(matrix[height - 1][x], ArrayUtils.getClampedValue(matrix, x, y), 1e-10);
    }

    x = -1;
    for (y = 0; y < height; y++) { // y iteration
      assertEquals(matrix[y][0], ArrayUtils.getClampedValue(matrix, x, y), 1e-10);
    }

    x = width;
    for (y = 0; y < height; y++) {
      assertEquals(matrix[y][width - 1], ArrayUtils.getClampedValue(matrix, x, y), 1e-10);
    }

    // inside valid range => should work like a matrix[y][x]
    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
        assertEquals(matrix[y][x], ArrayUtils.getClampedValue(matrix, x, y), 1e-10);
      }
    }
  }
}

package ar.edu.itba.ati.idp.function.filter;

import static org.junit.Assert.assertEquals;

import ar.edu.itba.ati.idp.function.filter.mask.linear.HighPassMask;
import org.junit.Test;

public class HighPassFilterTest {

  private static final int CONSTANT = 8;

  @Test
  public void testApply() {
    final double[][] pixels = new double[][]{
        {1, 3, 5},
        {7, 9, 11},
        {13, 15, 17}
    };

    final Filter<HighPassMask> highPassFilter = Filter.newInstance(HighPassMask.newInstance(3, 3));
    final double[][] newPixels = highPassFilter.apply(pixels);
    final double[][] expectedNewPixels = clampedExpectedPixels();
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        assertEquals(expectedNewPixels[y][x], newPixels[y][x], 1e-10);
      }
    }
  }

  private double[][] clampedExpectedPixels() {
    return new double[][]{
        {
            (CONSTANT - (1 + 1 + 3 + 1 + 3 + 7 + 7 + 9)) / 9d,
            (3 * CONSTANT - (1 + 1 + 3 + 5 + 5 + 7 + 9 + 11)) / 9d,
            (5 * CONSTANT - (3 + 5 + 3 + 5 + 5 + 9 + 11 + 11)) / 9d
        },
        {
            (7 * CONSTANT - (1 + 1 + 3 + 7 + 9 + 13 + 13 + 15)) / 9d,
            (9 * CONSTANT - (1 + 3 + 5 + 7 + 11 + 13 + 15 + 17)) / 9d,
            (11 * CONSTANT - (3 + 5 + 5 + 9 + 11 + 15 + 17 + 17)) / 9d
        },
        {
            (13 * CONSTANT - (7 + 7 + 9 + 13 + 15 + 13 + 13 + 15)) / 9d,
            (15 * CONSTANT - (7 + 9 + 11 + 13 + 17 + 13 + 15 + 17)) / 9d,
            (17 * CONSTANT - (9 + 11 + 11 + 15 + 17 + 15 + 17 + 17)) / 9d
        }
    };
  }
}

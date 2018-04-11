package ar.edu.itba.ati.idp.function.filter.mask.nonlinear;

import static org.junit.Assert.assertEquals;

import ar.edu.itba.ati.idp.function.filter.Filter;
import ar.edu.itba.ati.idp.function.filter.mask.nonlinear.WeightedMedianMask;
import org.junit.Test;

public class WeightedMedianFilterTest {
  @Test
  public void testApply() {
    final Filter<WeightedMedianMask> weightedMedianFilter = Filter.newInstance(WeightedMedianMask.newInstance(3, 3));
    final double[][] newPixels = weightedMedianFilter.apply(PIXELS);
    for (int y = 0; y < PIXELS.length; y++) {
      for (int x = 0; x < PIXELS[y].length; x++) {
        assertEquals(EXPECTED_PIXELS[y][x], newPixels[y][x], 1e-10);
      }
    }
  }

  private static final double[][] PIXELS = new double[][] {
      {1,3,5},
      {7,9,11},
      {13,15,17}
  };

  private static final double[][] EXPECTED_PIXELS = new double[][] {
      { /* 1, 1,1, 1,1, 1,1,1,1, 3, 3,3, 7, 7,7, 9 */ 1, /* 1, 1,1, 3,3, 3,3,3,3, 5, 5,5, 7, 9,9, 11 */ 3, /* 3, 3,3, 5,5, 5, 5,5,5,5, 5,5, 9, 11,11, 11 */ 5},
      // Following the same logic as above, and because the order of the elements in the pixels, we directly complete the below values.
      { 7, 9, 11},
      { 13, 15, 17}
  };
}

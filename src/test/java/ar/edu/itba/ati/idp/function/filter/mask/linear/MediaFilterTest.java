package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static org.junit.Assert.assertEquals;

import ar.edu.itba.ati.idp.function.filter.Filter;
import ar.edu.itba.ati.idp.function.filter.mask.linear.MediaMask;
import org.junit.Test;

public class MediaFilterTest {
  @Test
  public void testApply() {
    final double[][] pixels = new double[][] {
        {1,3,5},
        {7,9,11},
        {13,15,17}
    };

    final Filter<MediaMask> mediaFilter = Filter.newInstance(MediaMask.newInstance(3, 3));
    final double[][] newPixels = mediaFilter.apply(pixels);
    final double[][] expectedNewPixels = clampedExpectedPixels();
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        assertEquals(expectedNewPixels[y][x], newPixels[y][x], 1e-10);
      }
    }
  }

  @SuppressWarnings("unused")
  private double[][] emptyExpectedPixels() {
    return new double[][] {
        {(1+3+7+9)/9d, (1+3+5+7+9+11)/9d, (3+5+9+11)/9d},
        {(1+3+7+9+13+15)/9d, (1+3+5+7+9+11+13+15+17)/9d, (3+5+9+11+15+17)/9d},
        {(7+9+13+15)/9d, (7+9+11+13+15+17)/9d, (9+11+15+17)/9d}
    };
  }

  private double[][] clampedExpectedPixels() {
    return new double[][] {
        {(1+1+3+1+1+3+7+7+9)/9d, (1+1+3+3+5+5+7+9+11)/9d, (3+5+5+3+5+5+9+11+11)/9d},
        {(1+1+3+7+7+9+13+13+15)/9d, (1+3+5+7+9+11+13+15+17)/9d, (3+5+5+9+11+11+15+17+17)/9d},
        {(7+7+9+13+13+15+13+13+15)/9d, (7+9+11+13+15+17+13+15+17)/9d, (9+11+11+15+17+17+15+17+17)/9d}
    };
  }
}
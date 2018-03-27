package ar.edu.itba.ati.idp.function.filter;

import static org.junit.Assert.assertEquals;

import ar.edu.itba.ati.idp.function.filter.mask.linear.GaussMask;
import org.junit.Test;

public class GaussFilterTest {
  private static final double[][] PIXELS = new double[][] {
      {1,3,5},
      {7,9,11},
      {13,15,17}
  };

  private static final double SIGMA = 1;

  @Test
  public void testApply() {
    final Filter<GaussMask> gaussFilter = Filter.newInstance(GaussMask.newInstance(SIGMA));
    final double[][] newPixels = gaussFilter.apply(PIXELS);
    final double[][] expectedNewPixels = clampedExpectedPixels();
    for (int y = 0; y < PIXELS.length; y++) {
      for (int x = 0; x < PIXELS[y].length; x++) {
        assertEquals(expectedNewPixels[y][x], newPixels[y][x], 1e-10);
      }
    }
  }

  private double[][] clampedExpectedPixels() {
    final double[][] newPixels = new double[PIXELS.length][];
    for (int y = 0; y < PIXELS.length; y++) {
      final double[] rowPixels = new double[PIXELS[y].length];
      for (int x = 0; x < PIXELS[y].length; x++) {
        rowPixels[x] = clampedExpectedPixel(x, y);
      }
      newPixels[y] = rowPixels;
    }

    return newPixels;
  }

  private double clampedExpectedPixel(final int coreX, final int coreY) {
    double pixel = 0;
    for (int y = -1; y <= 1; y++) {
      for (int x = -1; x <= 1; x++) {
        pixel += (PIXELS[clampedY(coreY + y)][clampedX(coreX + x)] * MASK[y + 1][x + 1]);
      }
    }
    return pixel;
  }

  private int clampedY(final int y) {
    return y < 0 ? 0 : y >= PIXELS.length ? PIXELS.length - 1 : y;
  }

  private int clampedX(final int x) {
    return x < 0 ? 0 : x >= PIXELS[0].length ? PIXELS[0].length - 1 : x;
  }

  // Calculated with octave
  /*
   * gaussMask.m
   *
   format long

   function res = gauss(x,y,sigma)
     res = 1/(2*pi*sigma^2) * exp(-(x^2+y^2)/sigma^2);
   endfunction

   a = zeros(3,3);
   sigma = 1;
   for y=-1:1
     for x=-1:1
       a(y+2, x+2) = gauss(x, y, sigma);
     endfor
   endfor

   total = sum(sum(a));

    a/total
    ans =

       0.0449192238451564   0.1221031099267728   0.0449192238451564
       0.1221031099267728   0.3319106649122836   0.1221031099267728
       0.0449192238451564   0.1221031099267728   0.0449192238451564

    sum(sum(ans))
    ans =  1 # This is OK :D
   */
  private static final double[][] MASK = new double[][] {
      {0.0449192238451564, 0.1221031099267728, 0.0449192238451564},
      {0.1221031099267728, 0.3319106649122836, 0.1221031099267728},
      {0.0449192238451564, 0.1221031099267728, 0.0449192238451564}
  };
}

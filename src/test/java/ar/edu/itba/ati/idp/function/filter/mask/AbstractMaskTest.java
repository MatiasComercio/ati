package ar.edu.itba.ati.idp.function.filter.mask;

import static org.junit.Assert.assertEquals;

import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Before;
import org.junit.Test;

public class AbstractMaskTest {
  private static final double[][] PIXELS = new double[][] {
      {1, 3, 5},
      {7, 9, 11},
      {13, 15, 17}
  };

  private static final double[][] MASK = new double[][] {
      {2, 3, 4},
      {5, 6, 7},
      {8, 9, 10},
  };

  private AbstractMask abstractMask;

  @Before
  public void initialize() {
    abstractMask = new AbstractMask(MASK.length, MASK[0].length) {
      @Override
      protected void initializeMask() {
        this.mask = ArrayUtils.copyOf(MASK);
      }

      @Override
      protected double applyMaskTo(final double[][] pixels, final int currCoreX,
                                   final int currCoreY) {
        // Testing a sample matrix multiplication position per position iteration
        return pixels[currCoreY][currCoreX] * mask[currCoreY][currCoreX];
      }
    };
  }

  @Test
  public void applyTest() {
    /*
     * We are implicitly testing not only the apply method,
     *  but that it uses the initialized mask & the applyMastTo method correctly.
     */
    for (int y = 0; y < PIXELS.length; y++) {
      for (int x = 0; x < PIXELS[y].length; x++) {
        final double expectedPixel = PIXELS[y][x] * MASK[y][x];
        assertEquals(expectedPixel, abstractMask.apply(PIXELS, x, y), 1e-10);
      }
    }
  }

  @Test
  public void iterateMaskAndSetGetMaskPixelTest() {
    final double[][] copy = new double[MASK.length][MASK[0].length];
    final double[][] original = abstractMask.mask;
    abstractMask.iterateMask((maskX, maskY) -> {
      // For testing purpose only, we are changing temporarily the mask.
      final double pixel = abstractMask.getMaskPixel(maskX, maskY);
      abstractMask.mask = copy;
      abstractMask.setMaskPixel(maskX, maskY, pixel);
      abstractMask.mask = original;
    });

    for (int y = 0; y < original.length; y++) {
      for (int x = 0; x < original[y].length; x++) {
        assertEquals(copy[y][x], original[y][x], 1e-10);
      }
    }
  }

  @Test
  public void dimensionsTest() {
    final int randomWidth = ThreadLocalRandom.current().nextInt(100);
    final int randomHeight = ThreadLocalRandom.current().nextInt(100);
    abstractMask = new AbstractMask(randomWidth, randomHeight) {
      @Override
      protected void initializeMask() {
        // Dummy
      }

      @Override
      protected double applyMaskTo(final double[][] pixels, final int currCoreX,
                                   final int currCoreY) {
        return 0; // Dummy
      }
    };
    assertEquals(randomWidth, abstractMask.getWidth());
    assertEquals(randomHeight, abstractMask.getHeight());
  }
}

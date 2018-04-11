package ar.edu.itba.ati.idp.function.filter.mask.linear;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.lang.reflect.Modifier;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("PointlessArithmeticExpression")
public class AbstractLinearMaskTest {
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

  // Doing the linear transformation by hand
  private static final double[][] EXPECTED_PIXELS = new double[][] {
      {(2 * 1 + 3 * 1 + 4 * 3 + 5 * 1  + 6 * 1  + 7 * 3  + 8 * 7  + 9 * 7 + 10 * 9 ), (2 * 1 + 3 * 3 + 4 * 5  + 5 * 1  + 6 * 3  + 7 * 5  + 8 * 7  + 9 * 9 + 10 * 11), (2 * 3 + 3 * 5  + 4 * 5  + 5 * 3  + 6 * 5  + 7 * 5  + 8 * 9  + 9 * 11 + 10 * 11)},
      {(2 * 1 + 3 * 1 + 4 * 3 + 5 * 7  + 6 * 7  + 7 * 9  + 8 * 13 + 9 * 13+ 10 * 15), (2 * 1 + 3 * 3 + 4 * 5  + 5 * 7  + 6 * 9  + 7 * 11 + 8 * 13 + 9 * 15+ 10 * 17), (2 * 3 + 3 * 5  + 4 * 5  + 5 * 9  + 6 * 11 + 7 * 11 + 8 * 15 + 9 * 17 + 10 * 17)},
      {(2 * 7 + 3 * 7 + 4 * 9 + 5 * 13 + 6 * 13 + 7 * 15 + 8 * 13 + 9 * 13+ 10 * 15), (2 * 7 + 3 * 9 + 4 * 11 + 5 * 13 + 6 * 15 + 7 * 17 + 8 * 13 + 9 * 15+ 10 * 17), (2 * 9 + 3 * 11 + 4 * 11 + 5 * 15 + 6 * 17 + 7 * 17 + 8 * 15 + 9 * 17 + 10 * 17)}
  };

  private AbstractLinearMask abstractLinearMask;

  @Before
  public void initialize() {
    abstractLinearMask = new AbstractLinearMask(MASK[0].length, MASK.length) {
      @Override
      protected void initializeMask() {
        this.mask = ArrayUtils.copyOf(MASK);
      }
    };
  }

  @Test
  public void applyToMaskTest() {
    for (int y = 0; y < PIXELS.length; y++) {
      for (int x = 0; x < PIXELS[0].length; x++) {
        final double actualPixel = abstractLinearMask.apply(PIXELS, x, y);
        final double expectedPixel = EXPECTED_PIXELS[y][x];
        assertEquals(expectedPixel, actualPixel, 1e-10);
      }
    }
  }

  @Test
  public void applyToMaskIsFinalTest() { // This is to as to ensure all inherited classes behaves as they should.
    try {
      assertTrue(Modifier.isFinal(AbstractLinearMask.class.getDeclaredMethod("applyMaskTo", double[][].class, int.class, int.class).getModifiers()));
    } catch (NoSuchMethodException e) {
      fail();
    }
  }
}

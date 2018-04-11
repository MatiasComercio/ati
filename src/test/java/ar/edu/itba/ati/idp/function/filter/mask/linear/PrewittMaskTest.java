package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;

import org.junit.Before;
import org.junit.Test;

public class PrewittMaskTest {
  private static final double[][] BASE_MASK = new double[][] {
      { -1, 0, 1 },
      { -1, 0, 1 },
      { -1, 0, 1 }
  };

  private PrewittMask prewittMask;

  @Before
  public void initialize() {
    prewittMask = PrewittMask.newInstance();
  }

  @Test
  public void baseMaskTest() {
    final double[][] baseMask = prewittMask.getMask();
    for (int y = 0; y < BASE_MASK.length; y++) {
      for (int x = 0; x < BASE_MASK[y].length; x++) {
        TEST_HELPER.assertEqual(BASE_MASK[y][x], baseMask[y][x], x, y);
      }
    }
  }
}
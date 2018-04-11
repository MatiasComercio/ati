package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;

import org.junit.Before;
import org.junit.Test;

public class KirschMaskTest {
  private static final double[][] BASE_MASK = new double[][] {
      {   5,  5,  5 },
      {  -3,  0, -3 },
      {  -3, -3, -3 }
  };

  private KirschMask kirschMask;

  @Before
  public void initialize() {
    kirschMask = KirschMask.newInstance();
  }

  @Test
  public void baseMaskTest() {
    final double[][] baseMask = kirschMask.getMask();
    for (int y = 0; y < BASE_MASK.length; y++) {
      for (int x = 0; x < BASE_MASK[y].length; x++) {
        TEST_HELPER.assertEqual(BASE_MASK[y][x], baseMask[y][x], x, y);
      }
    }
  }
}
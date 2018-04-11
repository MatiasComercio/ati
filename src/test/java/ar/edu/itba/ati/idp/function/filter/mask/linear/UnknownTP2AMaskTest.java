package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;

import org.junit.Before;
import org.junit.Test;

public class UnknownTP2AMaskTest {
  private static final double[][] BASE_MASK = new double[][] {
      {  1,  1,  1 },
      {  1, -2,  1 },
      { -1, -1, -1 }
  };

  private UnknownTP2AMask unknownTP2AMask;

  @Before
  public void initialize() {
    unknownTP2AMask = UnknownTP2AMask.newInstance();
  }

  @Test
  public void baseMaskTest() {
    final double[][] baseMask = unknownTP2AMask.getMask();
    for (int y = 0; y < BASE_MASK.length; y++) {
      for (int x = 0; x < BASE_MASK[y].length; x++) {
        TEST_HELPER.assertEqual(BASE_MASK[y][x], baseMask[y][x], x, y);
      }
    }
  }
}
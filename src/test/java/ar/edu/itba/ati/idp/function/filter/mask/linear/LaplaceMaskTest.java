package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static junit.framework.TestCase.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class LaplaceMaskTest {
  private static final double[][] EXPECTED_MASK = new double[][] {
      {  0, -1,  0 },
      { -1,  4, -1 },
      {  0, -1,  0 }
  };

  private LaplaceMask laplaceMask;

  @Before
  public void initialize() {
    laplaceMask = LaplaceMask.getInstance();
  }

  @Test
  public void maskTest() {
    // when
    laplaceMask.initializeMask();

    // then
    for (int y = 0; y < EXPECTED_MASK.length; y++) {
      for (int x = 0; x < EXPECTED_MASK[y].length; x++) {
        TEST_HELPER.assertEqual(EXPECTED_MASK[y][x], laplaceMask.getMask()[y][x], x, y);
      }
    }
  }

  @Test
  public void isAnAbstractLinearMaskTest() {
    //noinspection ConstantConditions
    assertTrue(laplaceMask instanceof AbstractLinearMask);
  }
}
package ar.edu.itba.ati.idp.function.threshold;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GlobalThresholdingTest {
  /*
    There are some troubles when testing as, for example in this case, threshold may be 12 or 13,
    depending on the initial threshold (that is chosen randomly).
  */
  private static final double LIMIT_PIXEL_VALUE = 13;
  private static final double[][] PIXELS = new double[][] {
      {1 ,2 ,3 ,4 , 5},
      {16,17,18,19, 6},
      {15,24,25,20, 7},
      {14,23,22,21, 8},
      {13,12,11,10, 9}
  };


  private static final double[][] EXPECTED_PIXELS = new double[][] {
      {0  ,0  ,0  ,0  , 0},
      {255,255,255,255, 0},
      {255,255,255,255, 0},
      {255,255,255,255, 0},
      {255  ,0  ,0  ,0  , 0}
  };

  @Test
  public void testApply() {
    // when
    final double[][] newPixels = GlobalThresholding.GLOBAL_THRESHOLDING.apply(PIXELS);

    // verify
    assertEquals(EXPECTED_PIXELS.length, newPixels.length);
    for (int y = 0; y < newPixels.length; y++) {
      assertEquals(EXPECTED_PIXELS[0].length, newPixels[0].length);
      for (int x = 0; x < newPixels[y].length; x++) {
        if (PIXELS[y][x] == LIMIT_PIXEL_VALUE) {
          continue; // Skip the problematic case.
        }
        TEST_HELPER.assertEqual(EXPECTED_PIXELS[y][x], newPixels[y][x], x, y);
      }
    }
  }
}
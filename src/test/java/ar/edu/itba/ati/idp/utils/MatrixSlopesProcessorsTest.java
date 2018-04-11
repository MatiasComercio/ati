package ar.edu.itba.ati.idp.utils;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static ar.edu.itba.ati.idp.utils.MatrixSlopesProcessors.REAL_SLOPES_PROCESSOR;
import static ar.edu.itba.ati.idp.utils.MatrixSlopesProcessors.THRESHOLD_SLOPES_PROCESSOR;
import static org.junit.Assert.assertEquals;

import ar.edu.itba.ati.idp.model.ImageMatrix;
import org.junit.Test;

public class MatrixSlopesProcessorsTest {
  /*
    With this matrix, we are testing all cases:
    - Max values are being selected (merge left->right; up->down).
    - Bottom Right corner stays empty.
    - One & Double Zero between crosses.
    - Slopes & non-slopes.
   */
  private static final double[][] PIXELS = new double[][] {
      { -1, 1, 0, 4, -1},
      { 3, 4, 0, 2, 2 },
      { -2, 0, 0, 4, 1 },
      { 0, 1, 0, -5, 1 },
      { 1, 2, -5, 5, 4 },
  };

  private static final double[][] EXPECTED_REAL_SLOPES = new double[][] {
      { 4, 0,  0,  5, 3 },
      { 5, 0,  0,  0, 0 },
      { 3, 0,  0,  9, 0 },
      { 0, 6,  0, 10, 0 },
      { 0, 7, 10,  0, 0 },
  };

  @Test
  public void realSlopesTest() {
    final double[][] slopes = REAL_SLOPES_PROCESSOR.apply(PIXELS);

    assertEquals(EXPECTED_REAL_SLOPES.length, slopes.length);
    assertEquals(EXPECTED_REAL_SLOPES[0].length, slopes[0].length);

    for (int y = 0; y < EXPECTED_REAL_SLOPES.length; y++) {
      for (int x = 0; x < EXPECTED_REAL_SLOPES[y].length; x++) {
        TEST_HELPER.assertEqual(EXPECTED_REAL_SLOPES[y][x], slopes[y][x], x, y);
      }
    }
  }

  @Test
  public void thresholdSlopesTest() {
    final double[][] slopes = THRESHOLD_SLOPES_PROCESSOR.apply(PIXELS);

    assertEquals(EXPECTED_REAL_SLOPES.length, slopes.length);
    assertEquals(EXPECTED_REAL_SLOPES[0].length, slopes[0].length);

    for (int y = 0; y < EXPECTED_REAL_SLOPES.length; y++) {
      for (int x = 0; x < EXPECTED_REAL_SLOPES[y].length; x++) {
        final double thresholdPixel = EXPECTED_REAL_SLOPES[y][x] > 0 ? ImageMatrix.getMaxNormalizedPixelValue() : 0;
        TEST_HELPER.assertEqual(thresholdPixel, slopes[y][x], x, y);
      }
    }
  }
}
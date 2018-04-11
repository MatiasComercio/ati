package ar.edu.itba.ati.idp.function.threshold;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OptimumThresholdTest {
  /*
    Example taken from: http://www.labbookpages.co.uk/software/imgProc/otsuThreshold.html
    Checked with histogram comparison :D (check the `showHistogram` method at the end of this class).

    Threshold should be 3.
   */
  private static final double[][] PIXELS = new double[][] {
      {0,0,1,4,4,5},
      {0,1,3,4,3,4},
      {1,3,4,2,1,3},
      {4,4,3,1,0,0},
      {5,4,2,1,0,0},
      {5,5,4,3,1,0},
  };

  private static final double[][] EXPECTED_PIXELS = new double[][] {
      {  0,  0,  0,255,255,255},
      {  0,  0,255,255,255,255},
      {  0,255,255,  0,  0,255},
      {255,255,255,  0,  0,  0},
      {255,255,  0,  0,  0,  0},
      {255,255,255,255,  0,  0},
  };

  @Test
  public void applyTest() {
    final double[][] newPixels = OptimumThreshold.INSTANCE.apply(PIXELS);
    assertEquals(EXPECTED_PIXELS.length, newPixels.length);
    for (int y = 0; y < EXPECTED_PIXELS.length; y++) {
      assertEquals(EXPECTED_PIXELS[y].length, newPixels.length);
      for (int x = 0; x < EXPECTED_PIXELS[y].length; x++) {
        TEST_HELPER.assertEqual(EXPECTED_PIXELS[y][x], newPixels[y][x], x, y);
      }
    }
  }

  /*
  public void showHistogram() {
    final int[] freq = new int[6];
    for (int y = 0; y < PIXELS.length; y++) {
      for (int x = 0; x < PIXELS[0].length; x++) {
        freq[(int) PIXELS[y][x]] ++;
      }
    }
    System.out.println(Arrays.toString(freq));
  }
*/
}
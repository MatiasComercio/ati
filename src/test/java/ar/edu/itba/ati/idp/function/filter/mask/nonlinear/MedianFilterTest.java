package ar.edu.itba.ati.idp.function.filter.mask.nonlinear;

import static org.junit.Assert.assertEquals;

import ar.edu.itba.ati.idp.function.filter.Filter;
import ar.edu.itba.ati.idp.function.filter.mask.nonlinear.MedianMask;
import org.junit.Test;

public class MedianFilterTest {
  @Test
  public void testApply() {
    final Filter<MedianMask> medianFilter = Filter.newInstance(MedianMask.newInstance(3, 3));
    final double[][] newPixels = medianFilter.apply(PIXELS);
    for (int y = 0; y < PIXELS.length; y++) {
      for (int x = 0; x < PIXELS[y].length; x++) {
        assertEquals(EXPECTED_PIXELS[y][x], newPixels[y][x], 1e-10);
      }
    }
  }

  private static final double[][] PIXELS = new double[][] {
      {1,3,5},
      {7,9,11},
      {13,15,17}
  };

  private static final double[][] EXPECTED_PIXELS = new double[][] {
      { /* (1,1,1,1 ,**3 **,3 ,7, 7, 9)  */ 3,  /* (1,1,3 ,3 ,**5 **,5 ,7 ,9 ,11) */ 5,  /* (3,3 ,5 ,5 ,**5 **,5 ,9 ,11,11) */ 5},
      { /* (1,1,3,7 ,**7 **,9 ,13,13,15) */ 7,  /* (1,3,5 ,7 ,**9 **,11,13,15,17) */ 9,  /* (3,5 ,5 ,9 ,**11**,11,15,17,17) */ 11},
      { /* (7,7,9,13,**13**,13,13,15,15) */ 13, /* (7,9,11,13,**13**,15,15,17,17) */ 13, /* (9,11,11,15,**15**,17,17,17,17) */ 15}
  };
}

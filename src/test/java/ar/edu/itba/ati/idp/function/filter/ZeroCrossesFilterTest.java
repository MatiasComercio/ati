package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;

import ar.edu.itba.ati.idp.function.filter.mask.Mask;
import ar.edu.itba.ati.idp.utils.MatrixSlopesProcessor;
import org.junit.Before;
import org.junit.Test;

public class ZeroCrossesFilterTest {
  private Mask mockedMask;
  private MatrixSlopesProcessor mockedMatrixSlopesProcessor;
  private ZeroCrossesFilter zeroCrossesFilter;
  private double[][] pixels;

  @Before
  public void initialize() {
    mockedMask = mock(Mask.class);
    mockedMatrixSlopesProcessor = mock(MatrixSlopesProcessor.class);
    zeroCrossesFilter = ZeroCrossesFilter.newInstance(mockedMask, mockedMatrixSlopesProcessor);
    pixels = TEST_HELPER.buildRandomMatrix();
  }

  @Test
  public void applyTest() {
    // given
    given(mockedMask.apply(any(double[][].class), anyInt(), anyInt())).willAnswer(invocation -> {
      final double[][] pixels = invocation.getArgumentAt(0, double[][].class);
      final int width = pixels[0].length;
      final int x = invocation.getArgumentAt(1, int.class);
      final int y = invocation.getArgumentAt(2, int.class);

      return (y * width + x) + pixels[y][x]; // Add the index-as-array value to the pixel value.
    });

    // Multiply so as to check the order of operations.
    given(mockedMatrixSlopesProcessor.apply(any(double[][].class))).willAnswer(invocation -> {
      final double[][] pixels = invocation.getArgumentAt(0, double[][].class);
      final double[][] newPixels = new double[pixels.length][];
      for (int y = 0; y < pixels.length; y++) {
        newPixels[y] = new double[pixels[y].length];
        for (int x = 0; x < pixels[y].length; x++) {
          newPixels[y][x] = 2 * pixels[y][x];
        }
      }
      return newPixels;
    });

    // when
    final double[][] newPixels = zeroCrossesFilter.apply(pixels);

    // then
    // Verify that it has applied the mask & slope processor in the correct order.
    // Also verify that the new pixel matrix is of the same size as the original.
    assertEquals(pixels.length, newPixels.length);
    for (int y = 0; y < newPixels.length; y++) {
      final int width = pixels[y].length;
      assertEquals(width, newPixels[y].length);
      for (int x = 0; x < newPixels[y].length; x++) {
        // Operations order: first apply the mask and then apply the slopes processor.
        final double expectedPixel = 2 * ((y * width + x) + pixels[y][x]);
        final double actual = newPixels[y][x];
        TEST_HELPER.assertEqual(expectedPixel, actual, x, y);
      }
    }
  }
}
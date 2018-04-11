package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.utils.Doubles.equal;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

public class GradientFilterTest {

  private static final double[][] PIXELS = new double[][] {
      {1, 3, 5},
      {7, 9, 11},
      {13, 15, 17}
  };

  @Test
  public void testApply() {
    // given
    final RotatableMask mask = mock(RotatableMask.class);
    final RotatableMask rotatedMask = mock(RotatableMask.class);

    final int maskValue = ThreadLocalRandom.current().nextInt();
    final int rotatedMaskValue = ThreadLocalRandom.current().nextInt();

    given(mask.apply(any(double[][].class), anyInt(), anyInt())).willAnswer(invocation -> multiply(invocation, maskValue));
    given(rotatedMask.apply(any(double[][].class), anyInt(), anyInt())).willAnswer(invocation -> multiply(invocation, rotatedMaskValue));
    given(mask.rotate(Degree.D90)).willReturn(rotatedMask);

    // when
    final GradientFilter gradientFilter = GradientFilter.newInstance(mask);

    // then
    verify(mask).rotate(Degree.D90);

    // Verify that it calculates the euclidean distance of values returned by both masks.
    final double[][] newPixels = gradientFilter.apply(PIXELS);
    for (int y = 0; y < newPixels.length; y++) {
      for (int x = 0; x < newPixels[0].length; x++) {
        final double p = PIXELS[y][x];
        final double expectedValue = Math.sqrt(Math.pow(p * maskValue, 2) + Math.pow(p * rotatedMaskValue, 2));
        assertTrue(equal(expectedValue, newPixels[y][x]));
      }
    }
  }

  private double multiply(final InvocationOnMock invocation, final int maskValue) {
    final double[][] pixels = invocation.getArgumentAt(0, double[][].class);
    final int x = invocation.getArgumentAt(1, int.class);
    final int y = invocation.getArgumentAt(2, int.class);
    return maskValue * pixels[y][x];
  }
}
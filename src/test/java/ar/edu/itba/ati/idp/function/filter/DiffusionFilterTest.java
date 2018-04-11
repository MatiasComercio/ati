package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.copyOf;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.getClampedValue;
import static java.lang.Math.exp;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ar.edu.itba.ati.idp.function.filter.DiffusionFilter.DiffusionBorderDetector;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Before;
import org.junit.Test;

public class DiffusionFilterTest {
  private static final double BORDER_DETECTOR_RESPONSE = 2;
  private static final double LAMBDA = .25;

  private int times;
  private double[][] pixels;

  @Before
  public void initialize() {
    times = TEST_HELPER.getLocalRandom().nextInt(1, 10);
    pixels = TEST_HELPER.buildRandomMatrix();
  }

  @Test
  public void applyTest() {
    // given
    final DiffusionBorderDetector diffusionBorderDetector = mock(DiffusionBorderDetector.class);
    given(diffusionBorderDetector.apply(anyDouble())).willReturn(BORDER_DETECTOR_RESPONSE);
    final DiffusionFilter diffusionFilter = DiffusionFilter.newInstance(diffusionBorderDetector);

    // when
    final double[][] newPixels = diffusionFilter.apply(pixels, times);

    // verify
    final int expectedTimes = times * pixels.length * pixels[0].length * 4; // 4: one per direction.
    verify(diffusionBorderDetector, times(expectedTimes)).apply(anyDouble());

    final double[][] expectedPixels = calculateExpectedPixels(pixels);

    assertEquals(pixels.length, newPixels.length);
    for (int y = 0; y < pixels.length; y++) {
      assertEquals(pixels[y].length, newPixels[y].length);
      for (int x = 0; x < pixels[y].length; x++) {
        // final double expectedPixel = calculateExpectedPixels(pixels);
        TEST_HELPER.assertEqual(expectedPixels[y][x], newPixels[y][x], x, y);
      }
    }
  }

  private double[][] calculateExpectedPixels(final double[][] pixels) {
    double[][] curr = copyOf(pixels);
    double[][] next = new double[curr.length][curr[0].length];
    for (int time = 0; time < times; time++) {
      for (int y = 0; y < curr.length; y++) {
        for (int x = 0; x < curr[y].length; x++) {
          next[y][x] = diffuse(curr, x, y);
        }
      }
      curr = next;
    }
    return curr;
  }

  private double diffuse(final double[][] curr, final int x, final int y) {
    final double i = curr[y][x];
    final double n = getClampedValue(curr, x, y - 1) - i;
    final double s = getClampedValue(curr, x, y + 1) - i;
    final double e = getClampedValue(curr, x + 1, y) - i;
    final double w = getClampedValue(curr, x - 1, y) - i;
    return i + LAMBDA * BORDER_DETECTOR_RESPONSE * (n + s + e + w);
  }

  @Test
  public void diffusionBorderDetectorTest() {
    final ThreadLocalRandom localRandom = TEST_HELPER.getLocalRandom();
    final double value = localRandom.nextDouble(-100, 100);
    final double sigma = localRandom.nextDouble(1, 100);

    // given -> when -> verify

    // Isometric
    final DiffusionBorderDetector isometricDet = DiffusionBorderDetector.newIsometricDetector();
    final double isometricNewValue = isometricDet.apply(value);
    TEST_HELPER.assertEqual(1d, isometricNewValue);

    // Leclerc
    final DiffusionBorderDetector leclercDet = DiffusionBorderDetector.newLeclercDetector(sigma);
    final double leclercNewValue = leclercDet.apply(value);
    TEST_HELPER.assertEqual(leclerc(value, sigma), leclercNewValue);

    // Lorentz
    final DiffusionBorderDetector lorentzDet = DiffusionBorderDetector.newLorentzDetector(sigma);
    final double lorentzNewValue = lorentzDet.apply(value);
    TEST_HELPER.assertEqual(lorentz(value, sigma), lorentzNewValue);

    // Min
    final DiffusionBorderDetector minDet = DiffusionBorderDetector.newMinDetector(sigma);
    final double minNewValue = minDet.apply(value);
    TEST_HELPER.assertEqual(min(min(isometricNewValue, leclercNewValue), lorentzNewValue), minNewValue);
  }

  private double leclerc(final double value, final double sigma) {
    return exp(- (pow(value, 2) / pow(sigma, 2)));
  }

  private double lorentz(final double value, final double sigma) {
    return 1d / ((pow(value, 2) / pow(sigma, 2)) + 1);
  }
}
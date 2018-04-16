package ar.edu.itba.ati.idp.function.border;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;

import ar.edu.itba.ati.idp.function.filter.DiffusionFilter;
import ar.edu.itba.ati.idp.function.filter.DiffusionFilter.DiffusionBorderDetector;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class DiffusionTest {
  private double[][] pixels;

  @Before
  public void init() {
    pixels = TEST_HELPER.buildRandomMatrix();
  }

  @SuppressWarnings("unused") // Used as parameters for: testBehaviour
  public Object[] testBehaviourParams() { // All border detectors with their parameters.
    final int times = TEST_HELPER.getLocalRandom().nextInt(3, 10);
    final double sigma = TEST_HELPER.getLocalRandom().nextDouble(0, 10);
    return new Object[][] {
        {Diffusion.newIsometric(times), DiffusionFilter.newInstance(DiffusionBorderDetector.newIsometricDetector(), times)},
        {Diffusion.newLeclerc(times, sigma), DiffusionFilter.newInstance(DiffusionBorderDetector.newLeclercDetector(sigma), times)},
        {Diffusion.newLorentz(times, sigma), DiffusionFilter.newInstance(DiffusionBorderDetector.newLorentzDetector(sigma), times)},
        {Diffusion.newMin(times, sigma), DiffusionFilter.newInstance(DiffusionBorderDetector.newMinDetector(sigma), times)},
    };
  }

  @Test
  @Parameters(method = "testBehaviourParams")
  public void testBehaviour(final DiffusionFilter actualFilter,
                            final DiffusionFilter expectedFilter) {
    TEST_HELPER.assertEqual(expectedFilter.apply(pixels), actualFilter.apply(pixels));
  }
}
package ar.edu.itba.ati.idp.function.border;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static ar.edu.itba.ati.idp.utils.MatrixSlopesProcessors.REAL_SLOPES_PROCESSOR;
import static ar.edu.itba.ati.idp.utils.MatrixSlopesProcessors.THRESHOLD_SLOPES_PROCESSOR;

import ar.edu.itba.ati.idp.function.filter.ZeroCrossesFilter;
import ar.edu.itba.ati.idp.function.filter.mask.linear.LaplaceMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.LoGMask;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class ZeroCrossesBorderDetectorTest {
  private double[][] pixels;

  @Before
  public void init() {
    pixels = TEST_HELPER.buildRandomMatrix();
  }

  @SuppressWarnings("unused") // Used as parameters for: testBehaviour
  public Object[] testBehaviourParams() { // All border detectors with their parameters.
    final double sigma = TEST_HELPER.getLocalRandom().nextDouble(0, 10);
    return new Object[][] {
        {ZeroCrossesBorderDetector.getLaplace(), ZeroCrossesFilter.newInstance(LaplaceMask.getInstance(), THRESHOLD_SLOPES_PROCESSOR)},
        {ZeroCrossesBorderDetector.getLaplaceSlopes(), ZeroCrossesFilter.newInstance(LaplaceMask.getInstance(), REAL_SLOPES_PROCESSOR)},
        {ZeroCrossesBorderDetector.newLoG(sigma), ZeroCrossesFilter.newInstance(LoGMask.newInstance(sigma), REAL_SLOPES_PROCESSOR)}
    };
  }

  @Test
  @Parameters(method = "testBehaviourParams")
  public void testBehaviour(final ZeroCrossesFilter actualFilter,
                            final ZeroCrossesFilter expectedFilter) {
    TEST_HELPER.assertEqual(expectedFilter.apply(pixels), actualFilter.apply(pixels));
  }
}
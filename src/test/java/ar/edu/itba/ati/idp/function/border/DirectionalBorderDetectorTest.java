package ar.edu.itba.ati.idp.function.border;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.DirectionalFilter;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.KirschMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.PrewittMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.SobelMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.UnnamedTP2AMask;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class DirectionalBorderDetectorTest {
  private double[][] pixels;

  @Before
  public void init() {
    pixels = TEST_HELPER.buildRandomMatrix();
  }

  @SuppressWarnings("unused") // Used as parameters for: testBehaviour
  public Object[] testBehaviourParams() { // All border detectors with their parameters.
    return new Object[][] {
        {DirectionalBorderDetector.UNNAMED, UnnamedTP2AMask.newInstance()},
        {DirectionalBorderDetector.KIRSCH, KirschMask.newInstance()},
        {DirectionalBorderDetector.PREWITT, PrewittMask.newInstance()},
        {DirectionalBorderDetector.SOBEL, SobelMask.newInstance()}
    };
  }

  @Test
  @Parameters(method = "testBehaviourParams")
  public  <T extends RotatableMask<T>> void testBehaviour(final DirectionalBorderDetector detector,
                                                          final T mask) {
    // when
    final double[][] actualPixels =  detector.apply(pixels);

    // verify
    final UniquePixelsBandOperator op = DirectionalFilter.newInstance(mask);
    final double[][] expectedPixels = op.apply(pixels);
    TEST_HELPER.assertEqual(expectedPixels, actualPixels);
  }
}
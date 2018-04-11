package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static ar.edu.itba.ati.idp.utils.Doubles.equal;
import static org.junit.Assert.assertTrue;

import ar.edu.itba.ati.idp.utils.MatrixRotator;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class AbstractLinearRotatableMaskTest {
  private static final double[][] BASE_MASK = new double[][] {
      {-1, -1, -1},
      {0 , 0 , 0 },
      {1 , 1 , 1 }
  };

  private AbstractLinearRotatableMask abstractLinearRotatableMask;

  private static final class Dummy extends AbstractLinearRotatableMask<Dummy> {
    private Dummy(final double[][] mask) {
      super(mask);
    }

    @Override
    protected Dummy newInstance(final double[][] mask) {
      return new Dummy(mask);
    }
  }

  @Before
  public void initialize() {
    abstractLinearRotatableMask = new Dummy(BASE_MASK);
  }

  @SuppressWarnings("unused") // In usage by `rotationTest` method (JUnitParams)
  public Object[] allDegrees() {
    return MatrixRotator.Degree.values();
  }

  @Test
  @Parameters(method = "allDegrees")
  @TestCaseName("Degree: {0}")
  public void rotationTest(Degree degree) {
    final double[][] expectedRotatedBaseMask = MatrixRotator.INSTANCE.rotate(
        abstractLinearRotatableMask.getMask(), degree);
    final double[][] rotatedBaseMask = abstractLinearRotatableMask.rotate(degree).getMask();
    for (int y = 0; y < BASE_MASK.length; y++) {
      for (int x = 0; x < BASE_MASK[y].length; x++) {
        assertTrue(equal(expectedRotatedBaseMask[y][x], rotatedBaseMask[y][x]));
      }
    }
  }
}
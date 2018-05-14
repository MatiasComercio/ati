package ar.edu.itba.ati.idp.function.objectdetector;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static java.lang.String.format;

import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.util.LinkedList;
import java.util.List;

public class RectHoughMethod extends HoughMethod {

  private RectHoughMethod(final AccumMatrix accumMatrix) {
    super(accumMatrix);
  }

  public static RectHoughMethod newInstance(final double rhoStart, final double rhoStep, final double rhoEnd,
                                            final double thetaStart, final double thetaStep, final double thetaEnd,
                                            final double epsilon, final double acceptancePercentage) {
    if (thetaStart < -90 || thetaEnd > 90 || thetaStart > thetaEnd) {
      throw new IllegalArgumentException(
          format("Invalid theta parameters: thetaStart: %f, thetaEnd: %f", thetaStart, thetaEnd)
      );
    }

    if (rhoStart > rhoEnd) {
      throw new IllegalArgumentException(
          format("Invalid Rho parameters: rhoStart should be <= rhoEnd, but was: %f < %f", rhoStart, rhoEnd)
      );
    }

    final double[] rhoValues = ArrayUtils.newRangedArray(rhoStart, rhoStep, rhoEnd);
    final double[] thetaValues = ArrayUtils.newRangedArray(thetaStart, thetaStep, thetaEnd);

    final List<AbstractAccumCell> accumCells = new LinkedList<>();
    for (final double rhoValue : rhoValues) {
      for (final double thetaValue : thetaValues) {
        accumCells.add(new RectAccumCell(rhoValue, toRadians(thetaValue)));
      }
    }

    return new RectHoughMethod(new AccumMatrix(accumCells, epsilon, acceptancePercentage));
  }

  private static class RectAccumCell extends AbstractAccumCell {
    private final double rho;
    private final double theta;

    private RectAccumCell(final double rho, final double thetaValue) {
      super();
      this.rho = rho;
      this.theta = thetaValue;
    }

    @Override
    protected boolean doesEquationMatchPixelPoint(final Point pixelPoint, final double epsilon) {
      return abs(rho - pixelPoint.getX() * cos(theta) - pixelPoint.getY() * sin(theta)) < epsilon; // ximportant: check if this `y` sign should be inverted or not...
    }
  }
}

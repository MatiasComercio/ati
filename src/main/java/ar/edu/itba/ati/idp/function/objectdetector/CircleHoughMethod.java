package ar.edu.itba.ati.idp.function.objectdetector;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.String.format;

import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.util.LinkedList;
import java.util.List;

public class CircleHoughMethod extends HoughMethod {

  private CircleHoughMethod(final AccumMatrix accumMatrix) {
    super(accumMatrix);
  }

  public static CircleHoughMethod newInstance(final double aStart, final double aStep, final double aEnd,
                                              final double bStart, final double bStep, final double bEnd,
                                              final double radioStart, final double radioStep, final double radioEnd,
                                              final double epsilon) {
    validateRange("a", aStart, aEnd);
    validateRange("b", bStart, bEnd);
    validateRange("radio", radioStart, radioEnd);

    final double[] aValues = ArrayUtils.newRangedArray(aStart, aStep, aEnd);
    final double[] bValues = ArrayUtils.newRangedArray(bStart, bStep, bEnd);
    final double[] radioValues = ArrayUtils.newRangedArray(radioStart, radioStep, radioEnd);

    final List<AbstractAccumCell> accumCells = new LinkedList<>();
    for (final double aValue : aValues) {
      for (final double bValue : bValues) {
        for (final double radioValue : radioValues) {
          accumCells.add(new CircleAccumCell(aValue, bValue, radioValue));
        }
      }
    }

    return new CircleHoughMethod(new AccumMatrix(accumCells, epsilon));
  }

  private static void validateRange(final String varName, final double start, final double end) {
    throw new IllegalArgumentException(
        format("Invalid %s parameters: aStart should be <= aEnd, but was: %f < %f", varName, start, end)
    );
  }

  private static class CircleAccumCell extends AbstractAccumCell {
    private final double a;
    private final double b;
    private final double radio;

    private CircleAccumCell(final double a, final double b, final double radio) {
      super();
      this.a = a;
      this.b = b;
      this.radio = radio;
    }

    @Override
    protected boolean doesEquationMatchPixelPoint(final Point pixelPoint, final double epsilon) {
      // ximportant: check if this `y` sign should be inverted or not...
      return abs(radio - pow((pixelPoint.getX() - a), 2) - pow((pixelPoint.getY() - b), 2)) < epsilon;
    }
  }
}

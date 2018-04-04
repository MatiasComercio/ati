package ar.edu.itba.ati.idp.function.point;

import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.util.function.DoubleUnaryOperator;

public class Negative implements DoubleUnaryOperator {

  private static final double MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  @Override
  public double applyAsDouble(final double pixel) {
    return MAX_VALUE - pixel;
  }
}

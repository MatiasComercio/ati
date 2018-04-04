package ar.edu.itba.ati.idp.function.point;

import static java.lang.Math.pow;

import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.util.function.DoubleUnaryOperator;

/**
 * Gamma Pow
 */
public class GammaCorrection implements DoubleUnaryOperator {

  private static final double MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  private final double gamma;

  public GammaCorrection(final double gamma) {
    this.gamma = gamma; // Valid values may be between [0,1], but this is not validated though.
  }

  @Override
  public double applyAsDouble(final double pixel) {
    return pow(MAX_VALUE, 1 - gamma) * pow(pixel, gamma);
  }
}

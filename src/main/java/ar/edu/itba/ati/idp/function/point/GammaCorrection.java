package ar.edu.itba.ati.idp.function.point;

import static java.lang.Math.pow;

import java.util.function.DoubleUnaryOperator;

public class GammaCorrection implements DoubleUnaryOperator {

  private static final double MAX_VALUE = 255; // TODO: Sacar de ImageMatrix

  private final double gamma;

  public GammaCorrection(final double gamma) {
    // TODO: Ver valores validos de gamma
    this.gamma = gamma;
  }

  @Override
  public double applyAsDouble(final double pixel) {
    return pow(MAX_VALUE, 1 - gamma) * pow(pixel, gamma);
  }
}

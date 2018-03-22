package ar.edu.itba.ati.idp.function.point;

import java.util.function.DoubleUnaryOperator;

public class Threshold implements DoubleUnaryOperator {

  private static final double MAX_VALUE = 255; // TODO: Sacar de ImageMatrix

  private final int threshold;

  public Threshold(final int threshold) {
    this.threshold = threshold;
  }

  @Override
  public double applyAsDouble(final double pixel) {
    if (pixel <= threshold) {
      return 0.0;
    }

    return MAX_VALUE;
  }
}

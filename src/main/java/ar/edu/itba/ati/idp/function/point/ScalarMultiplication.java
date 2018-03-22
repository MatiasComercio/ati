package ar.edu.itba.ati.idp.function.point;

import java.util.function.DoubleUnaryOperator;

public class ScalarMultiplication implements DoubleUnaryOperator {

  private final double value;

  public ScalarMultiplication(final double value) {
    this.value = value;
  }

  @Override
  public double applyAsDouble(final double pixel) {
    return pixel * value;
  }
}

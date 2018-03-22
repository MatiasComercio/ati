package ar.edu.itba.ati.idp.function.point;

import java.util.function.DoubleUnaryOperator;

public class Negative implements DoubleUnaryOperator {

  private static final double MAX_VALUE = 255; // TODO: Sacar de ImageMatrix

  @Override
  public double applyAsDouble(final double pixel) {
    return MAX_VALUE - pixel;
  }
}

package ar.edu.itba.ati.idp.function.point;

import static java.lang.Math.log;

import java.util.function.DoubleUnaryOperator;

public class DynamicRangeCompression implements DoubleUnaryOperator {

  private static final double MAX_VALUE = 255; // TODO: Sacar de ImageMatrix
  private static final double R = 255;

  @Override
  public double applyAsDouble(final double pixel) {
    return MAX_VALUE / log(1.0 + R) * log(1.0 + pixel); // FIXME R
  }
}

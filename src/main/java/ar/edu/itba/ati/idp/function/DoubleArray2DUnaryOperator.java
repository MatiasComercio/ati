package ar.edu.itba.ati.idp.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleArray2DUnaryOperator {

  double[][] apply(final double[][] m);

  default DoubleArray2DUnaryOperator compose(final DoubleArray2DUnaryOperator before) {
    Objects.requireNonNull(before);
    return (m) -> apply(before.apply(m));
  }

  default DoubleArray2DUnaryOperator andThen(final DoubleArray2DUnaryOperator after) {
    Objects.requireNonNull(after);
    return (m) -> after.apply(apply(m));
  }

  static DoubleArray2DUnaryOperator identity() {
    return m -> m;
  }
}

package ar.edu.itba.ati.idp.function;

import java.util.Objects;

/**
 * This interface should be used when there's an explicit need of applying the
 * underneath implemented method to a grey scale image only.
 * All bands will be compressed to one unique band and that's the pixels matrix that will
 * be passed to the {@link #apply(double[][])} method.
 */
@FunctionalInterface
public interface UniquePixelsBandOperator extends DoubleArray2DUnaryOperator {

  default UniquePixelsBandOperator compose(UniquePixelsBandOperator before) {
    Objects.requireNonNull(before);
    return (m) -> apply(before.apply(m));
  }

  default UniquePixelsBandOperator andThen(UniquePixelsBandOperator after) {
    Objects.requireNonNull(after);
    return (m) -> after.apply(apply(m));
  }

  static UniquePixelsBandOperator identity() {
    return m -> m;
  }
}

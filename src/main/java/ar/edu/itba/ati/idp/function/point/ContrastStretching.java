package ar.edu.itba.ati.idp.function.point;

import java.util.function.DoubleUnaryOperator;

public class ContrastStretching implements DoubleUnaryOperator {

  private static final double MAX_VALUE = 255;

  private final double r1;
  private final double r2;

  private final DoubleUnaryOperator f1;
  private final DoubleUnaryOperator f2;
  private final DoubleUnaryOperator f3;

  public ContrastStretching(final double r1, final double r2) {
    if (r1 >= r2 || r1 < 0.0 || r2 < 0.0) {
      throw new IllegalArgumentException("Invalid probabilities");
    }

    this.r1 = r1;
    this.r2 = r2;

    this.f1 = operand -> 0;
    this.f2 = operand -> 0;
    this.f3 = operand -> 0;
  }

  @Override
  public double applyAsDouble(final double pixel) {
    if (pixel <= r1) {
      return pixel - (MAX_VALUE - pixel);
    }

    if (pixel >= r2) {
      return pixel + pixel;
    }

    return pixel;
  }
}

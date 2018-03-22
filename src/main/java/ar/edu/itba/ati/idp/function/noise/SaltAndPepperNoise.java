package ar.edu.itba.ati.idp.function.noise;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;

public class SaltAndPepperNoise implements DoubleUnaryOperator {

  private static final double MAX_VALUE = 255;

  private final double p0;
  private final double p1;

  public SaltAndPepperNoise(final double p0, final double p1) {
    if (p0 >= p1 || p0 < 0 || p0 > 1 || p1 < 0 || p1 > 1) {
      throw new IllegalArgumentException("Invalid probabilities");
    }

    this.p0 = p0;
    this.p1 = p1;
  }

  @Override
  public double applyAsDouble(final double pixel) {
    final double rand = ThreadLocalRandom.current().nextDouble();

    if (rand <= p0) {
      return 0.0;
    }

    if (rand >= p1) {
      return MAX_VALUE;
    }

    return pixel;
  }
}

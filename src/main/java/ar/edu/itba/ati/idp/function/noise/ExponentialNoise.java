package ar.edu.itba.ati.idp.function.noise;

import java.util.function.DoubleUnaryOperator;
import org.apache.commons.math3.distribution.ExponentialDistribution;

public class ExponentialNoise implements DoubleUnaryOperator {

  private final ExponentialDistribution exponentialDist;

  public ExponentialNoise(final double mean) {
    this.exponentialDist = new ExponentialDistribution(mean);
  }

  @Override
  public double applyAsDouble(final double pixel) {
    return pixel * exponentialDist.sample();
  }
}

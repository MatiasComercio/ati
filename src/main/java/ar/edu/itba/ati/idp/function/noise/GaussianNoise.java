package ar.edu.itba.ati.idp.function.noise;

import java.util.function.DoubleUnaryOperator;
import org.apache.commons.math3.distribution.NormalDistribution;

public class GaussianNoise implements DoubleUnaryOperator {

  private final NormalDistribution normalDist;

  public GaussianNoise(final double mean, final double sd) {
    this.normalDist = new NormalDistribution(mean, sd);
  }

  @Override
  public double applyAsDouble(final double pixel) {
    return pixel + normalDist.sample();
  }
}

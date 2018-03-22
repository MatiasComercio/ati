package ar.edu.itba.ati.idp.function.noise;

import static java.lang.Math.sqrt;

import java.util.function.DoubleUnaryOperator;
import org.apache.commons.math3.distribution.WeibullDistribution;

public class RayleighNoise implements DoubleUnaryOperator {

  private final WeibullDistribution rayleighDist;

  public RayleighNoise(final double scale) {
    rayleighDist = new WeibullDistribution(2.0, scale * sqrt(2));
  }

  @Override
  public double applyAsDouble(final double pixel) {
    return pixel * rayleighDist.sample();
  }
}

package ar.edu.itba.ati.idp.function.filter;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.function.filter.mask.linear.GaussMask;

public class GaussFilter implements DoubleArray2DUnaryOperator {
  private final Filter<GaussMask> gaussFilter;

  private GaussFilter(final Filter<GaussMask> gaussFilter) {
    this.gaussFilter = gaussFilter;
  }

  public static GaussFilter newInstance(final double sigma) {
    return new GaussFilter(Filter.newInstance(GaussMask.newInstance(sigma)));
  }

  public static GaussFilter newInstance(final double sigma, final int sideLength) {
    return new GaussFilter(Filter.newInstance(GaussMask.newInstance(sigma, sideLength)));
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    return gaussFilter.apply(pixels);
  }
}

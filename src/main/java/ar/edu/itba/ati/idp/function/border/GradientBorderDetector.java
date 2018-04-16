package ar.edu.itba.ati.idp.function.border;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.GradientFilter;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.PrewittMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.SobelMask;

public enum GradientBorderDetector implements UniquePixelsBandOperator {
  PREWITT(PrewittMask.newInstance()),
  SOBEL(SobelMask.newInstance());

  private GradientFilter<? extends RotatableMask> filter;

  <T extends RotatableMask<T>> GradientBorderDetector(final T mask) {
    this.filter = GradientFilter.newInstance(mask);
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    return filter.apply(pixels);
  }
}

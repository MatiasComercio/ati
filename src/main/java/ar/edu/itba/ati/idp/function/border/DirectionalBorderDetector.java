package ar.edu.itba.ati.idp.function.border;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.DirectionalFilter;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.KirschMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.PrewittMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.SobelMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.UnnamedTP2AMask;

public enum DirectionalBorderDetector implements UniquePixelsBandOperator {
  UNNAMED(UnnamedTP2AMask.newInstance()),
  KIRSCH(KirschMask.newInstance()),
  PREWITT(PrewittMask.newInstance()),
  SOBEL(SobelMask.newInstance());

  private final DirectionalFilter<? extends RotatableMask> filter;

  <T extends RotatableMask<T>> DirectionalBorderDetector(final T mask) {
    this.filter = DirectionalFilter.newInstance(mask);
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    return filter.apply(pixels);
  }
}

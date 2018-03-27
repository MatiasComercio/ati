package ar.edu.itba.ati.idp.function.filter;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.function.filter.mask.Mask;

public class Filter<T extends Mask> implements DoubleArray2DUnaryOperator {
  private final T mask;

  private Filter(final T mask) {
    this.mask = mask;
  }

  public static <T extends Mask> Filter<T> newInstance(final T mask) {
    return new Filter<>(mask);
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] newPixels = new double[pixels.length][pixels[0].length];
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        newPixels[y][x] = mask.apply(pixels, x, y);
      }
    }
    return newPixels;
  }
}

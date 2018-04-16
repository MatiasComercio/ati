package ar.edu.itba.ati.idp.function.filter;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import java.util.LinkedList;
import java.util.List;

public class GradientFilter<T extends RotatableMask<T>> implements UniquePixelsBandOperator {
  private final List<T> masks;

  private GradientFilter(final List<T> masks) {
    this.masks = masks;
  }

  public static <T extends RotatableMask<T>> GradientFilter<T> newInstance(final T mask) {
    final List<T> masks = new LinkedList<>();
    masks.add(mask);
    masks.add(mask.rotate(Degree.D90));
    return new GradientFilter<>(masks);
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] newPixels = new double[pixels.length][pixels[0].length];
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        double euclideanDistance = 0;
        for (final T mask : masks) {
          final double newValue = mask.apply(pixels, x, y);
          euclideanDistance += Math.pow(newValue, 2);
        }
        newPixels[y][x] = Math.sqrt(euclideanDistance);
      }
    }
    return newPixels;
  }
}

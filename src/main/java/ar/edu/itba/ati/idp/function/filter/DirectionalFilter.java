package ar.edu.itba.ati.idp.function.filter;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectionalFilter<T extends RotatableMask<T>> implements UniquePixelsBandOperator {
  private static final double MIN_ABS_VALUE = 0;

  private final List<T> masks;

  private DirectionalFilter(final List<T> masks) {
    this.masks = masks;
  }

  public static <T extends RotatableMask<T>> DirectionalFilter<T> newInstance(final T mask) {
    // Masks in all directions (8), so the filter is as precise as possible
    final List<T> masks = Stream.of(Degree.values()).map(mask::rotate).collect(Collectors.toList());
    return new DirectionalFilter<>(masks);
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] newPixels = new double[pixels.length][pixels[0].length];
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        double maxAbsValue = MIN_ABS_VALUE;
        for (final T mask : masks) {
          /*
           * We saw this during the last class: use the Math.abs of the returned value,
           *  as a mask may return a very deep but negative value, and in that case the border won't
           *  be considered if the absolute value is not taken.
           */
          final double newAbsValue = Math.abs(mask.apply(pixels, x, y));
          if (newAbsValue > maxAbsValue) {
            maxAbsValue = newAbsValue;
          }
        }
        newPixels[y][x] = maxAbsValue;
      }
    }
    return newPixels;
  }
}

package ar.edu.itba.ati.idp.function.filter;

import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

import ar.edu.itba.ati.idp.function.NonMaximalSuppression;
import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.mask.Mask;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.GaussMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.SobelMask;
import ar.edu.itba.ati.idp.function.threshold.HysteresisThreshold;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import java.util.ArrayList;
import java.util.List;

public class CannyFilter implements UniquePixelsBandOperator {

  private static final double MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  private final List<Filter<GaussMask>> gaussFilters;
  private final Mask maskDX;
  private final Mask maskDY;

  private CannyFilter(final List<Filter<GaussMask>> gaussFilters, final RotatableMask<SobelMask> mask) {
    this.gaussFilters = gaussFilters;
    this.maskDX = mask; // TODO: We should validate this. What if SobelMask orientation changes?
    this.maskDY = mask.rotate(Degree.D90);
  }

  public static CannyFilter newInstance(final double[] sigmas) {
    final List<Filter<GaussMask>> gaussFilters = new ArrayList<>(sigmas.length);

    for (final double sigma : sigmas) {
      gaussFilters.add(Filter.newInstance(GaussMask.newInstance(sigma)));
    }

    return new CannyFilter(gaussFilters, SobelMask.newInstance());
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][][] images = new double[gaussFilters.size()][][];
    int i = 0;

    for (final Filter<GaussMask> gaussFilter : gaussFilters) {
      final double[][] pixelsFiltered = gaussFilter.apply(pixels);
      final double[][] borderDirections = new double[pixelsFiltered.length][];

      for (int y = 0; y < pixelsFiltered.length; y++) {
        borderDirections[y] = new double[pixelsFiltered[y].length];

        for (int x = 0; x < pixelsFiltered[y].length; x++) {
          final double dx = maskDX.apply(pixelsFiltered, x, y);
          final double dy = maskDY.apply(pixelsFiltered, x, y);
          // TODO: Test
          borderDirections[y][x] = dx != 0 ? (toDegrees(atan2(dy, dx)) + 360) % 360 : 0.0;
        }
      }

      // FIXME: this NonMaximalSuppression should be applied to the magnitude matrix, not de filtered one directly.
      final double[][] borderMagnitudes = NonMaximalSuppression.apply(pixelsFiltered, borderDirections);

      images[i++] = HysteresisThreshold.INSTANCE.apply(borderMagnitudes);
    }

    return and(images);
  }

  // TODO: Esto puede ir en ArrayUtils [recibiendo falseValue (0.0), trueValue (255.0)]
  // TODO: Esta funcion de union deberia ser un parametro no?
  private static double[][] and(final double[][][] images) {
    final double[][] resultPixels = new double[images[0].length][images[0][0].length];

    for (int y = 0; y < resultPixels.length; y++) {
      for (int x = 0; x < resultPixels[0].length; x++) {
        resultPixels[y][x] = MAX_VALUE;

        for (final double[][] pixels : images) {
          if (pixels[y][x] != MAX_VALUE) {
            resultPixels[y][x] = 0.0;
            break;
          }
        }
      }
    }

    return resultPixels;
  }

  // TODO: Esto puede ir en ArrayUtils
  // TODO: Esta funcion de union deberia ser un parametro no?
  private static double[][] or(final double[][][] images) {
    final double[][] resultPixels = new double[images[0].length][images[0][0].length];

    for (int y = 0; y < resultPixels.length; y++) {
      for (int x = 0; x < resultPixels[0].length; x++) {
        resultPixels[y][x] = 0.0;

        for (final double[][] pixels : images) {
          if (pixels[y][x] == MAX_VALUE) {
            resultPixels[y][x] = MAX_VALUE;
            break;
          }
        }
      }
    }

    return resultPixels;
  }
}

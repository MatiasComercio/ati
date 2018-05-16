package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.newWithSizeOf;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

import ar.edu.itba.ati.idp.function.NonMaximalSuppression;
import ar.edu.itba.ati.idp.function.Normalizer;
import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.mask.Mask;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.GaussMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.SobelMask;
import ar.edu.itba.ati.idp.function.threshold.HysteresisThreshold;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import java.util.Arrays;

public class CannyFilter implements UniquePixelsBandOperator {

  private static final double MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  private final double[] sigmas;
  private final Mask maskDX;
  private final Mask maskDY;

  private <T extends RotatableMask<T>> CannyFilter(final double[] sigmas,
                                                   final RotatableMask<T> maskDX,
                                                   final RotatableMask<T> maskDY) {
    this.sigmas = sigmas;
    this.maskDX = maskDX;
    this.maskDY = maskDY;
  }

  public static CannyFilter newInstance(final double[] sigmas) {
    /*
     * IMPORTANT!
     * We need to detect borders going UP for y,
     * and borders going RIGHT for x
     * to be consistent with directions through all the Canny's algorithm.
     */
    // TODO: We should validate this. What if SobelMask orientation changes?
    final RotatableMask<SobelMask> maskDX = SobelMask.newInstance();
    return new CannyFilter(sigmas, maskDX, maskDX.rotate(Degree.D270));
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    /*
     * Apply Canny Algorithm for each gaussian filter corresponding to each given sigma,
     * and then merge them with an `and` policy.
     */
    return and(Arrays.stream(sigmas)
                   .mapToObj(sigma -> applyCanny(pixels, sigma))
                   .toArray(double[][][]::new)
    );
  }

  private double[][] applyCanny(final double[][] pixels, final double sigma) {
    final double[][] filteredPixels = Filter.newInstance(GaussMask.newInstance(sigma)).apply(pixels);
    final double[][] borderMagnitudes = newWithSizeOf(filteredPixels);
    final double[][] borderDirections = newWithSizeOf(filteredPixels);

    for (int y = 0; y < filteredPixels.length; y++) {
      for (int x = 0; x < filteredPixels[y].length; x++) {
        final double dx = maskDX.apply(filteredPixels, x, y);
        final double dy = maskDY.apply(filteredPixels, x, y);
        borderMagnitudes[y][x] = sqrt(pow(dx, 2) + pow(dy, 2));
        borderDirections[y][x] = dx == 0 ? 270 : (toDegrees(Math.atan(dy/dx)) + 360) % 360;
      }
    }

    final double[][] suppressedBorderMagnitudes =
        NonMaximalSuppression.apply(Normalizer.INSTANCE.apply(borderMagnitudes), borderDirections);

    return HysteresisThreshold.INSTANCE.apply(suppressedBorderMagnitudes);
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

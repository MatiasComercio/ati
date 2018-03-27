package ar.edu.itba.ati.idp.function.filter.mask.nonlinear;

import ar.edu.itba.ati.idp.function.filter.mask.AbstractMask;
import java.util.Arrays;

public class WeightedMedianMask extends AbstractMask {
  private final int maxExp;
  private final WeightCalculator weightCalculator;

  private int totalPixelsToSort;

  @SuppressWarnings("WeakerAccess")
  protected WeightedMedianMask(final int width, final int height,
                               final WeightCalculator weightCalculator) {
    super(width, height);
    this.maxExp = Math.min(getWidth(), getHeight()) - 1;
    this.weightCalculator = weightCalculator == null ?
            (int maskX, int maskY) -> (int) Math.pow(2, Math.max(0, maxExp - Math.abs(maskX) - Math.abs(maskY))) :
            weightCalculator;
  }

  public static WeightedMedianMask newInstance(final int width, final int height) {
    return newInstance(width, height, null);
  }

  public static WeightedMedianMask newInstance(final int width, final int height,
                                               final WeightCalculator weightCalculator) {
    return new WeightedMedianMask(width, height, weightCalculator);
  }

  @Override
  protected final void initializeMask() {
    iterateMask((maskX, maskY) -> {
      // All values decrementing from the center.
      final int weight = weightCalculator.calculate(maskX, maskY);
      setMaskPixel(maskX, maskY, weight);
      totalPixelsToSort += weight;
    });
  }

  protected interface WeightCalculator {
    int calculate(int maskX, int maskY);
  }

  @Override
  protected double applyMaskTo(final double[][] pixels, final int currCoreX,
                               final int currCoreY) {
    final double[] sortedPixels = new double[totalPixelsToSort];
    final int[] i = {0};
    iterateMask((maskX, maskY) -> {
      final double pixel = clampedSafePixel(pixels, currCoreX + maskX, currCoreY + maskY);
      final int timesToAdd = (int) getMaskPixel(maskX, maskY);
      for (int k = 0; k < timesToAdd; k++) {
        sortedPixels[i[0]++] = pixel;
      }
    });
    Arrays.sort(sortedPixels);
    final int halfIndex = sortedPixels.length / 2; // This `floor` of dividing 2 integers is OK
    if (sortedPixels.length % 2 == 1) { // It has an odd amount elements
      return sortedPixels[halfIndex];
    }

    // Else, return the average of both mid pixels
    final int theOtherHalfIndex = halfIndex - 1;
    return (sortedPixels[halfIndex] + sortedPixels[theOtherHalfIndex]) / 2d;
  }
}

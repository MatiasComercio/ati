package ar.edu.itba.ati.idp.function.threshold;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.function.Normalizer;
import ar.edu.itba.ati.idp.function.point.Threshold;
import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.util.concurrent.ThreadLocalRandom;

public enum GlobalThresholding implements DoubleArray2DUnaryOperator {
  INSTANCE;

  private static final int DELTA_THRESHOLD = 1;

  @Override
  public double[][] apply(final double[][] pixels) {
    /*
     * Normalize all values, as we need this to run the algorithm properly (and in an easier way).
     * Note that we are not loosing that much information, as the output
     * of this method is a matrix with only two possible values.
     */
    final int[][] normalizedPixels = Normalizer.INSTANCE.normalizeToInt(pixels);
    final double[][] newPixels = new double[normalizedPixels.length][normalizedPixels[0].length];

    int prevThreshold = 0;
    int threshold = chooseInitialThreshold(normalizedPixels);
    while (Math.abs(threshold - prevThreshold) >= DELTA_THRESHOLD) {
      final int[] m = new int[2];
      final int[] n = new int[2];
      //noinspection ForLoopReplaceableByForEach
      for (int y = 0; y < normalizedPixels.length; y++) {
        for (int x = 0; x < normalizedPixels[y].length; x++) {
          final int pixel = normalizedPixels[y][x];
          final int thresholdPixel = (int) Threshold.applyToPixel(pixel, threshold);
          newPixels[y][x] = thresholdPixel; // Save to be returned when while loop is ended.
          final int groupI = thresholdPixel > 0 ? 0 : 1; // WHITE (G1) -> 0; BLACK (G2) -> 1.
          m[groupI] += pixel;
          n[groupI] ++;
        }
      }
      // Calculate the new threshold.
      prevThreshold = threshold;
      threshold = 0;
      for (int groupI = 0; groupI < m.length; groupI++) {
        threshold += m[groupI] / n[groupI];
      }
      threshold = threshold / m.length; // int division on purpose.
    }

    return newPixels;
  }

  private int chooseInitialThreshold(final int[][] normalizedPixels) {
    final int[] minMax = ArrayUtils.minAndMax(normalizedPixels);
    // Initial threshold should be in range (min, max) (note the exclusive bounds).
    return ThreadLocalRandom.current().nextInt(minMax[0] + 1, minMax[1]);
  }
}

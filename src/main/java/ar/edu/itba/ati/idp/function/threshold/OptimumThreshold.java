package ar.edu.itba.ati.idp.function.threshold;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.newWithSizeOf;
import static ar.edu.itba.ati.idp.utils.Doubles.equal;
import static java.lang.Math.pow;

import ar.edu.itba.ati.idp.function.Normalizer;
import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.point.Threshold;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.util.LinkedList;
import java.util.List;

public enum OptimumThreshold implements UniquePixelsBandOperator {
  OPTIMUM_THRESHOLD;

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] normalizedPixels = Normalizer.INSTANCE.normalizeToDouble(pixels);
    final int optimumThreshold = getOptimumThresholdOfNormalized(normalizedPixels);

    return Threshold.apply(normalizedPixels, optimumThreshold);
  }

  private static int getOptimumThresholdOfNormalized(final double[][] normalizedPixels) {
    // Step 1.
    final double[] relativeFrequencies = new double[ImageMatrix.getMaxNormalizedPixelValue() + 1];
    final double N = normalizedPixels.length * normalizedPixels[0].length;
    final double delta = 1d / N;
    //noinspection ForLoopReplaceableByForEach
    for (int y = 0; y < normalizedPixels.length; y++) {
      for (int x = 0; x < normalizedPixels[y].length; x++) {
        relativeFrequencies[(int) normalizedPixels[y][x]] += delta;
      }
    }

    // Steps 2 & 3.
    final double[] cumulativeFrequencies = newWithSizeOf(relativeFrequencies);
    final double[] cumulativeMedias = newWithSizeOf(relativeFrequencies);
    // We know it has at least 1 element as pixels matrix values cannot be null.
    cumulativeFrequencies[0] = relativeFrequencies[0];
    cumulativeMedias[0] =
        0 * relativeFrequencies[0]; // Zero (this is just to make explicit what we are doing).
    for (int i = 1; i < relativeFrequencies.length; i++) {
      cumulativeFrequencies[i] = relativeFrequencies[i] + cumulativeFrequencies[i - 1];
      cumulativeMedias[i] = i * relativeFrequencies[i] + cumulativeMedias[i - 1];
    }

    // Step 4.
    final double globalMedia = cumulativeMedias[cumulativeMedias.length - 1];

    // Step 5 & 6
    // All variances should be >= 0 => at least the first one will update these two variables.
    double maxVariance = -1d;
    final List<Integer> maxVariancesIndexes = new LinkedList<>();
    for (int i = 0; i < cumulativeFrequencies.length; i++) {
      final double p1_t = cumulativeFrequencies[i];
      final double m_t = cumulativeMedias[i];
      final double variance = pow(globalMedia * p1_t - m_t, 2) / (p1_t * (1 - p1_t));
      // The order of this `if` statement is IMPORTANT!
      if (equal(variance, maxVariance)) {
        maxVariancesIndexes.add(i);
      } else if (variance > maxVariance) {
        maxVariance = variance;
        maxVariancesIndexes.clear();
        maxVariancesIndexes.add(i);
      } // else: this variance should be discarded as it's not the max variance found.
    }

    // Get the median threshold (not the mean).
    final int thresholdIndex = maxVariancesIndexes.size() / 2; // int division as floor operation.

    return maxVariancesIndexes.get(thresholdIndex);
  }

  public static int getOptimumThreshold(final double[][] pixels) {
    /*
     * Normalize values for the sake of simplicity in this algorithm.
     * Note that we are not loosing that much information, as the output
     * of this method is a matrix with only two possible values.
     */
    final double[][] normalizedPixels = Normalizer.INSTANCE.normalizeToDouble(pixels);

    return getOptimumThresholdOfNormalized(normalizedPixels);
  }
}

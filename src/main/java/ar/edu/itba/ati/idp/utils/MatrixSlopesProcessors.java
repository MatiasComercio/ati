package ar.edu.itba.ati.idp.utils;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.getClampedValue;
import static ar.edu.itba.ati.idp.utils.Doubles.equal;

import ar.edu.itba.ati.idp.model.ImageMatrix;

/** Returned matrix values represent the max slope found for each position. */
public enum MatrixSlopesProcessors implements MatrixSlopesProcessor {
  THRESHOLD_SLOPES_PROCESSOR(SlopeEvaluator.THRESHOLD_SLOPE_EVALUATOR),
  REAL_SLOPES_PROCESSOR(SlopeEvaluator.REAL_SLOPE_EVALUATOR);

  private final SlopeEvaluator slopeEvaluator;

  MatrixSlopesProcessors(final SlopeEvaluator slopeEvaluator) {
    this.slopeEvaluator = slopeEvaluator;
  }

  @Override
  public double[][] apply(final double[][] matrix) {
    final int height = matrix.length;
    final int width = matrix[0].length;
    final double[][] result = new double[height][width];
    // Iteration: → ; → ; ... ; →
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width - 1; x++) {
        // Up to width - 1 as the last value does not change forward (there are no more values).
        final double current = matrix[y][x];
        final double next = matrix[y][x + 1];
        final double nextNext = getClampedValue(matrix, x + 2, y);
        result[y][x] = slopeEvaluator.evaluate(current, next, nextNext);
      }
    }

    // Iteration: ↓ ; ↓ ; ... ; ↓
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height - 1; y++) {
        // Up to height - 1 as the last value does not change forward (there are no more values).
        final double current = matrix[y][x];
        final double next = matrix[y + 1][x];
        final double nextNext = getClampedValue(matrix, x, y + 2);
        // Pick the max slope, as it is more representative of current position zero cross.
        result[y][x] = Math.max(result[y][x], slopeEvaluator.evaluate(current, next, nextNext));
      }
    }

    return result;
  }

  private enum SlopeEvaluator {
    THRESHOLD_SLOPE_EVALUATOR {
      @Override
      double evaluate(final double current, final double next, final double nextNext) {
        double signChange = current * next;
        if (equal(signChange, ZERO)) {
          signChange = current * nextNext;
        }
        return equal(signChange, ZERO) ? ZERO
            : signChange < 0 ? ImageMatrix.getMaxNormalizedPixelValue() : ZERO;
      }
    },

    REAL_SLOPE_EVALUATOR {
      @Override
      double evaluate(final double current, final double next, final double nextNext) {
        double signChange = current * next;
        double realNext = next;
        if (equal(signChange, ZERO)) {
          signChange = current * nextNext;
          realNext = nextNext;
        }
        return equal(signChange, ZERO) ? ZERO
            : signChange < 0 ?  Math.abs(current) + Math.abs(realNext) : ZERO;
      }
    };

    private static final double ZERO = 0d;

    /**
     * Search for a slope between the current and next pixel, or the current and nextNext pixel
     * if next is 0.
     * <p>
     * Return 0 if no slope was found, or a value representing the slope found,
     * <b>which depends on the actual implementation.</b>
     * @param current The current pixel.
     * @param next The next pixel.
     * @param nextNext The next pixel of {@code next} that will be used only when {@code next} is 0.
     * @return The slope found: 0 if no slope;
     *         otherwise, an implementation dependent value (greater than 0)
     */
    abstract double evaluate(double current, double next, double nextNext);
  }
}

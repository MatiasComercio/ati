package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.copyOf;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.getClampedValue;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import java.util.stream.Stream;

public class DiffusionFilter implements UniquePixelsBandOperator {
  private static final int[][] DIRECTIONS = new int[][] {
      /*  {  x,  y }  */
      {  0, -1 }, // N
      {  0,  1 }, // S
      {  1,  0 }, // E
      { -1,  0 } // W
  };

  private static final double LAMBDA = 1d / DIRECTIONS.length;

  private final DiffusionBorderDetector diffusionBorderDetector;
  private final int times;

  private DiffusionFilter(final DiffusionBorderDetector diffusionBorderDetector, final int times) {
    this.diffusionBorderDetector = diffusionBorderDetector;
    this.times = times;
  }

  /**
   *
   * @param diffusionBorderDetector The diffusion border detector that will be used when calling {@link #apply}.
   * @param times The amount of times this filter will be applied consecutively to the pixel matrix when calling {@link #apply}.
   * @return The new diffusion filter characterized by the given parameters.
   */
  public static DiffusionFilter newInstance(final DiffusionBorderDetector diffusionBorderDetector, final int times) {
    return new DiffusionFilter(diffusionBorderDetector, times);
  }

  /**
   * Apply this diffusion filter to the given {@code pixels} matrix using the provided instance parameters.
   *
   * @param pixels The pixel matrix to which this diffusion filter will be applied.
   * @return The result of applying this filter to the {@code pixels} matrix {@code times} times,
   *         using the instance {@code diffusionBorderDetector}.
   * @see #newInstance(DiffusionBorderDetector, int)
   */
  public double[][] apply(final double[][] pixels) {
    double[][] curr = copyOf(pixels);
    double[][] next = new double[curr.length][curr[0].length];
    for (int time = 0; time < times; time++) {
      for (int y = 0; y < curr.length; y++) {
        for (int x = 0; x < curr[y].length; x++) {
          next[y][x] = diffuse(curr, x, y);
        }
      }
      curr = next;
    }
    return curr;
  }

  private double diffuse(final double[][] curr, final int x, final int y) {
    final double pixel = curr[y][x];
    double diffusion = 0;
    for (final int[] direction : DIRECTIONS) {
      final int dirX = direction[0];
      final int dirY = direction[1];
      final double directionalGradient = getClampedValue(curr, x + dirX, y + dirY) - pixel;
      diffusion += (directionalGradient * diffusionBorderDetector.apply(directionalGradient));
    }

    return pixel + LAMBDA * diffusion;
  }

  // Not final to be mocked when testing.
  public static class DiffusionBorderDetector {
    private static final double NO_SIGMA = 0;

    private final BorderDetector borderDetector;
    private final double sigma;

    private DiffusionBorderDetector(final BorderDetector borderDetector, final double sigma) {
      this.borderDetector = borderDetector;
      this.sigma = sigma;
    }

    public static DiffusionBorderDetector newIsometricDetector() {
      return new DiffusionBorderDetector(BorderDetector.ISOMETRIC, NO_SIGMA);
    }

    public static DiffusionBorderDetector newLeclercDetector(final double sigma) {
      return new DiffusionBorderDetector(BorderDetector.LECLERC, sigma);
    }

    public static DiffusionBorderDetector newLorentzDetector(final double sigma) {
      return new DiffusionBorderDetector(BorderDetector.LORENTZ, sigma);
    }

    public static DiffusionBorderDetector newMinDetector(final double sigma) {
      return new DiffusionBorderDetector(BorderDetector.MIN, sigma);
    }

    /* package-private */ double apply(final double value) {
      return borderDetector.apply(value, sigma);
    }

    private enum BorderDetector {
      ISOMETRIC {
        @Override
        public double apply(final double value, final double sigma) {
          return 1d; // Constant
        }
      },
      LECLERC {
        @Override
        public double apply(final double value, final double sigma) {
          return exp(- pow(value, 2) / pow(sigma, 2));
        }
      },
      LORENTZ {
        @Override
        public double apply(final double value, final double sigma) {
          return 1d / ((pow(value, 2) / pow(sigma, 2)) + 1);
        }
      },
      MIN {
        @Override
        public double apply(final double value, final double sigma) {
          return Stream.of(BorderDetector.values())
              .filter(borderDetector -> borderDetector != this) // Remove myself
              .map(borderDetector -> borderDetector.apply(value, sigma)) // Apply each
              .reduce(Double::min).orElseThrow(IllegalStateException::new); // Get the min
          /*
           * Note that the only way of throwing an exception is when the ONLY existing BorderDetector
           * is MIN, which indeed is an invalid/illegal case.
           */
        }
      };

      abstract double apply(final double value, double sigma);
    }
  }
}
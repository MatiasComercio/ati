package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import ar.edu.itba.ati.idp.utils.ArrayUtils;

public class LoGMask extends AbstractLinearMask {
  private final double sigma;

  private LoGMask(final int sideLength, final double sigma) {
    super(sideLength, sideLength);
    this.sigma = sigma;
  }

  public static LoGMask newInstance(final double sigma) {
    if (sigma <= 0) {
      throw new IllegalArgumentException("sigma should be positive");
    }
    /*
     * This sideLength was not enough to grab necessary values to determine where border exists,
     * as all values were negative, and when computing the zero crosses, all the matrix was 0.
     */

    /*
    final int ceilSigma = (int) Math.ceil(sigma);
    final int sideLength = ceilSigma > 10 ? bigMaskSize(ceilSigma) : 2 * ceilSigma + 1;
    */
    final int sideLength = 6 * (int) Math.ceil(sigma) + 1;
    return new LoGMask(sideLength, sigma);
  }

  @SuppressWarnings("unused")
  private static int bigMaskSize(final int ceilSigma) {
    final int maskSize = 3 * ceilSigma;
    // Always return odd size so as to have only one possible core position.
    return maskSize % 2 == 0 ? maskSize - 1 : maskSize;
  }

  @Override
  protected void initializeMask() {
    iterateMask((maskX, maskY) -> setMaskPixel(maskX, maskY, log(maskX, maskY, sigma)));
  }

  private double log(final int x, final int y, final double sigma) {
    final double term = (pow(x, 2) + pow(y, 2)) / pow(sigma, 2);
    return - 1 / (sqrt(2 * PI) * pow(sigma, 3))
           * (2 - term) * exp(- term / 2);
  }

  /* package-private */ /* testing-only */ double[][] getMask() {
    initializeMaskIfNecessary();
    return ArrayUtils.copyOf(mask);
  }
}

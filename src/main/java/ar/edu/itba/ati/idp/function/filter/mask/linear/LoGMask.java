package ar.edu.itba.ati.idp.function.filter.mask.linear;

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
    final int ceilSigma = (int) Math.ceil(sigma);
    final int sideLength = ceilSigma > 10 ? bigMaskSize(ceilSigma) : 2 * ceilSigma + 1;
    return new LoGMask(sideLength, sigma);
  }

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
    final double term = (Math.pow(x, 2) + Math.pow(y, 2)) / Math.pow(sigma, 2);
    return - 1 / (Math.sqrt(2 * Math.PI) * Math.pow(sigma, 3))
           * (2 - term) * Math.exp(- term / 2);
  }

  /* package-private */ /* testing-only */ double[][] getMask() {
    initializeMaskIfNecessary();
    return ArrayUtils.copyOf(mask);
  }
}

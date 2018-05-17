package ar.edu.itba.ati.idp.function.filter.mask.linear;

import ar.edu.itba.ati.idp.utils.ArrayUtils;

public final class GaussMask extends AbstractLinearMask {
  private final double sigma;

  private GaussMask(final int sideLength, final double sigma) {
    super(sideLength, sideLength);
    this.sigma = sigma;
  }

  public static GaussMask newInstance(final double sigma) {
    if (sigma <= 0) {
      throw new IllegalArgumentException("sigma should be positive");
    }
    final int ceilSigma = (int) Math.ceil(sigma);
    final int sideLength = ceilSigma > 10 ? bigMaskSize(ceilSigma) : 2 * ceilSigma + 1;
    return new GaussMask(sideLength, sigma);
  }

  public static GaussMask newInstance(final double sigma, final int sideLength) {
    if (sigma <= 0) {
      throw new IllegalArgumentException("sigma should be positive");
    }

    return new GaussMask(sideLength, sigma);
  }

  private static int bigMaskSize(final int ceilSigma) {
    final int maskSize = 3 * ceilSigma;
    // Always return odd size so as to have only one possible core position.
    return maskSize % 2 == 0 ? maskSize - 1 : maskSize;
  }

  @Override
  protected void initializeMask() {
    final double[] total = new double[] {0};
    iterateMask((maskX, maskY) -> {
      final double value = gauss(maskX, maskY, sigma);
      total[0] += value;
      setMaskPixel(maskX, maskY, value);
    });
    iterateMask(((maskX, maskY) -> setMaskPixel(maskX, maskY, getMaskPixel(maskX, maskY) / total[0])));
  }

  private double gauss(final int x, final int y, final double sigma) {
    return 1 / (2 * Math.PI * Math.pow(sigma, 2))
           * Math.exp(- (Math.pow(x, 2) + Math.pow(y, 2)) / Math.pow(sigma, 2));
  }

  /* package-private */ /* testing-only */ double[][] getMask() {
    return ArrayUtils.copyOf(mask);
  }
}

package ar.edu.itba.ati.idp.function.filter.mask.linear;

import ar.edu.itba.ati.idp.function.filter.mask.AbstractMask;

public abstract class AbstractLinearMask extends AbstractMask {
  @SuppressWarnings("WeakerAccess")
  protected AbstractLinearMask(final int width, final int height) {
    super(width, height);
  }

  @Override
  protected double applyMaskTo(final double[][] pixels, final int currCoreX,
                               final int currCoreY) {
    final double[] newPixel = new double[] {0};
    //noinspection CodeBlock2Expr
    iterateMask((maskX, maskY) -> {
      newPixel[0] += (
          clampedSafePixel(pixels, currCoreX + maskX, currCoreY + maskY)
          * getMaskPixel(maskX, maskY)
      );
    });
    return newPixel[0];
  }
}

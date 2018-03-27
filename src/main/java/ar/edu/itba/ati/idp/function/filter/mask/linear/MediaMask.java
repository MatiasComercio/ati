package ar.edu.itba.ati.idp.function.filter.mask.linear;

public final class MediaMask extends AbstractLinearMask {
  private MediaMask(final int width, final int height) {
    super(width, height);
  }

  public static MediaMask newInstance(final int width, final int height) {
    return new MediaMask(width, height);
  }

  @Override
  protected void initializeMask() {
    final double value = 1d / (getHeight() * getWidth());
    iterateMask((maskX, maskY) -> setMaskPixel(maskX, maskY, value));
  }
}

package ar.edu.itba.ati.idp.function.filter.mask.linear;

public final class HighPassMask extends AbstractLinearMask {
  private HighPassMask(final int width, final int height) {
    super(width, height);
  }

  public static HighPassMask newInstance(final int width, final int height) {
    return new HighPassMask(width, height);
  }

  @Override
  protected void initializeMask() {
    final double value = 1d / (getHeight() * getWidth());
    iterateMask((maskX, maskY) -> setMaskPixel(maskX, maskY, - value));
    // Set the core mask value.
    setMaskPixel(0, 0, (getHeight() * getWidth() - 1) * value);
  }
}

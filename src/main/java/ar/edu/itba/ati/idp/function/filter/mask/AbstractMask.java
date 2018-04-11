package ar.edu.itba.ati.idp.function.filter.mask;

public abstract class AbstractMask implements Mask {
  private final int width;
  private final int height;
  private final int coreXDistanceToLeft;
  private final int coreXDistanceToRight;
  private final int coreYDistanceToTop;
  private final int coreYDistanceToBottom;
  // Subclass responsibility to not break anything here.
  protected double[][] mask;

  private boolean maskInitialized;

  protected AbstractMask(final int width, final int height) {
    this.width = width;
    this.height = height;
    // If width/height is even, the core is at the right/bottom position, respectively
    this.coreXDistanceToLeft = width / 2; // Floor
    this.coreXDistanceToRight = width - coreXDistanceToLeft - 1;
    this.coreYDistanceToTop = height / 2; // Floor
    this.coreYDistanceToBottom = height - coreYDistanceToTop - 1;

    this.mask = new double[height][width];
    this.maskInitialized = false;
  }

  protected abstract void initializeMask();

  protected abstract double applyMaskTo(final double[][] pixels,
                                        final int currCoreX,
                                        final int currCoreY);

  @Override
  public final double apply(final double[][] pixels, final int currCoreX, final int currCoreY) {
    initializeMaskIfNecessary();
    return applyMaskTo(pixels, currCoreX, currCoreY);
  }

  protected final void initializeMaskIfNecessary() {
    if (!maskInitialized) {
      initializeMask();
      maskInitialized = true;
    }
  }

  protected double getMaskPixel(final int maskX, final int maskY) {
    return mask[maskY + coreYDistanceToTop][maskX + coreXDistanceToLeft];
  }

  protected void setMaskPixel(final int maskX, final int maskY, final double pixel) {
    mask[maskY + coreYDistanceToTop][maskX + coreXDistanceToLeft] = pixel;
  }

  protected int getWidth() {
    return width;
  }

  protected int getHeight() {
    return height;
  }

  protected void iterateMask(final MaskIterator maskIterator) {
    for (int maskY = -coreYDistanceToTop; maskY <= coreYDistanceToBottom; maskY++) {
      for (int maskX = -coreXDistanceToLeft; maskX <= coreXDistanceToRight; maskX++) {
        maskIterator.current(maskX, maskY);
      }
    }
  }

  protected interface MaskIterator {
    void current(int maskX, int maskY);
  }
}

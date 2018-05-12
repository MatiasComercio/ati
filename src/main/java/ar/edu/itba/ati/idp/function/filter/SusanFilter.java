package ar.edu.itba.ati.idp.function.filter;

import static java.lang.Math.abs;

import ar.edu.itba.ati.idp.function.filter.mask.nonlinear.SusanMask;

// Useful: https://users.fmrib.ox.ac.uk/~steve/susan/susan/susan.html
public final class SusanFilter extends Filter<SusanMask> {

  private static final double THRESHOLD = 0.10;

  public enum Type {
    NONE(0.0), BORDER(0.5), CORNER(0.75);

    private final double doubleValue;

    Type(final double doubleValue) {
      this.doubleValue = doubleValue;
    }

    public double doubleValue() {
      return doubleValue;
    }
  }

  private SusanFilter(final SusanMask mask) {
    super(mask);
  }

  public static SusanFilter newInstance(final double comparisonThreshold) {
    return new SusanFilter(SusanMask.newInstance(comparisonThreshold));
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] pixelsAfterSusanMask = super.apply(pixels);

    for (int y = 0; y < pixelsAfterSusanMask.length; y++) {
      for (int x = 0; x < pixelsAfterSusanMask[y].length; x++) {
        if (abs(pixelsAfterSusanMask[y][x] - Type.BORDER.doubleValue()) < THRESHOLD) {
          pixelsAfterSusanMask[y][x] = Type.BORDER.doubleValue();
        } else if (abs(pixelsAfterSusanMask[y][x] - Type.CORNER.doubleValue()) < THRESHOLD) {
          pixelsAfterSusanMask[y][x] = Type.CORNER.doubleValue();
        } else {
          pixelsAfterSusanMask[y][x] = Type.NONE.doubleValue();
        }
      }
    }

    return pixelsAfterSusanMask;
  }
}

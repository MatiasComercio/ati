package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.function.filter.SusanFilter.Type.BORDER;
import static ar.edu.itba.ati.idp.function.filter.SusanFilter.Type.CORNER;
import static ar.edu.itba.ati.idp.function.filter.SusanFilter.Type.NONE;
import static java.lang.Math.abs;

import ar.edu.itba.ati.idp.function.filter.mask.nonlinear.SusanMask;

// Useful: https://users.fmrib.ox.ac.uk/~steve/susan/susan/susan.html
public final class SusanFilter extends Filter<SusanMask> { // FIXME: should IMPLEMENT ColorOver ... Or 2DTo3D, depending whether it accepts or not color images.

  public enum Type {
    NONE(0.0, 0.0), BORDER(0.5, 0.05), CORNER(0.75, 0.1);

    private final double doubleValue;
    private double threshold;

    Type(final double doubleValue, final double threshold) {
      this.doubleValue = doubleValue;
      this.threshold = threshold;
    }

    public double doubleValue() {
      return doubleValue;
    }

    private double threshold() {
      return threshold;
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
        if (abs(pixelsAfterSusanMask[y][x] - BORDER.doubleValue()) < BORDER.threshold()) {
          pixelsAfterSusanMask[y][x] = BORDER.doubleValue();
        } else if (abs(pixelsAfterSusanMask[y][x] - CORNER.doubleValue()) < CORNER.threshold()) {
          pixelsAfterSusanMask[y][x] = CORNER.doubleValue();
        } else {
          pixelsAfterSusanMask[y][x] = NONE.doubleValue();
        }
      }
    }

    return pixelsAfterSusanMask;
  }
}

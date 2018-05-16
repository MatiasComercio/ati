package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.function.filter.SusanMethod.Type.BORDER;
import static ar.edu.itba.ati.idp.function.filter.SusanMethod.Type.CORNER;
import static java.lang.Math.abs;

import ar.edu.itba.ati.idp.function.ColorOverUniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.mask.nonlinear.SusanMask;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.model.ImageMatrix.Band;

// Useful: https://users.fmrib.ox.ac.uk/~steve/susan/susan/susan.html
public final class SusanMethod implements ColorOverUniquePixelsBandOperator {

  private final Filter<SusanMask> susanFilter;

  private SusanMethod(final SusanMask susanMask) {
    this.susanFilter = Filter.newInstance(susanMask);
  }

  public static SusanMethod newInstance(final double comparisonThreshold) {
    return new SusanMethod(SusanMask.newInstance(comparisonThreshold));
  }

  public static SusanMethod newSmotherInstance(final double comparisonThreshold) {
    return new SusanMethod(SusanMask.newSmotherInstance(comparisonThreshold));
  }

  @Override
  public double[][][] apply(final double[][] pixels) {
    final double[][] pixelsAfterFilter = susanFilter.apply(pixels);

    final double[][][] shapesPixelMatrix = new double[ImageMatrix.Type.BYTE_RGB
        .getNumBands()][pixelsAfterFilter.length][pixelsAfterFilter[0].length];

    for (int y = 0; y < pixelsAfterFilter.length; y++) {
      for (int x = 0; x < pixelsAfterFilter[y].length; x++) {
        if (abs(pixelsAfterFilter[y][x] - BORDER.doubleValue()) <= BORDER.threshold()) {
          shapesPixelMatrix[BORDER.colorBandIndex()][y][x] = ImageMatrix
              .getMaxNormalizedPixelValue();
        } else if (abs(pixelsAfterFilter[y][x] - CORNER.doubleValue()) <= CORNER.threshold()) {
          shapesPixelMatrix[CORNER.colorBandIndex()][y][x] = ImageMatrix
              .getMaxNormalizedPixelValue();
        }
      }
    }

    return shapesPixelMatrix;
  }

  public enum Type {
    BORDER(0.5, 0.1, Band.GREEN), CORNER(0.75, 0.14, Band.RED);

    private final double doubleValue;
    private final double threshold;
    private final Band band;

    Type(final double doubleValue, final double threshold, final Band band) {
      this.doubleValue = doubleValue;
      this.threshold = threshold;
      this.band = band;
    }

    public double doubleValue() {
      return doubleValue;
    }

    private double threshold() {
      return threshold;
    }

    public int colorBandIndex() {
      return band.getBandIndex();
    }
  }
}

package ar.edu.itba.ati.idp.function.point;

import static java.lang.Math.log;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.utils.ArrayUtils;

public class DynamicRangeCompression implements DoubleArray2DUnaryOperator {

  private static final double MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] newPixels = new double[pixels.length][];
    final double r = ArrayUtils.minAndMax(pixels)[1];

    for (int y = 0; y < pixels.length; y++) {
      newPixels[y] = new double[pixels[y].length];
      for (int x = 0; x < pixels[y].length; x++) {
        newPixels[y][x] = MAX_VALUE / log(1.0 + r) * log(1.0 + pixels[y][x]);
      }
    }
    return newPixels;
  }
}

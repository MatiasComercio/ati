package ar.edu.itba.ati.idp.function.point;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.model.ImageMatrix;

public class Threshold implements DoubleArray2DUnaryOperator {
  private final int threshold;

  public Threshold(final int threshold) {
    this.threshold = threshold;
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] newPixels = new double[pixels.length][pixels[0].length];
    //noinspection CodeBlock2Expr
    ImageMatrix.normalize(pixels, (normalizedPixel, x, y) -> {
      newPixels[y][x] = normalizedPixel <= threshold ? 0 : ImageMatrix.getMaxNormalizedPixelValue();
    });
    return newPixels;
  }
}

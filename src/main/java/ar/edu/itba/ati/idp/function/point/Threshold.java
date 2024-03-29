package ar.edu.itba.ati.idp.function.point;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.model.ImageMatrix;

public class Threshold implements DoubleArray2DUnaryOperator {
  private static final int MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  private final int threshold;

  public Threshold(final int threshold) {
    this.threshold = threshold;
  }

  public static double[][] apply(final double[][] pixels, final int threshold) {
    final double[][] newPixels = new double[pixels.length][pixels[0].length];
    //noinspection CodeBlock2Expr
    ImageMatrix.normalize(pixels, (normalizedPixel, x, y) -> {
//      final double pixel = normalizedPixel; // Normalized
      final double pixel = pixels[y][x]; // Non-Normalized
      newPixels[y][x] = applyToPixel(pixel, threshold);
    });
    return newPixels;
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    return apply(pixels, threshold);
  }

  public static double applyToPixel(final double pixel, final double threshold) {
    return pixel < threshold ? 0 : MAX_VALUE;
  }
}

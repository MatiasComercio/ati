package ar.edu.itba.ati.idp.function.point;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.util.function.DoubleUnaryOperator;

public class ContrastStretching implements DoubleArray2DUnaryOperator {
  private static final int MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  private final double r1;
  private final double r2;

  private final DoubleUnaryOperator f1;
  private final DoubleUnaryOperator f2;
  private final DoubleUnaryOperator f3;

  public ContrastStretching(final double r1, final double r2, final double s1, final double s2) {
    if (r1 < 0.0 || r2 < 0.0 || r1 >= r2) {
      throw new IllegalArgumentException("Invalid probabilities");
    }

    this.r1 = r1;
    this.r2 = r2;

    this.f1 = pixel -> Math.max(s1 / r1 * pixel, 0);
    this.f2 = pixel -> pixel;
    this.f3 = pixel -> Math.min(s2 / r2 * pixel, MAX_VALUE);
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final double[][] newPixels = new double[pixels.length][pixels[0].length];
    //noinspection CodeBlock2Expr
    ImageMatrix.normalize(pixels, (normalizedPixel, x, y) -> {
      //noinspection UnnecessaryLocalVariable
//      final double pixel = normalizedPixel; // Normalized case
      final double pixel = pixels[y][x]; //Non-Normalized case
      final double newPixel;
      if (pixel <= r1) {
        newPixel =  f1.applyAsDouble(pixel);
      } else if (pixel >= r2) {
        newPixel = f3.applyAsDouble(pixel);
      } else {
        newPixel = f2.applyAsDouble(pixel);
      }
      newPixels[y][x] = newPixel;
    });
    return newPixels;
  }
}

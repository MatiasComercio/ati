package ar.edu.itba.ati.idp.function;

import ar.edu.itba.ati.idp.model.ImageMatrix;

public enum Normalizer implements DoubleArray2DUnaryOperator {
  INSTANCE;

  @Override
  public double[][] apply(final double[][] pixels) {
    return normalizeToDouble(pixels);
  }

  public double[][] normalizeToDouble(final double[][] pixels) {
    final double[][] newPixels = new double[pixels.length][pixels[0].length];
    ImageMatrix.normalize(pixels, (normalizedPixel, x, y) -> newPixels[y][x] = normalizedPixel);
    return newPixels;
  }

  public int[][] normalizeToInt(final double[][] pixels) {
    final int[][] newPixels = new int[pixels.length][pixels[0].length];
    ImageMatrix.normalize(pixels, (normalizedPixel, x, y) -> newPixels[y][x] = normalizedPixel);
    return newPixels;
  }
}

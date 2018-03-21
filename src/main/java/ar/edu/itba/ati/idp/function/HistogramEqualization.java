package ar.edu.itba.ati.idp.function;

import ar.edu.itba.ati.idp.model.ImageMatrix;

public enum HistogramEqualization implements DoubleArray2DUnaryOperator {
  INSTANCE;

  private final int maxNormalizedPixelValue;

  HistogramEqualization() {
    this.maxNormalizedPixelValue = ImageMatrix.getMaxNormalizedPixelValue();
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    final int height = pixels.length;
    final int width = pixels[0].length;
    final double totalPixels = height * width;
    // Calculate the band density
    final double[] relativePixelDensity = new double[maxNormalizedPixelValue + 1];
    final int[][] normalizedPixels = new int[height][width];
    ImageMatrix.normalize(pixels, (normalizedPixel, x, y) -> {
      relativePixelDensity[normalizedPixel] += (1/totalPixels);
      normalizedPixels[y][x] = normalizedPixel;
    });

    // Calculate the band accumulated density
    // Values from 0 to maxNormalizedPixelValue - 1
    final double[] scaledCumPixelDensity = new double[relativePixelDensity.length];
    final double minCumPixelDensity = relativePixelDensity[0];
    scaledCumPixelDensity[0] = remapBandCumDensity(relativePixelDensity[0], minCumPixelDensity, maxNormalizedPixelValue);

    for (int j = 1; j < relativePixelDensity.length; j++) {
      // Accumulate in the original relative pixel density array
      relativePixelDensity[j] = relativePixelDensity[j] + relativePixelDensity[j-1];
      scaledCumPixelDensity[j] = remapBandCumDensity(relativePixelDensity[j], minCumPixelDensity, maxNormalizedPixelValue);
    }

    // Validate that the densities has been correctly scaled
    if (!Double.valueOf(0).equals(scaledCumPixelDensity[0]) ||
        !Double.valueOf(maxNormalizedPixelValue).equals(scaledCumPixelDensity[maxNormalizedPixelValue])) {
      final String errorMsg = String.format(
          "Illegal accumulated pixel density. Min should be %d and was %f, and Max should be %d and was %f",
          0, scaledCumPixelDensity[0], maxNormalizedPixelValue, scaledCumPixelDensity[maxNormalizedPixelValue]);
      throw new IllegalStateException(errorMsg);
    }

    // Apply the equalization to each pixel
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        pixels[y][x] = scaledCumPixelDensity[normalizedPixels[y][x]];
      }
    }
    return pixels;
  }

  private double remapBandCumDensity(final double sk, final double skMin, final int lMinus1) {
    return (int) ((sk - skMin) / (1 - skMin) * lMinus1 + 0.5);
  }
}

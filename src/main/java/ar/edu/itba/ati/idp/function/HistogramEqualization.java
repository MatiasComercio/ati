package ar.edu.itba.ati.idp.function;

import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum HistogramEqualization implements DoubleArray2DUnaryOperator {
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(HistogramEqualization.class);

  private final int maxNormalizedPixelValue;

  HistogramEqualization() {
    this.maxNormalizedPixelValue = ImageMatrix.getMaxNormalizedPixelValue();
  }

  @Override
  public double[][] apply(final double[][] pixels) {
//    return applyNormalized(pixels);
    return applyNonNormalized(pixels);
  }

  @SuppressWarnings("unused") // Save it just in case
  private double[][] applyNormalized(final double[][] pixels) {
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

  private double[][] applyNonNormalized(final double[][] pixels) {
    final int height = pixels.length;
    final int width = pixels[0].length;
    final double totalPixels = height * width;
    // Calculate the band density
    final int[][] intPixels = new int[height][width];
    final Map<Integer, Double> relativePixelDensity = new TreeMap<>();
    final double amount = (1/totalPixels);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        final int pixelValue = (int) pixels[y][x];
        relativePixelDensity.merge(pixelValue, amount, (originalValue, newValue) -> originalValue + newValue);
        intPixels[y][x] = pixelValue;
      }
    }

    // Calculate the band accumulated density
    final Map<Integer, Double> scaledCumPixelDensity = new TreeMap<>();

    final Iterator<Entry<Integer, Double>> relativePixelDensityIterator = relativePixelDensity.entrySet().iterator();
    if (!relativePixelDensityIterator.hasNext()) {
      LOGGER.error("At least one pixel was expected. Returning the same pixels...");
      return pixels;
    }

    // Entries are sorted by key, from min to max -> the first one should be treated differently
    final Entry<Integer, Double> minKeyRelativeEntry = relativePixelDensityIterator.next();
     final int minKey = minKeyRelativeEntry.getKey();
    final double minCumPixelDensity = minKeyRelativeEntry.getValue();
    scaledCumPixelDensity.put(minKeyRelativeEntry.getKey(), remapBandCumDensity(minKeyRelativeEntry.getValue(), minCumPixelDensity, maxNormalizedPixelValue));

    Entry<Integer, Double> prevRelativeEntry = minKeyRelativeEntry;
    while (relativePixelDensityIterator.hasNext()) {
      final Entry<Integer, Double> currRelativeEntry = relativePixelDensityIterator.next();
      final int currKey = currRelativeEntry.getKey();
      // Accumulate in the original relative pixel density array
      relativePixelDensity.merge(currKey, prevRelativeEntry.getValue(),
                                 (currRelativeValue, prevRelativeValue) -> currRelativeValue + prevRelativeValue);
      scaledCumPixelDensity.put(currKey, remapBandCumDensity(relativePixelDensity.get(currKey), minCumPixelDensity, maxNormalizedPixelValue));
      prevRelativeEntry = currRelativeEntry;
    }
     final int maxKey = prevRelativeEntry.getKey();



    // Validate that the densities has been correctly scaled
    if (!Double.valueOf(0).equals(scaledCumPixelDensity.get(minKey)) ||
        !Double.valueOf(maxNormalizedPixelValue).equals(scaledCumPixelDensity.get(maxKey))) {
      final String errorMsg = String.format(
          "Illegal accumulated pixel density. Min should be %d and was %f, and Max should be %d and was %f",
          0, scaledCumPixelDensity.get(minKey), maxNormalizedPixelValue, scaledCumPixelDensity.get(maxKey));
      throw new IllegalStateException(errorMsg);
    }

    // Apply the equalization to each pixel
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        pixels[y][x] = scaledCumPixelDensity.get(intPixels[y][x]);
      }
    }
    return pixels;
  }

  private double remapBandCumDensity(final double sk, final double skMin, final int lMinus1) {
    return (int) ((sk - skMin) / (1 - skMin) * lMinus1 + 0.5);
  }
}

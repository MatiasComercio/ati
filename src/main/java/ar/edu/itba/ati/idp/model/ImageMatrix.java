package ar.edu.itba.ati.idp.model;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.awt.image.BufferedImage;
import java.util.function.DoubleUnaryOperator;

public class ImageMatrix {
  private static final int DEFAULT_MAX_PIXEL_VALUE = 255;

  /** Non-Interleaved pixels */
  private final double[][][] pixels;
  private final int width;
  private final int height;
  private final Type type;

  private ImageMatrix(final double[][][] pixels, final Type type) {
    this.pixels = pixels;
    this.width = pixels[0][0].length;
    this.height = pixels[0].length;
    this.type = type;
  }

  // ========================= Builders ========================= //

  public static ImageMatrix fromBufferedImage(final BufferedImage bufferedImage) {
    final Type imageType = getBufferedImageType(bufferedImage);
    final double[][][] matrixPixels = new double[imageType.numBands][bufferedImage
        .getHeight()][bufferedImage.getWidth()];

    for (int b = 0; b < imageType.numBands; b++) {
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          matrixPixels[b][y][x] = bufferedImage.getRaster().getSampleDouble(x, y, b);
        }
      }
    }

    return new ImageMatrix(matrixPixels, imageType);
  }

  public static ImageMatrix fromNonInterleavedPixels(final double[][][] pixels) {
    final Type type;

    switch (pixels.length) { // All breaking cases here will lately derived into illegal states too
      case 1: type = Type.BYTE_G; break;
      case 3: type = Type.BYTE_RGB; break;
      case 4: type = Type.BYTE_ARGB; break;
      default: throw new IllegalStateException("Unsupported band size");
    }
    return new ImageMatrix(normalizeNonInterleavedPixelsToDouble(pixels), type);
  }

  // ========================= \Builders ========================= //

  private static Type getBufferedImageType(final BufferedImage bufferedImage) {
    switch (bufferedImage.getType()) {
      case BufferedImage.TYPE_BYTE_BINARY:
        return Type.BYTE_B;
      case BufferedImage.TYPE_BYTE_GRAY:
        return Type.BYTE_G;
      case BufferedImage.TYPE_3BYTE_BGR:
        return Type.BYTE_RGB;
      case BufferedImage.TYPE_4BYTE_ABGR:
        return Type.BYTE_ARGB;
      default:
        throw new IllegalArgumentException("Unsupported image");
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getMaxNormalizedPixelValue() {
    return DEFAULT_MAX_PIXEL_VALUE;
  }

  public BufferedImage toBufferedImage() {
    final BufferedImage bufferedImage = new BufferedImage(width, height, bufferedImageType());

    // TODO: check if normalization should be performed

    for (int b = 0; b < type.numBands; b++) {
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          bufferedImage.getRaster().setSample(x, y, b, pixels[b][y][x]);
        }
      }
    }

    return bufferedImage;
  }

  private int bufferedImageType() {
    switch (type) {
      case BYTE_B:
        return BufferedImage.TYPE_BYTE_BINARY;
      case BYTE_G:
        return BufferedImage.TYPE_BYTE_GRAY;
      case BYTE_RGB:
        return BufferedImage.TYPE_3BYTE_BGR;
      case BYTE_ARGB:
        return BufferedImage.TYPE_4BYTE_ABGR;
      default:
        throw new IllegalArgumentException("Unsupported image");
    }
  }

  public ImageMatrix apply(final DoubleArray2DUnaryOperator function) {
    final double[][][] newPixels = new double[type.numBands][][];

    for (int b = 0; b < type.numBands; b++) {
      newPixels[b] = function.apply(ArrayUtils.copyOf(pixels[b]));
    }

    return new ImageMatrix(newPixels, type);
  }

  public ImageMatrix apply(final DoubleUnaryOperator function) {
    final double[][][] newPixels = new double[type.numBands][][];

    for (int b = 0; b < type.numBands; b++) {
      newPixels[b] = apply(pixels[b], function);
    }

    return ImageMatrix.fromNonInterleavedPixels(newPixels);
  }

  private static double[][] apply(final double[][] currentPixels, final DoubleUnaryOperator func) {
    final double[][] newPixels = new double[currentPixels.length][currentPixels[0].length];

    for (int y = 0; y < newPixels.length; y++) {
      for (int x = 0; x < newPixels[0].length; x++) {
        newPixels[y][x] = func.applyAsDouble(currentPixels[y][x]);
      }
    }

    return newPixels;
  }

  public enum Type {
    BYTE_B(1), BYTE_G(1), BYTE_RGB(3), BYTE_ARGB(4);

    private final int numBands;

    Type(final int numBands) {
      this.numBands = numBands;
    }
  }
  
  // ========================= Normalizers ========================= //

  private interface PixelStore {
    void store(int normalizedPixel, int x, int y);
  }

  public int[][][] normalizePixelsToBytes() {
    return normalizeNonInterleavedPixelsToBytes(pixels);
  }

  public int[][][] normalizePixelsToInterleavedBytes() {
    final int[][][] normalizedPixels = new int[height][width][type.numBands];
    for (int bandNum = 0; bandNum < pixels.length; bandNum++) {
      final int currentBandNum = bandNum;
      normalize(pixels[bandNum], (normalizedPixel, x, y) -> normalizedPixels[y][x][currentBandNum] = normalizedPixel);
    }
    return normalizedPixels;
  }

  private static int[][][] normalizeNonInterleavedPixelsToBytes(final double[][][] pixels) {
    final int[][][] normalizedPixels = new int[pixels.length][][];
    for (int bandNum = 0; bandNum < pixels.length; bandNum++) {
      final int height = pixels[bandNum].length;
      final int width = pixels[bandNum][0].length;
      final int[][] bandPixels = new int[height][width];
      normalize(pixels[bandNum], (normalizedPixel, x, y) -> bandPixels[y][x] = normalizedPixel);
      normalizedPixels[bandNum] = bandPixels;
    }
    return normalizedPixels;
  }

  private static double[][][] normalizeNonInterleavedPixelsToDouble(final double[][][] pixels) {
    final double[][][] normalizedPixels = new double[pixels.length][][];
    for (int bandNum = 0; bandNum < pixels.length; bandNum++) {
      final int height = pixels[bandNum].length;
      final int width = pixels[bandNum][0].length;
      final double[][] bandPixels = new double[height][width];
      normalize(pixels[bandNum], (normalizedPixel, x, y) -> bandPixels[y][x] = normalizedPixel);
      normalizedPixels[bandNum] = bandPixels;
    }
    return normalizedPixels;
  }

  // You can store the normalized pixel in any structure with this implementation.
  private static void normalize(final double[][] band, final PixelStore pixelStore) {
    final double[] minAndMax = ArrayUtils.minAndMax(band);
    final double min = minAndMax[0];
    final double max = minAndMax[1];
    final double m = DEFAULT_MAX_PIXEL_VALUE / (max - min);
    final double b = -m * min;

    for (int y = 0; y < band.length; y++) {
      for (int x = 0; x < band[y].length; x++) {
        pixelStore.store((int) (m * band[y][x] + b), x, y);
      }
    }
  }

  // ========================= \Normalizers ========================= //
}

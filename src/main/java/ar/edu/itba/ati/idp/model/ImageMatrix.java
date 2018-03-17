package ar.edu.itba.ati.idp.model;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.utils.Arrays2D;
import java.awt.image.BufferedImage;
import java.util.function.DoubleUnaryOperator;

public class ImageMatrix {

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

  public void normalize() {
    for (final double[][] band : pixels) {
      normalize(band);
    }
  }

  private void normalize(final double[][] band) {
    final double[] minAndMax = Arrays2D.minAndMax(band);
    final double min = minAndMax[0];
    final double max = minAndMax[1];
    final double m = 255 / (max - min);
    final double b = -m * min;

    for (int y = 0; y < band.length; y++) {
      for (int x = 0; x < band[y].length; x++) {
        band[y][x] = m * band[y][x] + b;
      }
    }
  }

//  // TODO: Remove?
//  public double[] getPixel(final int x, final int y) {
//    return Arrays.copyOf(pixels[y][x], pixels[y][x].length);
//  }
//
//  // TODO: Remove
//  private void setPixel(final int x, final int y, final double[] pixel) {
//    arraycopy(pixel, 0, pixels[y][x], 0, pixels[y][x].length);
//  }

  public Type getType() {
    return type;
  }

  public BufferedImage toBufferedImage() {
    final BufferedImage bufferedImage = new BufferedImage(width, height, bufferedImageType());

    // TODO: normalize each band to be in range [0, 255]

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
      newPixels[b] = function.apply(Arrays2D.copyOf(pixels[b]));
    }

    return new ImageMatrix(newPixels, type);
  }

  public ImageMatrix apply(final DoubleUnaryOperator function) {
    final double[][][] newPixels = new double[type.numBands][][];

    for (int b = 0; b < type.numBands; b++) {
      newPixels[b] = apply(pixels[b], function);
    }

    return ImageMatrix.fromPixels(newPixels);
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

  public static ImageMatrix fromPixels(final double[][][] pixels) {
    final Type type;

    switch (pixels.length) { // All breaking cases here will lately derived into illegal states too
      case 1:
        type = Type.BYTE_G;
        break;
      case 3:
        type = Type.BYTE_RGB;
        break;
      case 4:
        type = Type.BYTE_ARGB;
        break;
      default:
        throw new IllegalArgumentException("Unsupported band size");
    }

    return new ImageMatrix(pixels, type);
  }

  public enum Type {
    BYTE_B(1), BYTE_G(1), BYTE_RGB(3), BYTE_ARGB(4);

    private final int numBands;

    Type(final int numBands) {
      this.numBands = numBands;
    }
  }
}

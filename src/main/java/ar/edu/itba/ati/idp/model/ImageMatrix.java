package ar.edu.itba.ati.idp.model;

import static java.lang.System.arraycopy;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageMatrix {

  public enum Type {
    BYTE_B(1), BYTE_G(1), BYTE_RGB(3), BYTE_ARGB(4);

    private final int numBands;

    Type(final int numBands) {
      this.numBands = numBands;
    }
  }

  private final double[][][] pixels;
  private final int width;
  private final int height;
  private final Type type;

  private ImageMatrix(final double[][][] pixels, final Type type) {
    this.pixels = pixels;
    this.width = pixels[0].length;
    this.height = pixels.length;
    this.type = type;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Type getType() {
    return type;
  }

  public double[] getPixel(final int x, final int y) {
    return Arrays.copyOf(pixels[y][x], pixels[y][x].length);
  }

  public void setPixel(final int x, final int y, final double[] pixel) {
    arraycopy(pixel, 0, pixels[y][x], 0, pixels[y][x].length);
  }

  public BufferedImage toBufferedImage() {
    final BufferedImage bufferedImage = new BufferedImage(width, height, bufferedImageType());

    // TODO: normalize each band to be in range [0, 255]

    for (int y = 0; y < bufferedImage.getHeight(); y++) {
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
        for (int b = 0; b < type.numBands; b++) {
          bufferedImage.getRaster().setSample(x, y, b, pixels[y][x][b]);
        }
      }
    }

    return bufferedImage;
  }

  public static ImageMatrix fromBufferedImage(final BufferedImage bufferedImage) {
    final Type imageType = getBufferedImageType(bufferedImage);
    final double[][][] matrixPixels = new double[bufferedImage.getHeight()][bufferedImage
        .getWidth()][imageType.numBands];

    for (int y = 0; y < bufferedImage.getHeight(); y++) {
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
        for (int band = 0; band < imageType.numBands; band++) {
          matrixPixels[y][x][band] = bufferedImage.getRaster().getSampleDouble(x, y, band);
        }
      }
    }

    return new ImageMatrix(matrixPixels, imageType);
  }

  public static ImageMatrix fromPixels(final double[][][] pixels) {
    final Type type;
    switch (pixels[0][0].length) { // All breaking cases here will lately derived into illegal states too
      case 1: type = Type.BYTE_G; break;
      case 3: type = Type.BYTE_RGB; break;
      case 4: type = Type.BYTE_ARGB; break;
      default: throw new IllegalStateException("Not a valid band number");
    }
    return new ImageMatrix(pixels, type);
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

  private int bufferedImageType() {
    switch (type) {
      case BYTE_B: return BufferedImage.TYPE_BYTE_BINARY;
      case BYTE_G: return BufferedImage.TYPE_BYTE_GRAY;
      case BYTE_RGB: return BufferedImage.TYPE_3BYTE_BGR;
      case BYTE_ARGB: return BufferedImage.TYPE_4BYTE_ABGR;
      default: throw new IllegalArgumentException("Unsupported image");
    }
  }
}

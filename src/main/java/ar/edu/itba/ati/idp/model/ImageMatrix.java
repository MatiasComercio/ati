package ar.edu.itba.ati.idp.model;

import static ar.edu.itba.ati.idp.model.ImageMatrix.Band.ALPHA;
import static ar.edu.itba.ati.idp.model.ImageMatrix.Band.BLACK_WHITE;
import static ar.edu.itba.ati.idp.model.ImageMatrix.Band.BLUE;
import static ar.edu.itba.ati.idp.model.ImageMatrix.Band.GREEN;
import static ar.edu.itba.ati.idp.model.ImageMatrix.Band.GREY;
import static ar.edu.itba.ati.idp.model.ImageMatrix.Band.RED;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.model.ImageHistogram.BandHistogram;
import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class ImageMatrix {

  private static final int DEFAULT_MAX_PIXEL_VALUE = 255;

  /**
   * Non-Interleaved pixels
   */
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
        throw new IllegalStateException("Unsupported band size");
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

  public static int getMaxNormalizedPixelValue() {
    return DEFAULT_MAX_PIXEL_VALUE;
  }

  // TODO: these functions may be migrated to `apply` logic
  public ImageHistogram getHistogram() {
    final List<Band> bands = type.toBands();
    final List<BandHistogram> bandHistograms = new LinkedList<>();
    for (final Band band : bands) {
      final int[] plainHistogram = new int[getMaxNormalizedPixelValue() + 1];
      normalize(pixels[band.bandIndex],
                (normalizedPixel, x, y) -> plainHistogram[normalizedPixel] ++
      );
      bandHistograms.add(BandHistogram.from(band, plainHistogram));
    }
    return ImageHistogram.from(bandHistograms);
  }

  public BufferedImage toBufferedImage() {
    final BufferedImage bufferedImage = new BufferedImage(width, height, bufferedImageType());

    final int[][][] normalizedPixels = normalizeNonInterleavedPixelsToBytes(ArrayUtils.copyOf(pixels));

    for (int b = 0; b < type.numBands; b++) {
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          bufferedImage.getRaster().setSample(x, y, b, normalizedPixels[b][y][x]);
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

  // ========================= Apply ========================= //
  public ImageMatrix add(final ImageMatrix other) {
    return apply((t, o) -> t + o, other);
  }

  public ImageMatrix subtract(final ImageMatrix other) {
    return apply((t, o) -> t - o, other);
  }

  public ImageMatrix multiply(final ImageMatrix other) {
    return apply((t, o) -> t * o, other);
  }

  private ImageMatrix apply(final DoubleBinaryOperator f, final ImageMatrix other) {
    if (other.type.numBands != this.type.numBands) {
      throw new IllegalArgumentException("Incompatible images");
    }

    final double[][][] newMatrix = new double[type.numBands][height][width];

    for (int b = 0; b < type.numBands; b++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (y < other.height && x < other.width) {
            newMatrix[b][y][x] = f.applyAsDouble(pixels[b][y][x], other.pixels[b][y][x]);
          } else {
            newMatrix[b][y][x] = pixels[b][y][x];
          }
        }
      }
    }

    return ImageMatrix.fromNonInterleavedPixels(newMatrix);
  }

  public ImageMatrix apply(final DoubleArray2DUnaryOperator function) {
    final double[][][] newPixels = new double[pixels.length][][];

    for (int bandNum = 0; bandNum < pixels.length; bandNum++) {
      newPixels[bandNum] = function.apply(ArrayUtils.copyOf(pixels[bandNum]));
    }

    return new ImageMatrix(newPixels, type);
  }

  public ImageMatrix apply(final DoubleUnaryOperator function) {
    final double[][][] newPixels = new double[pixels.length][][];

    for (int bandNum = 0; bandNum < pixels.length; bandNum++) {
      newPixels[bandNum] = apply(pixels[bandNum], function);
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
  // ========================= \Apply ========================= //

  public double[] getAveragePixel(final int x0, final int y0, final int w, final int h) {
    // TODO: Input validation
    final double[] avg = new double[type.numBands];
    int total = 0;

    for (int y = y0; y < h; y++) {
      for (int x = x0; x < w; x++) {
        total++;
        for (int b = 0; b < type.numBands; b++) {
          avg[b] += pixels[b][y][x];
        }
      }
    }

    for (int b = 0; b < type.numBands; b++) {
      avg[b] /= total;
    }

    return avg;
  }

  // ========================= Normalizers ========================= //

  public interface PixelVisitor {
    void visit(int normalizedPixel, int x, int y);
  }

  /**
   * Iterate through all pixels per band (non-interleaved), normalizing each of them and calling
   *  the given {@code pixelStore} per pixel.
   * <p>
   * Users may visit/manipulate/do whatever they need to with each normalized pixel using
   *  the built pixel visit instance.
   * @param pixelStore The pixel visit instance that will be called per each normalized pixel
   *                   (traversed in a non-interleaved manner).
   */
  public void normalize(final PixelVisitor pixelStore) {
    for (final double[][] pixel : pixels) {
      normalize(pixel, pixelStore);
    }
  }

  public int[][][] normalizePixelsToBytes() {
    return normalizeNonInterleavedPixelsToBytes(pixels);
  }

  public int[][][] normalizePixelsToInterleavedBytes() {
    final int[][][] normalizedPixels = new int[height][width][type.numBands];
    for (int bandNum = 0; bandNum < pixels.length; bandNum++) {
      final int band = bandNum;
      normalize(pixels[bandNum], (normalizedPixel, x, y) -> normalizedPixels[y][x][band] = normalizedPixel);
    }
    return normalizedPixels;
  }

  // You can visit the normalized pixel in any structure with this implementation.
  public static void normalize(final double[][] pixelsBand, final PixelVisitor pixelStore) {
    final double[] minAndMax = ArrayUtils.minAndMax(pixelsBand);
    final double min = minAndMax[0];
    final double max = minAndMax[1];
    final double m = DEFAULT_MAX_PIXEL_VALUE / (max - min);
    final double b = -m * min;

    for (int y = 0; y < pixelsBand.length; y++) {
      for (int x = 0; x < pixelsBand[y].length; x++) {
        pixelStore.visit((int) (m * pixelsBand[y][x] + b), x, y);
      }
    }
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

  // ========================= \Normalizers ========================= //

  // ========================= Type ========================= //
  public enum Type {
    BYTE_B(1, BLACK_WHITE), BYTE_G(1, GREY),
    BYTE_RGB(3, RED, GREEN, BLUE), BYTE_ARGB(4, RED, GREEN, BLUE, ALPHA);

    private final int numBands;
    private final List<Band> bands;

    Type(final int numBands, final Band... bands) {
      this.numBands = numBands;
      this.bands = Arrays.asList(bands);
    }

    public List<Band> toBands() {
      return bands;
    }
  }
  // ========================= \Type ========================= //

  // ========================= Bands ========================= //
  public enum Band {
    BLACK_WHITE(0, "Black & White", "#000000"),
    GREY(0, "Grey", "#808080"),
    RED(0, "Red", "#FF0000"),
    GREEN(1, "Green", "#00FF00"),
    BLUE(2, "Blue", "#0000FF"),
    ALPHA(3, "Alpha", "#D3D3D3");

    private final int bandIndex;
    private final String name;
    private final String hexColor;

    Band(final int bandIndex, final String name, final String hexColor) {
      this.bandIndex = bandIndex;
      this.name = name;
      this.hexColor = hexColor;
    }

    public String getHexColor() {
      return hexColor;
    }

    @Override
    public String toString() {
      return name;
    }
  }
  // ========================= \Bands ========================= //
}

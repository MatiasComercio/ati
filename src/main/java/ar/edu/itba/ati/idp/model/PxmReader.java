package ar.edu.itba.ati.idp.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// PGM: http://netpbm.sourceforge.net/doc/pgm.html
// PPM: http://netpbm.sourceforge.net/doc/ppm.html
// Based on: https://gist.github.com/armanbilge/3276d80030d1caa2ed7c
public abstract class PxmReader {

  private static final char COMMENT = '#';
  private static final int EOF = -1;
  private static final int MAX_VALUE_ONE_BYTE = 255;
  private static final int ONE_BYTE_IN_BITS = 8;

  // Supported file extensions
  private static final List<String> SUPPORTED_FILE_EXTENSIONS = Arrays.asList("pgm", "ppm");

  // PGM variables
  private static final String PGM_RAW_MAGIC_NUMBER = "P5";
  private static final String PGM_PLAIN_MAGIC_NUMBER = "P2";
  private static final PxmToPixels PGM_RAW_TO_PIXELS;
  private static final PxmToPixels PGM_PLAIN_TO_PIXELS;

  // PPM variables
  private static final String PPM_RAW_MAGIC_NUMBER = "P6";
  private static final String PPM_PLAIN_MAGIC_NUMBER = "P3";
  private static final PxmToPixels PPM_RAW_TO_PIXELS;
  private static final PxmToPixels PPM_PLAIN_TO_PIXELS;

  public static boolean supportsFileExtension(final String fileExtension) {
    final String lowerCaseFileExtension = fileExtension.toLowerCase();
    return SUPPORTED_FILE_EXTENSIONS.stream().anyMatch(supportedFileExtension -> {
      //noinspection CodeBlock2Expr
      return supportedFileExtension.equals(lowerCaseFileExtension);
    });
  }

  public static ImageFile read(final File file) throws IOException {
    return read(file.toPath());
  }

  private static ImageFile read(final Path path) throws IOException {
    try (BufferedInputStream stream = new BufferedInputStream(Files.newInputStream(path))) {
      final String magicNumber = next(stream);
      final int width = Integer.parseInt(next(stream));
      final int height = Integer.parseInt(next(stream));
      final int maxValue = Integer.parseInt(next(stream));

      final PxmToPixels pxmToPixels = getPxmToPixels(magicNumber);

      final double[][][] pixels = pxmToPixels.convert(stream, width, height, maxValue);
      final ImageMatrix imageMatrix = ImageMatrix.fromPixels(pixels);
      return new ImageFile(path.toFile(), imageMatrix);
    }
  }

  private static PxmToPixels getPxmToPixels(final String magicNumber) {
    switch (magicNumber) {
      case PGM_RAW_MAGIC_NUMBER:
        return PGM_RAW_TO_PIXELS;
      case PGM_PLAIN_MAGIC_NUMBER:
        return PGM_PLAIN_TO_PIXELS;
      case PPM_RAW_MAGIC_NUMBER:
        return PPM_RAW_TO_PIXELS;
      case PPM_PLAIN_MAGIC_NUMBER:
        return PPM_PLAIN_TO_PIXELS;
      default:
        throw new IllegalStateException("Not a valid PGM magic number");
    }
  }

  /**
   * Finds the next whitespace-delimited string in a stream, ignoring any comments.
   *
   * @param stream the stream load from
   * @return the next whitespace-delimited string
   * @throws IOException .
   */
  private static String next(final InputStream stream) throws IOException {
    final List<Byte> bytes = new LinkedList<>();
    while (true) {
      final int b = stream.read();

      if (b != -1) {
        final char c = (char) b;
        if (c == COMMENT) { // Skip comments: http://netpbm.sourceforge.net/doc/pbm.html
          int d;
          do {
            d = stream.read();
          } while (d != -1 && d != '\n' && d != '\r');
        } else if (!Character.isWhitespace(c)) {
          bytes.add((byte) b);
        } else if (bytes.size()
            > 0) { // If there is any space saved when `c` is a whitespace => return that
          break;
        }
      } else { // Finished the file
        break;
      }
    }
    final byte[] bytesArray = new byte[bytes.size()];
    for (int i = 0; i < bytesArray.length; ++i) {
      bytesArray[i] = bytes.get(i);
    }
    return new String(bytesArray);
  }


  private interface PxmToPixels {

    double[][][] convert(BufferedInputStream stream, int width, int height, int maxValue)
        throws IOException;
  }

  static {
    PGM_RAW_TO_PIXELS = (stream, width, height, maxValue) -> {
      final int bandsNum = 1; // Grey band
      final int band = 0;
      final double[][][] pixels = new double[bandsNum][height][width];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int pixel = safeReadByte(stream);
          if (pixel == EOF) {
            throw new IllegalStateException("Reached end-of-file-prematurely");
          }
          if (maxValue > MAX_VALUE_ONE_BYTE) {
            pixel <<= ONE_BYTE_IN_BITS;
            pixel &= safeReadByte(stream);
          }
          pixels[band][y][x] = pixel; // Normalization occurs only when showing the image
        }
      }

      return pixels;
    };

    PGM_PLAIN_TO_PIXELS = (stream, width, height, maxValue) -> {
      final int bandsNum = 1; // Grey band
      final int band = 0;
      final double[][][] pixels = new double[bandsNum][height][width];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int pixel;
          try {
            pixel = Integer.parseInt(next(stream));
          } catch (final NumberFormatException e) {
            throw new IllegalStateException("Reached end-of-file-prematurely");
          }
          pixels[band][y][x] = pixel;
        }
      }

      return pixels;
    };

    PPM_RAW_TO_PIXELS = (stream, width, height, maxValue) -> {
      final int bandsNum = 3; // RGB band
      final double[][][] pixels = new double[bandsNum][height][width];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          for (int band = 0; band < bandsNum; band++) {
            int pixel = safeReadByte(stream);
            if (maxValue > MAX_VALUE_ONE_BYTE) {
              pixel <<= ONE_BYTE_IN_BITS;
              pixel &= safeReadByte(stream);
            }
            pixels[band][y][x] = pixel; // Normalization occurs only when showing the image
          }
        }
      }

      return pixels;
    };

    PPM_PLAIN_TO_PIXELS = (stream, width, height, maxValue) -> {
      final int bandsNum = 3; // RGB band
      final double[][][] pixels = new double[bandsNum][height][width];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          for (int band = 0; band < bandsNum; band++) {
            int pixel;
            try {
              pixel = Integer.parseInt(next(stream));
            } catch (final NumberFormatException e) {
              throw new IllegalStateException("Reached end-of-file-prematurely");
            }
            pixels[band][y][x] = pixel;
          }
        }
      }

      return pixels;
    };
  }

  private static int safeReadByte(final BufferedInputStream stream) throws IOException {
    final int dataByte = stream.read();
    if (dataByte == EOF) {
      throw new IllegalStateException("Reached end-of-file-prematurely");
    }
    return dataByte;
  }
}

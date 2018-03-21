package ar.edu.itba.ati.idp.io;

import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmMagicNumber.P2;
import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmMagicNumber.P3;
import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmMagicNumber.P5;
import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmMagicNumber.P6;
import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmPixelIO.PLAIN;
import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmPixelIO.RAW;
import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmType.PGM;
import static ar.edu.itba.ati.idp.io.PxmImageIO.PxmType.PPM;
import static ar.edu.itba.ati.idp.io.PxmImageIOs.next;
import static ar.edu.itba.ati.idp.io.PxmImageIOs.safeReadByte;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// PGM: http://netpbm.sourceforge.net/doc/pgm.html
// PPM: http://netpbm.sourceforge.net/doc/ppm.html
// Based on: https://gist.github.com/armanbilge/3276d80030d1caa2ed7c
/* package-private */ enum PxmImageIO implements ImageIO {
  /* See: PXM_IMAGE_IO_PER_MAGIC_NUMBER */ @SuppressWarnings("unused") PGM_RAW(PGM, RAW, P5),
  /* See: PXM_IMAGE_IO_PER_MAGIC_NUMBER */ @SuppressWarnings("unused") PGM_PLAIN(PGM, PLAIN, P2),
  /* See: PXM_IMAGE_IO_PER_MAGIC_NUMBER */ @SuppressWarnings("unused") PPM_RAW(PPM, RAW, P6),
  /* See: PXM_IMAGE_IO_PER_MAGIC_NUMBER */ @SuppressWarnings("unused") PPM_PLAIN(PPM, PLAIN, P3);

  private static final Logger LOGGER = LoggerFactory.getLogger(PxmImageIO.class);
  private static final String ILLEGAL_PXM_IMAGE_IO =
      "Non-matching magic number detected for the PxmImageIO instance. Expected: %s, Found: %s";
  private static final String INVALID_PXM_MAGIC_NUMBER_ERROR_MSG =
      "Not a valid PXM magic number: {}";
  private static final int MAX_VALUE_ONE_BYTE = 255;
  private static final int ONE_BYTE_IN_BITS = 8;


  // Supported file extensions
  /* package-private */ static final Set<String> SUPPORTED_FILE_EXTENSIONS = Collections.unmodifiableSet(
      Stream.of(PxmType.values())
          .map(pxmType -> {
            final String pxmTypeName = pxmType.name();
            return Stream.of(pxmTypeName, pxmTypeName.toLowerCase());
          })
          .flatMap(stringStream -> stringStream)
          .collect(Collectors.toSet())
  );


  private static final Map<PxmMagicNumber, PxmImageIO> PXM_IMAGE_IO_PER_MAGIC_NUMBER;
  static {
    final Map<PxmMagicNumber, PxmImageIO> map = new EnumMap<>(PxmMagicNumber.class);
    for (final PxmImageIO pxmImageIO : PxmImageIO.values()) {
      map.put(pxmImageIO.magicNumber, pxmImageIO);
    }
    PXM_IMAGE_IO_PER_MAGIC_NUMBER = Collections.unmodifiableMap(map);
  }

  // Non static stuff
  private final PxmMagicNumber magicNumber;
  private final PxmType pxmType;
  private final PxmPixelIO pxmPixelIO;

  PxmImageIO(final PxmType pxmType, final PxmPixelIO pxmPixelIO,
      final PxmMagicNumber magicNumber) {
    this.magicNumber = magicNumber;
    this.pxmType = pxmType;
    this.pxmPixelIO = pxmPixelIO;
  }

  @Override
  public Set<String> getSupportedFileExtensions() {
    return SUPPORTED_FILE_EXTENSIONS;
  }

  @Override
  public ImageFile load(final File file) throws IOException {
    try (InputStream stream = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
      final String magicNumber = next(stream);
      // If using the instance `load`, the magic number of the file being loaded should be
      //  equal to the magic number that this instance is prepared to parse.
      if (!this.equals(getFromMagicNumber(magicNumber))) {
        final String errorMsg = String.format(ILLEGAL_PXM_IMAGE_IO, this.magicNumber, magicNumber);
        throw new IllegalStateException(errorMsg);
      }
      return load(file, stream);
    }
  }

  @Override
  public void save(final ImageFile imageFile) throws IOException {
    // Loosing comments of original file... never mind
    final ImageMatrix imageMatrix = imageFile.getImageMatrix();
    try (PrintStream stream = new PrintStream(new BufferedOutputStream(
        Files.newOutputStream(imageFile.getFile().toPath()))
    )) {
      // Write metadata
      stream.print(magicNumber.name());
      stream.println();
      stream.print(String.valueOf(imageMatrix.getWidth()));
      stream.print(' ');
      stream.print(String.valueOf(imageMatrix.getHeight()));
      stream.println();
      stream.print(String.valueOf(ImageMatrix.getMaxNormalizedPixelValue()));
      stream.println();
      // Write image data
      final int[][][] pixels = imageMatrix.normalizePixelsToInterleavedBytes();
      long pixelArrayPosition = 0;
      //noinspection ForLoopReplaceableByForEach
      for (int y = 0; y < pixels.length; y++) {
        for (int x = 0; x < pixels[y].length; x++) {
          for (int bandNum = 0; bandNum < pixels[y][x].length; bandNum++) {
            pxmPixelIO.setPixel(pixels[y][x][bandNum], pixelArrayPosition, stream);
            pixelArrayPosition ++;
          }
        }
      }
    }
  }

  // No need to close the stream; magic number already consumed
  /* package-private */ ImageFile load(final File file, final InputStream stream) throws IOException {
    // Remaining metadata
    final int width = Integer.parseInt(next(stream));
    final int height = Integer.parseInt(next(stream));
    final int maxValue = Integer.parseInt(next(stream));
    // Image data
    final double[][][] pixels = pxmToPixels(stream, width, height, maxValue);
    final ImageMatrix imageMatrix = ImageMatrix.fromNonInterleavedPixels(pixels);
    return new ImageFile(file, imageMatrix, this);
  }

  /* package-private */ double[][][] pxmToPixels(final InputStream stream, int width, int height,
      int maxValue) throws IOException {
    final int bandsNum = pxmType.bandsNum;
    final double[][][] pixels = new double[bandsNum][height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int band = 0; band < bandsNum; band++) { // interleaved
          pixels[band][y][x] = pxmPixelIO.getPixel(stream, maxValue); // non-interleaved
        }
      }
    }
    return pixels;
  }

  // ===============================================================================================
  // ===============================================================================================
  //                                        Auxiliary Stuff
  // ===============================================================================================
  // ===============================================================================================

  // Pxm Magic Numbers
  /* package-private */ enum PxmMagicNumber {
    P5, P2, P6, P3
  }

  /**
   * Get the PxmImageIO instance corresponding to the given magicNumber.
   *
   * @param magicNumber The magic number used to get the matching PxmImageIO instance.
   * @return The PxmImageIO instance corresponding to the given magicNumber;
   *         null if the magic number does not match any PxmImageIO instance.
   */
  public static PxmImageIO getFromMagicNumber(final String magicNumber) {
    try {
      return PXM_IMAGE_IO_PER_MAGIC_NUMBER.get(PxmMagicNumber.valueOf(magicNumber));
    } catch (final IllegalArgumentException e) {
      LOGGER.error(INVALID_PXM_MAGIC_NUMBER_ERROR_MSG, magicNumber);
      return null;
    }
  }

  // Pxm Pixel Parsers
  /* package-private */ enum PxmPixelIO {
    PLAIN { // Pixels are ASCII characters
      /*
       * This number has been empirically extracted from PxM (PGM & PPM) file samples.
       * This also ensures that the line length is no longer than 70 characters as the max length
       *  would be: 12 * (3 [max pixel size as chars] + 1 [space | \r | \n]) + 1 [\n] = 49
       */
      private int PIXELS_PER_LINE = 12;

      @Override
      public double getPixel(final InputStream stream, final int maxValue) throws IOException {
        return Integer.parseInt(next(stream));
      }

      @Override
      public void setPixel(final int pixel, final long pixelArrayPosition, final PrintStream stream) {
        if (pixelArrayPosition > 0) {
          if (pixelArrayPosition % PIXELS_PER_LINE == 0) {
            stream.println();
          } else {
            stream.print(' ');
          }
        }
        stream.print(pixel);
      }
    },
    RAW { // Pixels are bytes
      @Override
      public double getPixel(final InputStream stream, final int maxValue)
          throws IOException {
        int pixel = safeReadByte(stream);
        if (maxValue > MAX_VALUE_ONE_BYTE) {
          pixel <<= ONE_BYTE_IN_BITS;
          pixel &= safeReadByte(stream);
        }
        /*
         * Normalization occurs when creating the image matrix, IGNORING the given max value
         * and taking the right max value from the image values. This is done this way
         * as it may happen that the max value is bad
         * (not representative of the actual matrix values)
         */
        return pixel;
      }

      @Override
      public void setPixel(final int pixel, final long pixelArrayPosition, final PrintStream stream) {
        stream.write(pixel);
      }
    };

    public abstract double getPixel(final InputStream stream, final int maxValue) throws IOException;

    public abstract void setPixel(final int pixel, final long pixelArrayPosition, final PrintStream stream);
  }

  /* package-private */ enum PxmType {
    PGM(1), PPM(3);

    private final int bandsNum;

    PxmType(final int bandsNum) {
      this.bandsNum = bandsNum;
    }
  }
}

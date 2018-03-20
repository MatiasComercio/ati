package ar.edu.itba.ati.idp.io;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* package-private */ enum RawImageIO implements ImageIO {
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(RawImageIO.class);
  private static final String HEADER_FILE_EXTENSION = "header";
  private static final String RAW_FILE_EXTENSION = "raw";
  private static final Set<String> SUPPORTED_EXTENSIONS;
  private static final String NO_ASSOCIATED_HEADER_FILE_ERROR_MSG =
      "HEADER file for RAW image `{}` was not found (expected: `{}`)";
  private static final String ERROR_WHILE_SCANNING_FILE_MSG =
      "Error while scanning file: {}. Aborting...";

  static {
    final Set<String> supportedExtensions = new HashSet<>();
    supportedExtensions.add(RAW_FILE_EXTENSION);
    supportedExtensions.add(RAW_FILE_EXTENSION.toUpperCase());
    SUPPORTED_EXTENSIONS = Collections.unmodifiableSet(supportedExtensions);
  }

  @Override
  public Set<String> getSupportedFileExtensions() {
    return SUPPORTED_EXTENSIONS;
  }

  /**
   * Raw files and its associated header should have the same (even matching case) basename.
   * The extension case should also match.
   * Raw files extension: .raw
   * Header files extension: .header
   */
  @Override
  public ImageFile load(final File rawFile) throws IOException {
    final File headerFile = buildHeaderFile(rawFile);
    if (!Files.exists(headerFile.toPath())) {
      LOGGER.error(NO_ASSOCIATED_HEADER_FILE_ERROR_MSG, headerFile.getPath(), rawFile.getPath());
      return null;
    }
    // header file should have: width height
    final Scanner scanner = new Scanner(
        new BufferedInputStream(Files.newInputStream(headerFile.toPath()))
    );
    final int width;
    final int height;
    try {
      width = scanner.nextInt();
      height = scanner.nextInt();
    } catch (final Exception e) {
      LOGGER.error(ERROR_WHILE_SCANNING_FILE_MSG, headerFile, e);
      throw new IOException(e);
    }

    final InputStream inputStream = Files.newInputStream(rawFile.toPath());

    final int bandsNum = 1;
    final int band = 0;
    final double[][][] pixels = new double[bandsNum][height][width];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        pixels[band][y][x] = inputStream.read();
      }
    }

    return new ImageFile(rawFile, ImageMatrix.fromNonInterleavedPixels(pixels), INSTANCE);
  }

  @Override
  public void save(final ImageFile rawImageFile) throws IOException {
    final File contentFile = rawImageFile.getFile();
    final File headerFile = buildHeaderFile(contentFile);
    final ImageMatrix imageMatrix = rawImageFile.getImageMatrix();
    final int[][][] pixelsAsBytesPerBand = imageMatrix.normalizePixelsToBytes();

    // Image file should be a raw image => it MUST have only one band
    final int[][] rawImagePixels = pixelsAsBytesPerBand[0];
    final OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(
        contentFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
    ));

    // Dump all pixels in the raw file in the correct order
    for (final int[] pixelsRow : rawImagePixels) {
      for (final int pixel : pixelsRow) {
        outputStream.write(pixel);
      }
    }
    outputStream.close();

    // Dump the header file
    final BufferedWriter bufferedWriter = Files.newBufferedWriter(
        headerFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
    );
    bufferedWriter.write(String.valueOf(imageMatrix.getWidth()));
    bufferedWriter.write(' ');
    bufferedWriter.write(String.valueOf(imageMatrix.getHeight()));
    bufferedWriter.close();
  }

  private static File buildHeaderFile(final File contentFile) {
    final String fileName = contentFile.getPath();
    final String rawExtension = FilenameUtils.getExtension(fileName);
    final String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
    // Choose lower or upper case extension to match the original header file convention
    final String headerExtension = RAW_FILE_EXTENSION.equals(rawExtension) ?
        HEADER_FILE_EXTENSION : HEADER_FILE_EXTENSION.toUpperCase();
    final String headerFileName = String.format("%s.%s", fileNameWithoutExtension , headerExtension);
    return new File(headerFileName);
  }
}

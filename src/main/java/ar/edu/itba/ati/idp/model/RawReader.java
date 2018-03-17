package ar.edu.itba.ati.idp.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Scanner;

public class RawReader {
  private static final String HEADER_FILE_EXTENSION = "header";
  private static final String RAW_FILE_EXTENSION = "raw";

  public static boolean isHeaderFile(final String fileExtension) {
    return HEADER_FILE_EXTENSION.equalsIgnoreCase(fileExtension);
  }

  public static boolean isRawFile(final String fileExtension) {
    return RAW_FILE_EXTENSION.equalsIgnoreCase(fileExtension);
  }

  public static ImageMatrix read(final File headerFile, final File rawFile) throws IOException {
    // header file should have: width height
    final Scanner scanner = new Scanner(Files.newInputStream(headerFile.toPath()));
    final int width = scanner.nextInt();
    final int height = scanner.nextInt();

    final InputStream inputStream = Files.newInputStream(rawFile.toPath());

    final int bandsNum = 1;
    final int band = 0;
    final double[][][] pixels = new double[bandsNum][height][width];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        pixels[band][y][x] = inputStream.read();
      }
    }
    return ImageMatrix.fromPixels(pixels);
  }
}

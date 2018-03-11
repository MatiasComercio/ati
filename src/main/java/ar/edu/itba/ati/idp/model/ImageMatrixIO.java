package ar.edu.itba.ati.idp.model;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public abstract class ImageMatrixIO {
  public static ImageMatrix read(final Path path) throws IOException {
    return read(requireNonNull(path).toFile());
  }

  public static ImageMatrix read(final File file) throws IOException {
    final String fileExtension = getFileExtension(file);
    if (PxmReader.supportsFileExtension(fileExtension)) {
      return PxmReader.read(file);
    } else { // default case is buffered image
      return ImageMatrix.fromBufferedImage(ImageIO.read(requireNonNull(file)));
    }
  }

  public static void write(final ImageMatrix imageMatrix, final File file) throws IOException {
    ImageIO.write(imageMatrix.toBufferedImage(), getFileExtension(file), file);
  }

  // From: https://stackoverflow.com/q/3571223
  private static String getFileExtension(final File file) {
    final String name = file.getName();

    return name.substring(name.lastIndexOf(".") + 1);
  }
}

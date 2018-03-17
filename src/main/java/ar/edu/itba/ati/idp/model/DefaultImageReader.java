package ar.edu.itba.ati.idp.model;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// Default image reader uses a buffered image
public class DefaultImageReader {

  public static ImageFile read(final File file) throws IOException {
    final ImageMatrix imageMatrix = ImageMatrix.fromBufferedImage(ImageIO.read(requireNonNull(file)));
    return new ImageFile(file, imageMatrix);
  }
}

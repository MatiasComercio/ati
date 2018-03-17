package ar.edu.itba.ati.idp.model;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

public abstract class ImageMatrixIO {
  public static ImageMatrix read(final List<File> files) throws IOException {
    /*
     * Raw files and its associated header should have the same basename.
     * Raw files extension: .raw
     * Header files extension: .header
     */
    final Iterator<File> fileIterator = files.stream().sorted().iterator();
    final ImageMatrix imageMatrix;
    while (fileIterator.hasNext()) { // xnow: only returning the first
      File file = fileIterator.next();
      String fileExtension = getFileExtension(file);
      // xnow: improve flow to try with the next format for the collected file in case: file1 is .header && file2 is .pxm
      if (RawReader.isHeaderFile(fileExtension) && fileIterator.hasNext()) {
        final File headerFile = file;
        file = fileIterator.next();
        fileExtension = getFileExtension(file);
        if (RawReader.isRawFile(fileExtension)) {
          return RawReader.read(headerFile, file);
        }
      } else if (PxmReader.supportsFileExtension(fileExtension)) {
        return PxmReader.read(file);
      } else { // default case is buffered image
        return ImageMatrix.fromBufferedImage(ImageIO.read(requireNonNull(file)));
      }
    }
    return null; // No file
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

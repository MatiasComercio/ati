package ar.edu.itba.ati.idp.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImageFiles {
  private static final Logger LOGGER = LoggerFactory.getLogger(ImageFiles.class);

  /*
   * Raw files and its associated header should have the same (even matching case) basename.
   * The extension case should also match.
   * Raw files extension: .raw
   * Header files extension: .header
   */
  public static List<ImageFile> load(final List<File> files) throws IOException {
    final List<ImageFile> imageFiles = new LinkedList<>();
    final Iterator<File> fileIterator = files.stream().sorted().iterator();

    File lastHeaderFile = null;
    while (fileIterator.hasNext()) {
      final File file = fileIterator.next();
      final String fileExtension = getFileExtension(file);

      if (RawReader.isHeaderFile(getFileExtension(file))) {
        lastHeaderFile = file;
      } else if (RawReader.isRawFile(fileExtension)) {
        if (lastHeaderFile != null) {
          try {
            imageFiles.add(RawReader.read(lastHeaderFile, file));
          } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
          } finally {
            lastHeaderFile = null;
          }
        }
      } else if (PxmReader.supportsFileExtension(fileExtension)) {
        imageFiles.add(PxmReader.read(file));
      } else {
        imageFiles.add(DefaultImageReader.read(file));
      }
    }
    return imageFiles;
  }

  // From: https://stackoverflow.com/q/3571223
  public static String getFileExtension(final File file) {
    final String name = file.getName();

    return name.substring(name.lastIndexOf(".") + 1);
  }
}

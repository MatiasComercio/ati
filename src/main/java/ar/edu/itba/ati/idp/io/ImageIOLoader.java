package ar.edu.itba.ati.idp.io;

import ar.edu.itba.ati.idp.model.ImageFile;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface ImageIOLoader {
  /**
   * Load the given image file into an ImageFile instance.
   * <p>
   * Read all necessary extra data from disk that may be needed to load the given file.
   *
   * @param file The file path containing the image to be loaded
   * @return The ImageFile loaded or null if there was any error during the load.
   * @throws IOException If an unexpected condition is met while loading the image.
   */
  ImageFile load(File file) throws IOException;

  /**
   * Get all the supported file extensions (lower & upper case).
   * @return All the supported file extensions (lower & upper case).
   */
  Set<String> getSupportedFileExtensions();

  /**
   * Check whether the given file extension is supported or not.
   * @param fileExtension The file extension to be tested against supported extensions.
   * @return true if the given file extension is supported; false otherwise.
   */
  default boolean supportsFileExtension(final String fileExtension) {
    return getSupportedFileExtensions().contains(fileExtension);
  }
}

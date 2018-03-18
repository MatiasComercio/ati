package ar.edu.itba.ati.idp.io;

import ar.edu.itba.ati.idp.model.ImageFile;
import java.io.File;
import java.io.IOException;

public interface ImageIOSaver {
  /**
   * Save the given ImageFile instance to its associated file.
   * <p>
   * Create all extra data that on disk that may be needed to later load the save file using
   * the {@link ImageIO#load(File)} method.
   *
   * @param imageFile The ImageFile instance whose content (using its information) will be saved
   *                  to disk.
   * @throws IOException if an unexpected condition is met while saving the image.
   */
  void save(ImageFile imageFile) throws IOException;
}

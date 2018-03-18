package ar.edu.itba.ati.idp.io;

import static ar.edu.itba.ati.idp.io.PxmImageIOs.next;

import ar.edu.itba.ati.idp.model.ImageFile;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

/* package-private */ enum PxmImageIOLoader implements ImageIOLoader {
  INSTANCE;

  @Override
  public Set<String> getSupportedFileExtensions() {
    return PxmImageIO.SUPPORTED_FILE_EXTENSIONS;
  }

  @Override
  public ImageFile load(final File file) throws IOException {
    try (BufferedInputStream stream = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
      final String magicNumber = next(stream);
      final PxmImageIO pxmImageIO = PxmImageIO.getFromMagicNumber(magicNumber);
      return pxmImageIO == null ? null : pxmImageIO.load(file, stream);
    }
  }
}

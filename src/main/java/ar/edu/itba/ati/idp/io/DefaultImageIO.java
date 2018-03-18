package ar.edu.itba.ati.idp.io;


import static java.util.Objects.requireNonNull;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;

// Default image reader uses a buffered image
/* package-private */ enum DefaultImageIO implements ImageIO {
  INSTANCE;

  private static final Set<String> SUPPORTED_EXTENSIONS;

  static {
    // Only support formats that can be both read & write
    // Note that these methods already return lower & upper case extensions
    final Set<String> readExtensions = new HashSet<>(Arrays.asList(javax.imageio.ImageIO.getReaderFormatNames()));
    final Set<String> writeExtensions = new HashSet<>(Arrays.asList(javax.imageio.ImageIO.getWriterFormatNames()));
    readExtensions.retainAll(writeExtensions);
    SUPPORTED_EXTENSIONS = Collections.unmodifiableSet(readExtensions);
  }

  @Override
  public Set<String> getSupportedFileExtensions() {
    return SUPPORTED_EXTENSIONS;
  }

  @Override
  public ImageFile load(final File file) throws IOException {
    final ImageMatrix imageMatrix = ImageMatrix.fromBufferedImage(javax.imageio.ImageIO.read(requireNonNull(file)));
    return new ImageFile(file, imageMatrix, INSTANCE);
  }

  @Override
  public void save(final ImageFile imageFile) throws IOException {
    final File file = imageFile.getFile();
    final ImageMatrix imageMatrix = imageFile.getImageMatrix();
    javax.imageio.ImageIO
        .write(imageMatrix.toBufferedImage(), FilenameUtils.getExtension(file.getPath()), file);
  }
}

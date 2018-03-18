package ar.edu.itba.ati.idp.io;

import ar.edu.itba.ati.idp.model.ImageFile;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ImageLoader {
  ;

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);
  private static final String ERROR_LOADING_IMAGE_FILE_MSG = "IO error when loading image file: {}";
  private static final String NOT_SUPPORTED_EXTENSION_MSG =
      "The given file `{}` has an unsupported extension. Skipping...";

  private static final List<ImageIOLoader> IMAGE_IO_LOADERS;
  private static final Set<String> SUPPORTED_FILE_EXTENSIONS;

  static {
    final List<ImageIOLoader> imageIOLoaders = new LinkedList<>();
    imageIOLoaders.add(RawImageIO.INSTANCE);
    imageIOLoaders.add(PxmImageIOLoader.INSTANCE);
    imageIOLoaders.add(DefaultImageIO.INSTANCE);

    IMAGE_IO_LOADERS = Collections.unmodifiableList(imageIOLoaders);
    SUPPORTED_FILE_EXTENSIONS = Collections.unmodifiableSet(
        imageIOLoaders.stream().map(ImageIOLoader::getSupportedFileExtensions)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet())
    );
  }

  public static Set<String> getSupportedFileExtensions() {
    return SUPPORTED_FILE_EXTENSIONS;
  }

  public static List<ImageFile> load(final List<File> files) {
    final List<ImageFile> imageFiles = new LinkedList<>();

    files.forEach(file -> {
      final ImageFile imageFile;
      try {
        imageFile = load(file);
      } catch (IOException e) {
        LOGGER.error(ERROR_LOADING_IMAGE_FILE_MSG, file, e);
        return;
      }

      if (imageFile == null) {
        LOGGER.info(NOT_SUPPORTED_EXTENSION_MSG, file.getPath());
      } else {
        imageFiles.add(imageFile);
      }
    });

    return imageFiles;
  }

  private static ImageFile load(final File file) throws IOException {
    final String fileExtension = FilenameUtils.getExtension(file.getPath());
    for (final ImageIOLoader imageIOLoader : IMAGE_IO_LOADERS) {
      if (imageIOLoader.supportsFileExtension(fileExtension)) {
        return imageIOLoader.load(file);
      }
    }
    return null;
  }
}

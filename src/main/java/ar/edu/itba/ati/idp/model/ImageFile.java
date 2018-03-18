package ar.edu.itba.ati.idp.model;

import ar.edu.itba.ati.idp.io.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageFile {
  private final ImageMatrix imageMatrix;
  private final ImageIO imageIO;

  private File file;

  public ImageFile(final File file, final ImageMatrix imageMatrix, final ImageIO imageIO) {
    this.file = file;
    this.imageMatrix = imageMatrix;
    this.imageIO = imageIO;
  }

  public File getFile() {
    return file;
  }

  public ImageMatrix getImageMatrix() {
    return imageMatrix;
  }

  public void save() throws IOException {
    imageIO.save(this);
  }

  public void saveAs(final File newFile) throws IOException {
    this.file = newFile;
    save();
  }
}

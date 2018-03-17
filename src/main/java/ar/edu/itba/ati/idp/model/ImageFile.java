package ar.edu.itba.ati.idp.model;

import static ar.edu.itba.ati.idp.model.ImageFiles.getFileExtension;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageFile {
  protected File file;
  protected final ImageMatrix imageMatrix;

  public ImageFile(final File file, final ImageMatrix imageMatrix) {
    this.file = file;
    this.imageMatrix = imageMatrix;
  }

  public File getFile() {
    return file;
  }

  public ImageMatrix getImageMatrix() {
    return imageMatrix;
  }

  public void saveToFile() throws IOException {
    // TODO: implement different strategies depending on the type of the original file
    ImageIO.write(imageMatrix.toBufferedImage(), getFileExtension(file), file);
  }

  public void saveToFile(final File newFile) throws IOException {
    this.file = newFile;
    saveToFile();
  }
}

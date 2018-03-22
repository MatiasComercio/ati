package ar.edu.itba.ati.idp.model;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.io.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageFile {
  private final ImageIO imageIO;

  private File file;
  private ImageMatrix imageMatrix;

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

  public void apply(final DoubleArray2DUnaryOperator function) {
    imageMatrix = imageMatrix.apply(function);
  }

  public void setImageMatrix(final ImageMatrix newImageMatrix) {
    imageMatrix = newImageMatrix;
  }
}

package ar.edu.itba.ati.idp.model;

import java.io.File;
import java.io.IOException;

public class RawImageFile extends ImageFile {
  protected File headerFile;

  public RawImageFile(final File headerFile, final File file, final ImageMatrix imageMatrix) {
    super(file, imageMatrix);
    this.headerFile = headerFile;
  }

  public File getHeaderFile() {
    return headerFile;
  }

  @Override
  public void saveToFile() throws IOException {
    super.saveToFile();
    // TODO: implement saving header file
  }

  @Override
  public void saveToFile(final File newFile) throws IOException {
    super.saveToFile(newFile);
    // TODO: implement saving header file
  }
}

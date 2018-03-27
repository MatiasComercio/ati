package ar.edu.itba.ati.idp.model;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.io.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

public class ImageFile {
  private static final int UNDO_REDO_SIZE_LIMIT = 10;
  private final ImageIO imageIO;
  private final Deque<ImageMatrix> undoImageMatrices;
  private final Deque<ImageMatrix> redoImageMatrices;

  private File file;
  private ImageMatrix imageMatrix;

  public ImageFile(final File file, final ImageMatrix imageMatrix, final ImageIO imageIO) {
    this.file = Objects.requireNonNull(file);
    this.imageMatrix = Objects.requireNonNull(imageMatrix);
    this.imageIO = Objects.requireNonNull(imageIO);
    this.undoImageMatrices = new LinkedList<>();
    this.redoImageMatrices = new LinkedList<>();
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
    this.file = Objects.requireNonNull(newFile);
    save();
  }

  public void apply(final DoubleArray2DUnaryOperator function) {
    setImageMatrix(imageMatrix.apply(function));
  }

  // TODO: should be private...
  public void setImageMatrix(final ImageMatrix newImageMatrix) {
    push(undoImageMatrices, imageMatrix);
    // Remove all previous redo images (this collection is only fulfilled by undo operations)
    redoImageMatrices.clear();
    imageMatrix = newImageMatrix;
  }

  public boolean undo() {
    return swapImageMatrix(undoImageMatrices, redoImageMatrices);
  }

  public boolean redo() {
    return swapImageMatrix(redoImageMatrices, undoImageMatrices);
  }

  private boolean swapImageMatrix(final Deque<ImageMatrix> loadDeque,
                               final Deque<ImageMatrix> storeDeque) {
    if (loadDeque.isEmpty()) return false; // Nothing to load
    push(storeDeque, imageMatrix);
    imageMatrix = loadDeque.pop();
    return true;
  }

  private void push(final Deque<ImageMatrix> deque, final ImageMatrix imageMatrix) {
    if (deque.size() >= UNDO_REDO_SIZE_LIMIT) {
      deque.poll(); // Discard the oldest image
    }
    deque.push(imageMatrix);
  }
}

package ar.edu.itba.ati.idp.model;

import ar.edu.itba.ati.idp.function.ColorOverUniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.io.ImageIO;
import ar.edu.itba.ati.idp.io.ImageIOs;
import ar.edu.itba.ati.idp.io.ImageLoader;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class ImageFile {
  private static final int UNDO_REDO_SIZE_LIMIT = 10;
  private final Deque<ImageMatrix> undoImageMatrices;
  private final Deque<ImageMatrix> redoImageMatrices;
  private final Map<ImageMatrix, ImageIO> imageIOPerImageMatrix;

  private final File originalFile;
  private File file;
  private ImageMatrix imageMatrix;
  private ImageIO imageIO;

  public ImageFile(final File originalFile, final ImageMatrix imageMatrix, final ImageIO imageIO) {
    this.originalFile = Objects.requireNonNull(originalFile);
    this.file = this.originalFile;
    this.imageMatrix = Objects.requireNonNull(imageMatrix);
    this.imageIO = Objects.requireNonNull(imageIO);
    this.undoImageMatrices = new LinkedList<>();
    this.redoImageMatrices = new LinkedList<>();
    this.imageIOPerImageMatrix = new HashMap<>();
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

  public void apply(final UniquePixelsBandOperator function) {
    setImageMatrix(imageMatrix.apply(function));
  }

  public void apply(final ColorOverUniquePixelsBandOperator function) {
    setImageMatrix(imageMatrix.apply(function));
  }

  // TODO: should be private...
  public void setImageMatrix(final ImageMatrix newImageMatrix) {
    /*
     * Remove all previous redo images (this collection is only fulfilled by undo operations),
     * along with their associated imageIOs, if any
     */
    while (redoImageMatrices.size() > 0) {
      imageIOPerImageMatrix.remove(redoImageMatrices.pop());
    }

    // Add this image matrix to the undo queue
    push(undoImageMatrices, imageMatrix);
    // Associate the imageIO to the enqueued imageMatrix, if type changed.
    if (imageMatrix.getType() != newImageMatrix.getType()) {
      imageIOPerImageMatrix.put(imageMatrix, imageIO);
      imageIO = ImageIOs.getImageIO(newImageMatrix.getType());
    }

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
    final ImageMatrix newImageMatrix = loadDeque.pop();
    if (imageMatrix.getType() != newImageMatrix.getType()) {
      imageIOPerImageMatrix.put(imageMatrix, imageIO);
      imageIO = imageIOPerImageMatrix.remove(newImageMatrix);
    }
    imageMatrix = newImageMatrix;
    return true;
  }

  private void push(final Deque<ImageMatrix> deque, final ImageMatrix imageMatrix) {
    if (deque.size() >= UNDO_REDO_SIZE_LIMIT) {
      imageIOPerImageMatrix.remove(deque.poll()); // Discard the oldest image along with its imageIO
    }
    deque.push(imageMatrix);
  }

  public ImageFile duplicate() {
    final ImageFile imageFile = new ImageFile(originalFile, imageMatrix.duplicate(), imageIO);
    imageFile.file = file;
    imageFile.undoImageMatrices.addAll(undoImageMatrices);
    imageFile.redoImageMatrices.addAll(redoImageMatrices);
    imageFile.imageIOPerImageMatrix.putAll(imageIOPerImageMatrix);
    return imageFile;
  }

  public File getOriginalFile() {
    return originalFile;
  }
}

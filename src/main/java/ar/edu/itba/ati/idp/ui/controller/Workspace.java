package ar.edu.itba.ati.idp.ui.controller;

import static javafx.embed.swing.SwingFXUtils.toFXImage;

import ar.edu.itba.ati.idp.function.ColorOverUniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.io.ImageLoader;
import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.controller.menu.MenuBarController;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Workspace {
  private static final Logger LOGGER = LoggerFactory.getLogger(Workspace.class);
  private static final String WORKSPACE_LAYOUT_PATH = "ui/workspace.fxml";
  private static final String DEFAULT_WORKSPACE_NAME = "ImgEditor";
  private static final double MIN_STAGE_WIDTH = 400;
  private static final double MIN_STAGE_HEIGHT = 300;
  private static final KeyCombination UNDO_KEYS =
      new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
  private static final KeyCombination REDO_KEYS =
      new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
  private static final KeyCombination DUPLICATE_KEYS =
      new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);

  // Important! If the FXML element needs a reference to this workspace,
  // assign it in the #start method of this class

  @FXML
  private MenuBarController mainMenuBar;

  @FXML
  public DropPaneController dropPane;

  @FXML
  private BorderPane mainPane;

  private ImagePaneController imagePane; // Created programmatically
  private Stage stage;
  private ImageFile imageFile;

  private void start(final Stage stage) {
    // Save the stage for oneself
    this.stage = stage;

    this.stage.setTitle(DEFAULT_WORKSPACE_NAME);
    this.stage.centerOnScreen();
    // this.stage.setOnCloseRequest(we -> {}); // TODO: handle not saved changes & ask for exit confirmation
    this.stage.setMinWidth(MIN_STAGE_WIDTH);
    this.stage.setMinHeight(MIN_STAGE_HEIGHT);

    this.stage.addEventFilter(KeyEvent.KEY_PRESSED, undoHandler());
    this.stage.addEventFilter(KeyEvent.KEY_PRESSED, redoHandler());
    this.stage.addEventFilter(KeyEvent.KEY_PRESSED, duplicateHandler());

    // Make the stage visible
    this.stage.show();

    // Load remaining elements & Configure oneself as the workspace for all elements that need it
    this.mainMenuBar.setWorkspace(this);
    this.dropPane.setWorkspace(this);
    this.imagePane = new ImagePaneController();
  }

  // Thanks: https://stackoverflow.com/questions/25397742/javafx-keyboard-event-shortcut-key
  private EventHandler<KeyEvent> undoHandler() {
    return keyEvent -> {
      if (UNDO_KEYS.match(keyEvent)) {
        if (imageFile.undo()) {
          renderImage();
        }
        keyEvent.consume(); // Stop passing the event to next node
      }
    };
  }

  private EventHandler<KeyEvent> redoHandler() {
    return keyEvent -> {
      if (REDO_KEYS.match(keyEvent)) {
        if (imageFile.redo()) {
          renderImage();
        }
        keyEvent.consume(); // Stop passing the event to next node
      }
    };
  }

  private EventHandler<KeyEvent> duplicateHandler() {
    return keyEvent -> {
      if (DUPLICATE_KEYS.match(keyEvent)) {
        duplicate();
        keyEvent.consume(); // Stop passing the event to next node
      }
    };
  }

  public static Workspace newInstance() {
    // Load the fxml containing the workspace layout
    final FXMLLoader loader = new FXMLLoader(ResourceLoader.INSTANCE.load(WORKSPACE_LAYOUT_PATH));

    final Parent root;
    try {
      root = loader.load();
    } catch (final IOException e) {
      LOGGER.error("Could not find the workspace layout resource for the new workspace", e);
      return null;
    }

    // Create the stage of this workspace
    final Stage stage = new Stage();
    stage.setScene(new Scene(root));

    // Start the workspace with the new stage
    final Workspace workspace = loader.getController();
    workspace.start(stage);

    return workspace;
  }

  private static Workspace newInstance(final ImageFile imageFile) {
    final Workspace workspace = newInstance();
    if (workspace == null) return null;
    workspace.loadImage(imageFile);
    return workspace;
  }

  public void loadImages(final List<File> files) {
    final Iterator<ImageFile> imageFiles = filesToImageFiles(files).iterator();

    if (!imageFiles.hasNext()) return;

    // Assign the first image to this workspace and create one workspace for each remaining image
    loadImage(imageFiles.next());
    imageFiles.forEachRemaining(Workspace::newInstance);
  }

  private static List<ImageFile> filesToImageFiles(final List<File> files) {
    return ImageLoader.load(files);
  }

  private void loadImage(final ImageFile imageFile) {
    // Load the new image
    this.imageFile = imageFile;
    renderImage();
    // Set the image pane in the stage's main pane
    this.mainPane.setCenter(imagePane);
    updateWorkspaceTitle();
  }

  public Stage getStage() {
    return stage;
  }

  public void unloadImage() {
    this.imageFile = null;
    this.imagePane.unloadImage();
    this.mainPane.setCenter(dropPane);
    updateWorkspaceTitle();
  }

  public void save() {
    if (imageFile == null) return;

    try {
      imageFile.save();
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }
  }

  public void saveAs(final File file) {
    if (imageFile == null) return;

    try {
      imageFile.saveAs(file);
      updateWorkspaceTitle();
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }
  }

  public File getImageFilePath() {
    return imageFile == null ? null : imageFile.getFile();
  }

  // Change the workspace title to the image name
  private void updateWorkspaceTitle() {
    stage.setTitle(imageFile == null ? DEFAULT_WORKSPACE_NAME : imageFile.getFile().getName());
  }

  public boolean isImageLoaded() {
    return imageFile != null;
  }

  /**
   *
   * @return The image file; null if not present.
   * @deprecated in favour of {@link Workspace#getOpImageFile}
   */
  @Deprecated
  public ImageFile getImageFile() {
    return imageFile;
  }

  public Optional<ImageFile> getOpImageFile() {
    if (!isImageLoaded()) LOGGER.warn("No image loaded on workspace.");
    return Optional.ofNullable(imageFile);
  }

  public ImageFile applyToImage(final DoubleArray2DUnaryOperator function) {
    imageFile.apply(function);
    renderImage();
    return imageFile;
  }

  public ImageFile applyToImage(final UniquePixelsBandOperator function) {
    imageFile.apply(function);
    renderImage();
    return imageFile;
  }

  public ImageFile applyToImage(final ColorOverUniquePixelsBandOperator function) {
    imageFile.apply(function);
    renderImage();
    return imageFile;
  }

  // TODO: Think if there is other way to do this
  public void updateImage(final ImageMatrix imageMatrix) {
    imageFile.setImageMatrix(imageMatrix);
    renderImage();
  }

  // TODO: try to set a handler that, when the image file matrix is updated, the workspace re-renders the image
  private void renderImage() {
    this.imagePane.loadImage(toFXImage(imageFile.getImageMatrix().toBufferedImagePlot(), null));
  }

  public void duplicate() {
    getOpImageFile().ifPresent(imageFile -> Workspace.newInstance(imageFile.duplicate()));
  }

  public void openOriginalInNewWorkspace() {
    getOpImageFile().ifPresent(theImageFile -> filesToImageFiles(Collections.singletonList(theImageFile.getOriginalFile())).forEach(Workspace::newInstance));
  }

  public Optional<Rectangle> getSelectedArea() {
    return imagePane.getSelectedArea();
  }
}

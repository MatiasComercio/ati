package ar.edu.itba.ati.idp.ui.controller;

import static javafx.embed.swing.SwingFXUtils.toFXImage;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.io.ImageLoader;
import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.controller.menu.MenuBarController;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Workspace {
  private static final Logger LOGGER = LoggerFactory.getLogger(Workspace.class);
  private static final String WORKSPACE_LAYOUT_PATH = "ui/workspace.fxml";
  private static final String DEFAULT_WORKSPACE_NAME = "ImgEditor";
  private static final double MIN_STAGE_WIDTH = 400;
  private static final double MIN_STAGE_HEIGHT = 300;

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

    // Make the stage visible
    this.stage.show();

    // Load remaining elements & Configure oneself as the workspace for all elements that need it
    this.mainMenuBar.setWorkspace(this);
    this.dropPane.setWorkspace(this);
    this.imagePane = new ImagePaneController();
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

  public ImageFile getImageFile() {
    return imageFile;
  }

  public ImageFile applyToImage(final DoubleArray2DUnaryOperator function) {
    imageFile.apply(function);
    renderImage();
    return imageFile;
  }

  private void renderImage() {
    this.imagePane.loadImage(toFXImage(imageFile.getImageMatrix().toBufferedImage(), null));
  }
}

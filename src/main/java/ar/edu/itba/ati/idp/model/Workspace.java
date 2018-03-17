package ar.edu.itba.ati.idp.model;

import static javafx.embed.swing.SwingFXUtils.toFXImage;

import ar.edu.itba.ati.idp.ui.DropPane;
import ar.edu.itba.ati.idp.ui.MainMenuBar;
import ar.edu.itba.ati.idp.ui.SelectableAreaImageView;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Unificar los FileChooser
public class Workspace {
  private static final Logger LOGGER = LoggerFactory.getLogger(Workspace.class);
  private static final String EMPTY_WORKSPACE_IMAGE = "ui/icons/image_icon.png";
  private ImageFile imageFile;

  private static final String DEFAULT_WORKSPACE_NAME = "ImgEditor";

  private Stage stage;
  private MenuBar mainMenuBar;
  private BorderPane mainPane;
  private Pane noImagePane;
  private ScrollPane imagePane;
  private ImageView imageView;
  private SelectableAreaImageView selectableAreaImageView;


  private Workspace(final ImageFile imageFile) {
    // TODO: Restrict UI min size to window components
    this.stage = new Stage();
    this.mainMenuBar = buildMenuBar(stage);

    this.mainPane = new BorderPane();
    this.mainPane.setTop(this.mainMenuBar);

    this.noImagePane = buildNoImagePane();
    this.setNoImagePane();

    this.imageView = new ImageView();

    this.selectableAreaImageView = new SelectableAreaImageView(imageView);

    this.imagePane = new ScrollPane(new BorderPane(this.selectableAreaImageView));
    this.imagePane.setFitToWidth(true);
    this.imagePane.setFitToHeight(true);

    this.stage.setTitle(DEFAULT_WORKSPACE_NAME);
    this.stage.setOnCloseRequest(event -> System.exit(0));
    this.stage.centerOnScreen();
    this.stage.setScene(new Scene(this.mainPane));
    this.stage.show();

    if (imageFile != null) {
      setImageFile(imageFile);
    }

    // TODO: handle not saved changes & ask for exit confirmation
    // This is to avoid closing all workspaces when one is closed
    this.stage.setOnCloseRequest(we -> {});
  }

  public static void newInstance() {
    new Workspace(null);
  }

  private static void newInstance(final ImageFile imageFile) {
    new Workspace(imageFile);
  }


  private MenuBar buildMenuBar(final Stage primaryStage) {
    final MainMenuBar mainMenuBar = new MainMenuBar(primaryStage);
    mainMenuBar.setOnOpenAction(this::handleOpenFiles);
    mainMenuBar.setOnSaveAction(this::handleSaveFile);
    mainMenuBar.setOnSaveAsAction(this::handleSaveAsFile);
    mainMenuBar.setOnCloseAction(this::handleCloseFile);

    return mainMenuBar;
  }

  private Pane buildNoImagePane() {
    final Image image = new Image(getEmptyWorkspaceImageUrl());

    final DropPane dropPane = new DropPane(stage, image, "Drag image here…", "Open image");
    dropPane.setOnOpenAction(this::handleOpenFiles);

    return dropPane;
  }

  private String getEmptyWorkspaceImageUrl() {
    final ClassLoader classLoader = getClass().getClassLoader();
    return Objects.requireNonNull(classLoader.getResource(EMPTY_WORKSPACE_IMAGE)).toString();
  }

  private void setNoImagePane() {
    mainPane.setCenter(noImagePane);
  }

  private void handleSaveAsFile(final File newFile) {
    try {
      imageFile.saveToFile(newFile);
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }
  }

  private void handleSaveFile() {
    try {
      imageFile.saveToFile();
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }
  }

  private void handleCloseFile() {
    setNoImagePane();
  }

  private void handleOpenFiles(final List<File> files) {
    final Iterator<ImageFile> imageFiles = internalHandleOpenFiles(files).iterator();

    if (!imageFiles.hasNext()) return;

    // Assign the first image to this workspace and create one workspace for each remaining image
    setImageFile(imageFiles.next());
    imageFiles.forEachRemaining(Workspace::newInstance);
  }

  public static List<ImageFile> internalHandleOpenFiles(final List<File> files) {
    List<ImageFile> imageFiles = Collections.emptyList();
    try {
      imageFiles = ImageFiles.load(files);
    } catch (final IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }

    return imageFiles;
  }

  public void setImageFile(final ImageFile imageFile) {
    this.imageFile = imageFile;
    this.imageView.setImage(toFXImage(imageFile.getImageMatrix().toBufferedImage(), null));
    this.mainPane.setCenter(imagePane);
    this.selectableAreaImageView.enableSelection();
  }
}

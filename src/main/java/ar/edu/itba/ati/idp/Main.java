package ar.edu.itba.ati.idp;

import static javafx.embed.swing.SwingFXUtils.toFXImage;

import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.model.ImageMatrixIO;
import ar.edu.itba.ati.idp.ui.DropPane;
import ar.edu.itba.ati.idp.ui.MainMenuBar;
import ar.edu.itba.ati.idp.ui.SelectableAreaImageView;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
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
public class Main extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final String APPLICATION_NAME = "imgEditor";

  private Stage mainStage;
  private MenuBar mainMenuBar;
  private BorderPane mainPane;
  private Pane noImagePane;
  private ScrollPane imagePane;
  private ImageView imageView;
  private SelectableAreaImageView selectableAreaImageView;

  private File currentFile;
  private ImageMatrix currentImage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage mainStage) {
    this.mainMenuBar = buildMenuBar(mainStage);

    this.mainPane = new BorderPane();
    this.mainPane.setTop(this.mainMenuBar);

    this.noImagePane = buildNoImagePane();
    this.setNoImagePane();

    this.imageView = new ImageView();

    this.selectableAreaImageView = new SelectableAreaImageView(imageView);

    this.imagePane = new ScrollPane(new BorderPane(this.selectableAreaImageView));
    this.imagePane.setFitToWidth(true);
    this.imagePane.setFitToHeight(true);

    // TODO: Restrict UI min size to window components

    this.mainStage = mainStage;
    this.mainStage.setTitle(APPLICATION_NAME);
    this.mainStage.setOnCloseRequest(event -> System.exit(0));
    this.mainStage.centerOnScreen();
    this.mainStage.setScene(new Scene(this.mainPane));
    this.mainStage.show();
  }

  private void handleOpenFiles(final List<File> files) {
    final ImageMatrix imageMatrix;
    try {
      imageMatrix = ImageMatrixIO.read(files);
    } catch (final IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
      return;
    }

    this.selectableAreaImageView.enableSelection();
    currentFile = files.get(0); // FIXME: hardcoded file
    currentImage = imageMatrix;
    imageView.setImage(toFXImage(currentImage.toBufferedImage(), null));
    mainPane.setCenter(imagePane);
  }

  private void handleSaveAsFile(final File file) {
    try {
      ImageMatrixIO.write(currentImage, file);
      currentFile = file;
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }
  }

  private void handleSaveFile() {
    try {
      ImageMatrixIO.write(currentImage, currentFile); // FIXME: raw image cases
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }
  }

  private void handleCloseFile() {
    setNoImagePane();
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
    final Image image = new Image("file:src/main/resources/ui/icons/image_icon.png");

    final DropPane dropPane = new DropPane(mainStage, image, "Drag image here…", "Open image");
    dropPane.setOnOpenAction(this::handleOpenFiles);

    return dropPane;
  }

  private void setNoImagePane() {
    mainPane.setCenter(noImagePane);
  }
}

package ar.edu.itba.ati.idp;

import static javafx.embed.swing.SwingFXUtils.toFXImage;

import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.model.ImageMatrixIO;
import ar.edu.itba.ati.idp.ui.DropPane;
import ar.edu.itba.ati.idp.ui.MainMenuBar;
import java.io.File;
import java.io.IOException;
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

public class Main extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final String APPLICATION_NAME = "imgEditor";

  private Stage mainStage;
  private MenuBar mainMenuBar;
  private BorderPane mainPane;
  private Pane noImagePane;
  private ScrollPane imagePane;
  private ImageView imageView;

  private File currentFile;
  private ImageMatrix currentImage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage mainStage) {
    this.mainStage = mainStage;
    this.mainMenuBar = buildMenuBar(mainStage);
    this.mainPane = new BorderPane();
    this.noImagePane = buildNoImagePane();
    this.imageView = new ImageView();
    this.imagePane = new ScrollPane(new BorderPane(this.imageView));

    this.imagePane.setFitToWidth(true);
    this.imagePane.setFitToHeight(true);

    this.mainPane.setTop(this.mainMenuBar);
    this.setNoImagePane();
    // TODO: Restrict UI min size to window components

    this.mainStage.setTitle(APPLICATION_NAME);
    this.mainStage.setOnCloseRequest(event -> System.exit(0));
    this.mainStage.centerOnScreen();
    this.mainStage.setScene(new Scene(this.mainPane));
    this.mainStage.show();
  }

  private void handleOpenFile(final File file) {
    final ImageMatrix imageMatrix;
    try {
      imageMatrix = ImageMatrixIO.read(file);
    } catch (final IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
      return;
    }

    // TODO: Remove, para testeo unicamente
    for (int y = 0; y < imageMatrix.getHeight(); y++) {
      for (int x = 0; x < imageMatrix.getWidth(); x++) {
        imageMatrix.setPixel(x, y, new double[]{0, 0, 0});
      }
    }

    currentFile = file;
    currentImage = imageMatrix;
    imageView.setImage(toFXImage(imageMatrix.toBufferedImage(), null));
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
      ImageMatrixIO.write(currentImage, currentFile);
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage(), exception);
    }
  }

  private void handleCloseFile() {
    setNoImagePane();
  }

  private MenuBar buildMenuBar(final Stage primaryStage) {
    final MainMenuBar mainMenuBar = new MainMenuBar(primaryStage);
    mainMenuBar.setOnOpenAction(this::handleOpenFile);
    mainMenuBar.setOnSaveAction(this::handleSaveFile);
    mainMenuBar.setOnSaveAsAction(this::handleSaveAsFile);
    mainMenuBar.setOnCloseAction(this::handleCloseFile);

    return mainMenuBar;
  }

  private Pane buildNoImagePane() {
    final Image image = new Image("file:src/main/resources/ui/icons/image_icon.png");

    final DropPane dropPane = new DropPane(mainStage, image, "Drag image here…", "Open image");
    dropPane.setOnOpenAction(this::handleOpenFile);

    return dropPane;
  }

  private void setNoImagePane() {
    mainPane.setCenter(noImagePane);
  }
}

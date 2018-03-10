package ar.edu.itba.ati.idp;

import ar.edu.itba.ati.idp.ui.DropPane;
import ar.edu.itba.ati.idp.ui.MainMenuBar;
import java.io.File;
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
  private Pane noImagePane;
  private BorderPane mainPane;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage mainStage) {
    this.mainStage = mainStage;
    this.mainMenuBar = buildMenuBar(mainStage);
    this.noImagePane = buildNoImagePane();
    this.mainPane = new BorderPane();

    this.mainPane.setTop(this.mainMenuBar);
    this.setNoImagePane();
    // TODO:
//    mainPane.centerProperty().addListener((observable, oldValue, newValue) -> {
//      final Region region = (Region) newValue;
//
//      if (region.getMinWidth() > 0) {
//        mainStage.setMinWidth(region.getMinWidth());
//      }
//
//      if (region.getMinHeight() > 0) {
//        mainStage.setMinHeight(region.getMinHeight() + 22);
//      }
//    });

    this.mainStage.setTitle(APPLICATION_NAME);
    this.mainStage.setOnCloseRequest(event -> System.exit(0));
    this.mainStage.centerOnScreen();
    this.mainStage.setScene(new Scene(this.mainPane));
    this.mainStage.show();
  }

  // FIXME: Para testeo unicamente
  private void handleOpenFile(final File file) {
    final Image image = new Image(file.toURI().toString());
    final ImageView imageView = new ImageView(image);
    final ScrollPane scrollPane = new ScrollPane(imageView);
    mainPane.setCenter(scrollPane);
  }

  private void handleSaveAsFile(final File file) {

  }

  private void handleSaveFile() {

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
    // TODO: Change to a better & local image
    final Image image = new Image(
        "https://cdn3.iconfinder.com/data/icons/faticons/32/picture-01-256.png", 50, 50, true,
        true);

    final DropPane dropPane = new DropPane(mainStage, image, "Drag image here…", "Open image");
    dropPane.setOnOpenAction(this::handleOpenFile);

    return dropPane;
  }

  private void setNoImagePane() {
    mainPane.setCenter(noImagePane);
  }
}

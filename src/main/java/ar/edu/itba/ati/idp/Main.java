package ar.edu.itba.ati.idp;

import static javafx.geometry.Pos.CENTER;
import static javafx.scene.layout.BorderStrokeStyle.DASHED;
import static javafx.scene.paint.Color.LIGHTGRAY;

import ar.edu.itba.ati.idp.ui.MainMenuBar;
import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final String APPLICATION_NAME = "Imageditor";

  private Stage mainStage;
  private MenuBar mainMenuBar;
  private Pane noImagePane;
  private BorderPane mainPane;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) {
    this.mainStage = primaryStage;
    this.mainMenuBar = buildMenuBar(primaryStage);
    this.noImagePane = buildNoImagePane();
    this.mainPane = new BorderPane();

    this.mainPane.setTop(this.mainMenuBar);
    this.mainPane.setCenter(this.noImagePane);

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

    final StringProperty imageFileNameProperty = new SimpleStringProperty();
    final BooleanProperty imageModifiedProperty = new SimpleBooleanProperty(false);
  }

  private void handleSaveAsFile(final File file) {

  }

  private void handleSaveFile() {

  }

  private void handleCloseFile() {
    mainPane.setCenter(noImagePane);
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
    final Text dragImageText = new Text("Drag and drop a supported image");
    dragImageText.setTextAlignment(TextAlignment.CENTER);
    final StackPane dragAndDropPane = new StackPane(dragImageText);
    dragAndDropPane.setPadding(new Insets(15, 15, 15, 15));
    dragAndDropPane.setBorder(
        new Border(new BorderStroke(LIGHTGRAY, DASHED, new CornerRadii(3), new BorderWidths(3))));
    dragAndDropPane.setAlignment(CENTER);
    dragAndDropPane.setOnDragOver(event -> {
      final Dragboard dragboard = event.getDragboard();
      if (dragboard.hasFiles()) {
        event.acceptTransferModes(TransferMode.ANY);
        dragAndDropPane.setBackground(
            new Background(
                new BackgroundFill(LIGHTGRAY, new CornerRadii(3), new Insets(6, 6, 6, 6))));
      }
    });
    dragAndDropPane.setOnDragDropped(event -> {
      final Dragboard dragboard = event.getDragboard();
      if (dragboard.hasFiles()) {
        final List<File> files = dragboard.getFiles();
        this.handleOpenFile(files.get(0));
      }
    });
    dragAndDropPane.setOnDragExited(event -> dragAndDropPane.setBackground(Background.EMPTY));

    final StackPane borderPane = new StackPane(dragAndDropPane);
    borderPane.setPadding(new Insets(10, 10, 10, 10));
    borderPane.setPrefSize(300, 200);

    return borderPane;
  }
}

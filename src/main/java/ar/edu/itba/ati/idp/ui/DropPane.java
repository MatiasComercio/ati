package ar.edu.itba.ati.idp.ui;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DropPane extends BorderPane {

  private final Stage mainStage;

  private final VBox content;
  private final Image image;
  private final Text text;
  private final Button openButton;

  private final FileChooser openFileChooser;

  public DropPane(final Stage mainStage, final Image image, final String dropTextString,
      final String openTextString) {
    this.mainStage = mainStage;
    this.content = new VBox();
    this.text = new Text(dropTextString);
    this.image = image;
    this.openButton = new Button(openTextString);
    this.openFileChooser = buildOpenFileChooser(openTextString);

    this.content.setPadding(new Insets(50, 70, 50, 70));
    this.content.setBorder(
        new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.DASHED, new CornerRadii(3),
            new BorderWidths(3))));
    // TODO: Remove
//    this.content.setBackground(new Background(
//        new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
//            BackgroundPosition.CENTER, new BackgroundSize(50, 50, false, false, false, false))));
    this.content.setAlignment(Pos.CENTER);
    this.content.setSpacing(15);
    this.content.getChildren().addAll(new ImageView(this.image), this.text, this.openButton);

    this.text.setTextAlignment(TextAlignment.CENTER);
    this.text.setFont(Font.font(16));
    this.text.setFill(Color.GRAY);

    this.setPadding(new Insets(10, 10, 10, 10));
    this.setCenter(content);
    this.configureDrag();
  }

  public void setOnOpenAction(final Consumer<File> openHandler) {
    openButton.setOnAction(event -> {
      final File file = openFileChooser.showOpenDialog(mainStage);
      if (file != null) {
        openHandler.accept(file);
      }
    });

    setOnDragDropped(event -> {
      final Dragboard dragboard = event.getDragboard();
      if (dragboard.hasFiles()) {
        final List<File> files = dragboard.getFiles();
        openHandler.accept(files.get(0));
      }
    });
  }

  private void configureDrag() {
    setOnDragOver(event -> {
      final Dragboard dragboard = event.getDragboard();
      if (dragboard.hasFiles()) {
        event.acceptTransferModes(TransferMode.ANY);
        content.setBackground(
            new Background(
                new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(3), new Insets(8, 8, 8, 8))));
      }
    });

    setOnDragExited(event -> content.setBackground(Background.EMPTY));
  }

  private static FileChooser buildOpenFileChooser(final String openTextString) {
    final FileChooser openFileChooser = new FileChooser();
    openFileChooser.setTitle(openTextString + "…");

    return openFileChooser;
  }
}

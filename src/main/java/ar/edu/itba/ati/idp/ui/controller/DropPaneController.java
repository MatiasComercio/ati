package ar.edu.itba.ati.idp.ui.controller;

import ar.edu.itba.ati.idp.ui.OpenFileChooser;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DropPaneController extends VBox {
  private static final String LAYOUT_PATH = "ui/dropPane.fxml";

  private Workspace workspace;
  private OpenFileChooser openFileChooser;

  public DropPaneController() {
    this.openFileChooser = new OpenFileChooser();

    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
  }

  @FXML
  public void handleOpenImagesButton() {
    openFileChooser.chooseToLoadInWorkspace(workspace);
  }

  @FXML
  public void handleDragDropped(final DragEvent event) {
    final Dragboard dragboard = event.getDragboard();
    if (dragboard.hasFiles()) {
      workspace.loadImages(dragboard.getFiles());
    }
  }

  @FXML
  public void handleDragOver(final DragEvent event) {
    final Dragboard dragboard = event.getDragboard();
    if (dragboard.hasFiles()) {
      event.acceptTransferModes(TransferMode.ANY);

      this.setBackground(new Background(new BackgroundFill(
                  Color.LIGHTGRAY,
                  new CornerRadii(3),
                  new Insets(8, 8, 8, 8)
      )));
    }
  }

  @FXML
  public void handleDragExited() {
    this.setBackground(Background.EMPTY);
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
  }
}

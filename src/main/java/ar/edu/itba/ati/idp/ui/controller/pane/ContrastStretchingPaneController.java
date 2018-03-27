package ar.edu.itba.ati.idp.ui.controller.pane;

import ar.edu.itba.ati.idp.function.point.ContrastStretching;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ContrastStretchingPaneController extends VBox {

  private static final String LAYOUT_PATH = "ui/pane/contrastStretchingPane.fxml";
  private static final String STAGE_TITLE = "Contrast Stretching";

  private final Stage stage;

  @FXML
  private TextField r1TextField;

  @FXML
  private TextField r2TextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public ContrastStretchingPaneController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    r1TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue, r2TextField.getText())) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });
    r2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(r1TextField.getText(), newValue)) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });

    this.stage = new Stage(StageStyle.UTILITY);
    this.stage.setScene(new Scene(this));
    this.stage.setResizable(false);
    this.stage.setOnCloseRequest(event -> this.stage.hide());
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
  }

  public void show() {
    if (workspace.isImageLoaded()) { // TODO: this should be done in the caller of this `show` method
      stage.setTitle(STAGE_TITLE + " - " + workspace.getImageFile().getFile().getName());
      stage.show();
    }
  }

  @FXML
  public void handleApply() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final double r1;
    final double r2;
    try {
      r1 = Double.parseDouble(r1TextField.getText());
      r2 = Double.parseDouble(r2TextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(new ContrastStretching(r1, r2));

    workspace.updateImage(newImageMatrix);
  }

  private static boolean isValidInput(final String r1String, final String r2String) {
    if (r1String == null || r1String.equals("") || r2String == null || r2String.equals("")) {
      return false;
    }

    final double r1;
    final double r2;
    try {
      r1 = Double.parseDouble(r1String);
      r2 = Double.parseDouble(r2String);
    } catch (final NumberFormatException exception) {
      return false;
    }

    if (r1 >= r2 || r1 < 0.0 || r2 < 0.0) { // TODO: Agregar condiciones necesarias
      return false;
    }

    return true;
  }
}

package ar.edu.itba.ati.idp.ui.controller.pane;

import ar.edu.itba.ati.idp.function.point.Threshold;
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

public class ThresholdPaneController extends VBox {

  private static final String LAYOUT_PATH = "ui/pane/thresholdPane.fxml";
  private static final String STAGE_TITLE = "Threshold";

  private final Stage stage;

  @FXML
  private TextField thresholdTextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public ThresholdPaneController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    thresholdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue)) {
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
    if (workspace.isImageLoaded()) {
      stage.setTitle(STAGE_TITLE + " - " + workspace.getImageFile().getFile().getName());
      stage.show();
    }
  }

  @FXML
  public void handleApply() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final int value;
    try {
      value = Integer.parseInt(thresholdTextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(new Threshold(value));

    workspace.updateImage(newImageMatrix);
  }

  private static boolean isValidInput(final String input) {
    if (input == null || input.equals("")) {
      return false;
    }

    final int intValue;
    try {
      intValue = Integer.parseInt(input);
    } catch (final NumberFormatException exception) {
      return false;
    }

    return intValue >= 0 && intValue <= 255;
  }
}

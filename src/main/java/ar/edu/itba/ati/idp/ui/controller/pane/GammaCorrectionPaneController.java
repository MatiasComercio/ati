package ar.edu.itba.ati.idp.ui.controller.pane;

import ar.edu.itba.ati.idp.function.point.GammaCorrection;
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

public class GammaCorrectionPaneController extends VBox {

  private static final String LAYOUT_PATH = "ui/pane/gammaCorrectionPane.fxml";
  private static final String STAGE_TITLE = "Gamma Correction";

  private final Stage stage;

  @FXML
  private TextField gammaTextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public GammaCorrectionPaneController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    gammaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
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

    final double gamma;
    try {
      gamma = Double.parseDouble(gammaTextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(new GammaCorrection(gamma));

    workspace.updateImage(newImageMatrix);
  }

  private static boolean isValidInput(final String input) {
    if (input == null || input.equals("")) {
      return false;
    }

    final double doubleValue;
    try {
      doubleValue = Double.parseDouble(input);
    } catch (final NumberFormatException exception) {
      return false;
    }

    if (doubleValue < 0 || doubleValue > 2) {
      return false;
    }

    return true;
  }
}

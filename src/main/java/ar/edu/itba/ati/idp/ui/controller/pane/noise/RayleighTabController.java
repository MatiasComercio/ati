package ar.edu.itba.ati.idp.ui.controller.pane.noise;

import ar.edu.itba.ati.idp.function.noise.RandomOperation;
import ar.edu.itba.ati.idp.function.noise.RayleighNoise;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.controller.PercentageSliderController;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class RayleighTabController extends Tab {

  private static final String LAYOUT_PATH = "ui/pane/noise/rayleighTab.fxml";

  @FXML
  private PercentageSliderController percentageSlider;

  @FXML
  private TextField valueTextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public RayleighTabController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    valueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue)) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  public void handleApply() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final double value;
    try {
      value = Double.parseDouble(valueTextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(
        new RandomOperation(percentageSlider.getPercentageAsDouble(), imageMatrix.getWidth(),
            imageMatrix.getHeight(), new RayleighNoise(value)));

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

    if (doubleValue < 0.0) { // TODO: Agregar condiciones necesarias
      return false;
    }

    return true;
  }
}

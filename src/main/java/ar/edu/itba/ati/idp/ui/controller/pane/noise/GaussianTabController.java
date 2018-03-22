package ar.edu.itba.ati.idp.ui.controller.pane.noise;

import ar.edu.itba.ati.idp.function.noise.GaussianNoise;
import ar.edu.itba.ati.idp.function.noise.RandomOperation;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.controller.PercentageSliderController;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class GaussianTabController extends Tab {

  private static final String LAYOUT_PATH = "ui/pane/noise/gaussianTab.fxml";

  @FXML
  private PercentageSliderController percentageSlider;

  @FXML
  private TextField meanTextField;

  @FXML
  private TextField sdTextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public GaussianTabController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    meanTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue) && isValidInput(sdTextField.getText())) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });
    sdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue) && isValidInput(meanTextField.getText())) {
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

    final double mean;
    final double sd;
    try {
      mean = Double.parseDouble(meanTextField.getText());
      sd = Double.parseDouble(sdTextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(
        new RandomOperation(percentageSlider.getPercentageAsDouble(), imageMatrix.getWidth(),
            imageMatrix.getHeight(), new GaussianNoise(mean, sd)));

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

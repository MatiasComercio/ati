package ar.edu.itba.ati.idp.ui.controller.pane.noise;

import ar.edu.itba.ati.idp.function.noise.ExponentialNoise;
import ar.edu.itba.ati.idp.function.noise.RandomOperation;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.controller.PercentageSliderController;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class ExponentialTabController extends Tab {

  private static final String LAYOUT_PATH = "ui/pane/noise/exponentialTab.fxml";

  @FXML
  private PercentageSliderController percentageSlider;

  @FXML
  private TextField meanTextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public ExponentialTabController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    meanTextField.textProperty().addListener((observable, oldValue, newValue) -> {
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

    final double mean;
    try {
      mean = Double.parseDouble(meanTextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(
        new RandomOperation(percentageSlider.getPercentageAsDouble(), imageMatrix.getWidth(),
            imageMatrix.getHeight(), new ExponentialNoise(mean)));

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

package ar.edu.itba.ati.idp.ui.controller.pane.noise;

import ar.edu.itba.ati.idp.function.noise.RandomOperation;
import ar.edu.itba.ati.idp.function.noise.SaltAndPepperNoise;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.controller.PercentageSliderController;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class SaltAndPepperTabController extends Tab {

  private static final String LAYOUT_PATH = "ui/pane/noise/saltAndPepperTab.fxml";

  @FXML
  private PercentageSliderController percentageSlider;

  @FXML
  private TextField p0TextField;

  @FXML
  private TextField p1TextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public SaltAndPepperTabController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    p0TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue) && isValidInput(p1TextField.getText())) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });
    p1TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue) && isValidInput(p0TextField.getText())) {
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

    final double p0;
    final double p1;
    try {
      p0 = Double.parseDouble(p0TextField.getText());
      p1 = Double.parseDouble(p1TextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(
        new RandomOperation(percentageSlider.getPercentageAsDouble(), imageMatrix.getWidth(),
            imageMatrix.getHeight(), new SaltAndPepperNoise(p0, p1)));

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

    if (doubleValue < 0.0 || doubleValue > 1.0) { // TODO: Agregar condiciones necesarias
      return false;
    }

    return true;
  }
}

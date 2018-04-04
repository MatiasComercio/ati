package ar.edu.itba.ati.idp.ui.controller.pane;

import ar.edu.itba.ati.idp.function.point.ContrastStretching;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.model.ImageMatrix.Band;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContrastStretchingPaneController extends VBox {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContrastStretchingPaneController.class);

  private static final String LAYOUT_PATH = "ui/pane/contrastStretchingPane.fxml";
  private static final String STAGE_TITLE = "Contrast Stretching";

  private final Stage stage;

  @FXML
  private TextField r1TextField;

  @FXML
  private TextField r2TextField;

  @FXML
  private TextField s1TextField;

  @FXML
  private TextField s2TextField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public ContrastStretchingPaneController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    r1TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(newValue, r2TextField.getText(), s1TextField.getText(), s2TextField.getText())) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });
    r2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(r1TextField.getText(), newValue, s1TextField.getText(), s2TextField.getText())) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });
    s1TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(r1TextField.getText(), r2TextField.getText(), newValue, s2TextField.getText())) {
        applyButton.setDisable(false);
      } else {
        applyButton.setDisable(true);
      }
    });
    s2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (isValidInput(r1TextField.getText(), r2TextField.getText(), s1TextField.getText(), newValue)) {
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
    stage.setTitle(STAGE_TITLE + " - " + workspace.getImageFile().getFile().getName());
    populateRs(workspace.getImageFile().getImageMatrix());
    stage.show();
  }

  private void populateRs(final ImageMatrix imageMatrix) {
    final double[] rs = calculateContrastRs(imageMatrix);
    r1TextField.setText(String.valueOf(rs[0]));
    r2TextField.setText(String.valueOf(rs[1]));
  }

  // TODO: implement as a function outside UI
  private double[] calculateContrastRs(final ImageMatrix imageMatrix) {
    final double[] rs = new double[2];
    final Map<Band, Double> avgPixelPerBand =
        imageMatrix.getAveragePixelPerBand(0, 0, imageMatrix.getWidth(), imageMatrix.getHeight());
    // This method works only for grey scale images
    final Double avgPixel = avgPixelPerBand.get(Band.GREY);
    if (avgPixel == null) {
      LOGGER.warn("This method should be applied to grey images only...");
      return rs;
    }

    final Map<Band, Double> standardDeviationPerBand =
        imageMatrix.getStandardDeviationPerBand(0, 0, imageMatrix.getWidth(), imageMatrix.getHeight(), avgPixelPerBand);

    final Double std = standardDeviationPerBand.get(Band.GREY);
    if (std == null) {
      LOGGER.warn("This method should be applied to grey images only...");
      return rs;
    }

    final double r1;
    if (std >= avgPixel) {
      r1 = avgPixel / 2;
    } else {
      r1 = avgPixel - std;
    }

    final int maxPixelValue = ImageMatrix.getMaxNormalizedPixelValue();
    final double r2;
    if (avgPixel + std > maxPixelValue) {
      r2 = avgPixel + (maxPixelValue - avgPixel) / 2d;
    } else {
      r2 = avgPixel + std;
    }

    rs[0] = r1;
    rs[1] = r2;
    return rs;
  }


  @FXML
  public void handleApply() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final double r1;
    final double r2;
    final double s1;
    final double s2;
    try {
      r1 = Double.parseDouble(r1TextField.getText());
      r2 = Double.parseDouble(r2TextField.getText());
      s1 = Double.parseDouble(s1TextField.getText());
      s2 = Double.parseDouble(s2TextField.getText());
    } catch (final NumberFormatException exception) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(new ContrastStretching(r1, r2, s1, s2));

    workspace.updateImage(newImageMatrix);
  }

  private static boolean isValidInput(final String r1String, final String r2String,
                                      final String s1String, final String s2String) {
    if (r1String == null || r1String.equals("") || r2String == null || r2String.equals("")
        || s1String == null || s1String.equals("") || s2String == null || s2String.equals("")) {
      return false;
    }

    final double r1;
    final double r2;
    final double s1;
    final double s2;
    try {
      r1 = Double.parseDouble(r1String);
      r2 = Double.parseDouble(r2String);
      s1 = Double.parseDouble(s1String);
      s2 = Double.parseDouble(s2String);
    } catch (final NumberFormatException exception) {
      return false;
    }

    final int maxVal = ImageMatrix.getMaxNormalizedPixelValue();
    if (r1 < 0 || r2 < 0 || s1 < 0 || s2 < 0 ||
        r1 > maxVal || r2 > maxVal || s1 > maxVal || s2 > maxVal
        || r1 > r2 || s1 > s2 || s1 > r1 || r2 > s2) {
      return false;
    }

    return true;
  }
}

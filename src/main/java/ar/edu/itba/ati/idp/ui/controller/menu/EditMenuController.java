package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.function.Compressor;
import ar.edu.itba.ati.idp.function.Normalizer;
import ar.edu.itba.ati.idp.function.point.DynamicRangeCompression;
import ar.edu.itba.ati.idp.function.point.Negative;
import ar.edu.itba.ati.idp.io.ImageLoader;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.OpenFileChooser;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.ui.controller.pane.ContrastStretchingPaneController;
import ar.edu.itba.ati.idp.ui.controller.pane.GammaCorrectionPaneController;
import ar.edu.itba.ati.idp.ui.controller.pane.ScalarMultiplicationPaneController;
import ar.edu.itba.ati.idp.ui.controller.pane.ThresholdPaneController;
import ar.edu.itba.ati.idp.ui.controller.pane.noise.NoiseTabPaneController;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class EditMenuController extends Menu {

  private static final String LAYOUT_PATH = "ui/menu/edit/editMenu.fxml";

  private final OpenFileChooser openFileChooser;
  private final NoiseTabPaneController noiseTabPaneController;
  private final ScalarMultiplicationPaneController scalarMultiplicationPaneController;
  private final ThresholdPaneController thresholdPaneController;
  private final GammaCorrectionPaneController gammaCorrectionPaneController;
  private final ContrastStretchingPaneController contrastStretchingPaneController;
  private final Negative negativeFunction;
  private final DynamicRangeCompression dynamicRangeCompressionFunction;

  @FXML
  private HistogramMenuController histogramMenu;

  @FXML
  private FilterMenuController filterMenu;

  private Workspace workspace;

  public EditMenuController() {
    this.openFileChooser = new OpenFileChooser();
    this.noiseTabPaneController = new NoiseTabPaneController();
    this.scalarMultiplicationPaneController = new ScalarMultiplicationPaneController();
    this.thresholdPaneController = new ThresholdPaneController();
    this.gammaCorrectionPaneController = new GammaCorrectionPaneController();
    this.contrastStretchingPaneController = new ContrastStretchingPaneController();
    this.negativeFunction = new Negative();
    this.dynamicRangeCompressionFunction = new DynamicRangeCompression();

    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    this.noiseTabPaneController.setWorkspace(workspace);
    this.scalarMultiplicationPaneController.setWorkspace(workspace);
    this.thresholdPaneController.setWorkspace(workspace);
    this.gammaCorrectionPaneController.setWorkspace(workspace);
    this.contrastStretchingPaneController.setWorkspace(workspace);
    this.histogramMenu.setWorkspace(workspace);
    this.filterMenu.setWorkspace(workspace);
  }

  @FXML
  private void handleNegative() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(negativeFunction);

    workspace.updateImage(newImageMatrix);
  }

  @FXML
  private void handleImageAdd() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final ImageMatrix openedImage = openImage();
    if (openedImage == null) {
      return;
    }

    final ImageMatrix currentImage = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = currentImage.add(openedImage);

    workspace.updateImage(newImageMatrix);
  }

  @FXML
  private void handleImageSubtract() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final ImageMatrix openedImage = openImage();
    if (openedImage == null) {
      return;
    }

    final ImageMatrix currentImage = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = currentImage.subtract(openedImage);

    workspace.updateImage(newImageMatrix);
  }

  @FXML
  private void handleImageMultiply() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final ImageMatrix openedImage = openImage();
    if (openedImage == null) {
      return;
    }

    final ImageMatrix currentImage = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = currentImage.multiply(openedImage);

    workspace.updateImage(newImageMatrix);
  }

  @FXML
  private void handleScalarMultiplication() {
    scalarMultiplicationPaneController.show();
  }

  @FXML
  private void handleDynamicRangeCompression() {
    if (!workspace.isImageLoaded()) {
      return;
    }

    final ImageMatrix imageMatrix = workspace.getImageFile().getImageMatrix();
    final ImageMatrix newImageMatrix = imageMatrix.apply(dynamicRangeCompressionFunction);

    workspace.updateImage(newImageMatrix);
  }

  @FXML
  private void handleGammaCorrection() {
    gammaCorrectionPaneController.show();
  }

  @FXML
  private void handleContrastStretching() {
    if (!workspace.isImageLoaded()) return;
    contrastStretchingPaneController.show();
  }

  @FXML
  private void handleThreshold() {
    thresholdPaneController.show();
  }

  @FXML
  private void handleNoise() {
    noiseTabPaneController.show();
  }

  @FXML
  private void handleNormalize() {
    if (!workspace.isImageLoaded()) {
      return;
    }
    workspace.applyToImage(Normalizer.INSTANCE);
  }

  @FXML
  private void handleCompress() {
    workspace.getOpImageFile().ifPresent(imageFile -> workspace.applyToImage(Compressor.INSTANCE));
  }

  // TODO: improve
  private ImageMatrix openImage() {
    final List<File> files = openFileChooser.chooseToLoadManually(workspace);
    if (files != null && files.size() > 0) {
      return ImageLoader.load(Collections.singletonList(files.get(0))).get(0).getImageMatrix();
    }

    return null;
  }
}

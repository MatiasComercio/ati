package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.function.HistogramEqualization;
import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageHistogram;
import ar.edu.itba.ati.idp.ui.controller.HistogramChartController;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class HistogramMenuController extends Menu {
  private static final String LAYOUT_PATH = "ui/menu/edit/histogramMenu.fxml";

  private Workspace workspace;
  private HistogramChartController histogramChartController;

  public HistogramMenuController() {
    this.histogramChartController = new HistogramChartController();

    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  public void handleShow() {
    if (!workspace.isImageLoaded()) return;
    internalHandleShow(workspace.getImageFile());
  }

  @FXML
  public void handleEqualize() {
    if (!workspace.isImageLoaded()) return;
    workspace.applyToImage(HistogramEqualization.INSTANCE);
  }

  private void internalHandleShow(final ImageFile imageFile) {
    final ImageHistogram imageHistogram = imageFile.getImageMatrix().getHistogram();
    final String imageName = workspace.getImageFile().getFile().getName();
    histogramChartController.show(imageHistogram, imageName);
  }
}

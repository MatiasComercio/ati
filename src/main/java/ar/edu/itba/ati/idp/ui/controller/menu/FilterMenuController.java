package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.ui.controller.pane.filter.GaussFilterController;
import ar.edu.itba.ati.idp.ui.controller.pane.filter.HighPassFilterController;
import ar.edu.itba.ati.idp.ui.controller.pane.filter.MediaFilterController;
import ar.edu.itba.ati.idp.ui.controller.pane.filter.MedianFilterController;
import ar.edu.itba.ati.idp.ui.controller.pane.filter.WeightedMedianFilterController;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class FilterMenuController extends Menu {
  private static final String LAYOUT_PATH = "ui/menu/edit/filterMenu.fxml";

  private Workspace workspace;
  private MediaFilterController mediaFilterController;
  private GaussFilterController gaussFilterController;
  private MedianFilterController medianFilterController;
  private WeightedMedianFilterController weightedMedianFilterController;
  private HighPassFilterController highPassFilterController;

  public FilterMenuController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    this.mediaFilterController = new MediaFilterController();
    this.gaussFilterController = new GaussFilterController();
    this.medianFilterController = new MedianFilterController();
    this.weightedMedianFilterController = new WeightedMedianFilterController();
    this.highPassFilterController = new HighPassFilterController();
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    this.mediaFilterController.setWorkspace(workspace);
    this.gaussFilterController.setWorkspace(workspace);
    this.medianFilterController.setWorkspace(workspace);
    this.weightedMedianFilterController.setWorkspace(workspace);
    this.highPassFilterController.setWorkspace(workspace);
  }

  @FXML
  public void handleMediaFilter() {
    if (!workspace.isImageLoaded()) return;
    mediaFilterController.show(workspace.getImageFile().getFile().getName()); // TODO: workspace.getFileName();
  }

  @FXML
  public void handleGaussFilter() {
    if (!workspace.isImageLoaded()) return;
    gaussFilterController.show(workspace.getImageFile().getFile().getName());
  }

  @FXML
  public void handleMedianFilter() {
    if (!workspace.isImageLoaded()) return;
    medianFilterController.show(workspace.getImageFile().getFile().getName());
  }

  @FXML
  public void handleWeightedMedianFilter() {
    if (!workspace.isImageLoaded()) return;
    weightedMedianFilterController.show(workspace.getImageFile().getFile().getName());
  }

  @FXML
  public void handleHighPassFilter() {
    if (!workspace.isImageLoaded()) return;
    highPassFilterController.show(workspace.getImageFile().getFile().getName());
  }
}

package ar.edu.itba.ati.idp.ui.controller.pane.noise;

import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NoiseTabPaneController extends TabPane {

  private static final String LAYOUT_PATH = "ui/pane/noise/noiseTabPane.fxml";
  private static final String STAGE_TITLE = "Noise Editor";

  private final Stage stage;

  private Workspace workspace;

  @FXML
  private ExponentialTabController exponentialTab;

  @FXML
  private RayleighTabController rayleighTab;

  @FXML
  private GaussianTabController gaussianTab;

  @FXML
  private SaltAndPepperTabController saltAndPepperTab;

  public NoiseTabPaneController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    this.stage = new Stage(StageStyle.UTILITY);
    this.stage.setScene(new Scene(this));
    this.stage.setResizable(false);
    this.stage.setOnCloseRequest(event -> this.stage.hide());
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    this.exponentialTab.setWorkspace(workspace);
    this.rayleighTab.setWorkspace(workspace);
    this.gaussianTab.setWorkspace(workspace);
    this.saltAndPepperTab.setWorkspace(workspace);
  }

  public void show() {
    if (workspace.isImageLoaded()) {
      stage.setTitle(STAGE_TITLE + " - " + workspace.getImageFile().getFile().getName());
      stage.show();
    }
  }
}

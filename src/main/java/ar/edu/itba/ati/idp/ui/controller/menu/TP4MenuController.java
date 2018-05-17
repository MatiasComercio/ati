package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.function.border.HarrisDetector;
import ar.edu.itba.ati.idp.function.filter.CannyFilter;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.ui.controller.pane.tp3.CannyBorderDetectorUI;
import ar.edu.itba.ati.idp.ui.controller.pane.tp4.HarrisDetectorUI;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class TP4MenuController extends Menu {
  private static final String LAYOUT_PATH = "ui/menu/tp4Menu.fxml";

  private final Showable harrisDetectorUI;

  private Workspace workspace;

  @SuppressWarnings("CodeBlock2Expr")
  public TP4MenuController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
    harrisDetectorUI = HarrisDetectorUI.newInstance((workspace, imageFile,
                                                     gaussSigma, gaussSideLength, cimId, k,
                                                     acceptancePercentage) -> {
      workspace.applyToImage(HarrisDetector.newInstance(gaussSigma, gaussSideLength, cimId, k,
                                                        acceptancePercentage));
    });
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    harrisDetectorUI.setWorkspace(workspace);
  }

  @FXML
  public void handleHarrisDetector() {
    show(harrisDetectorUI);
  }

  private void show(final Showable showable) {
    workspace.getOpImageFile().ifPresent(imageFile -> showable.show(imageFile.getFile().getName()));
  }
}

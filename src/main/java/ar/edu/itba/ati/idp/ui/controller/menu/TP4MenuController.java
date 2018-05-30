package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.function.border.HarrisDetector;
import ar.edu.itba.ati.idp.function.border.SiftDetector;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.ui.controller.pane.tp4.HarrisDetectorUI;
import ar.edu.itba.ati.idp.ui.controller.pane.tp4.SiftDetectorUI;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class TP4MenuController extends Menu {
  private static final String LAYOUT_PATH = "ui/menu/tp4Menu.fxml";

  private final Showable harrisDetectorUI;
  private final Showable siftDetectorUI;

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
    siftDetectorUI = SiftDetectorUI.newInstance((workspace, imageFile1, imageFile2,
                                                 matchingDistance, matchingPercentage) -> {
      final Workspace newWorkspace = Workspace.newInstance();
      if (newWorkspace == null) {
        return;
      }
      newWorkspace.loadImages(
          SiftDetector.INSTANCE.apply(imageFile1, imageFile2, matchingDistance, matchingPercentage)
      );
    });
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    harrisDetectorUI.setWorkspace(workspace);
    siftDetectorUI.setWorkspace(workspace);
  }

  @FXML
  public void handleHarrisDetector() {
    show(harrisDetectorUI);
  }

  @FXML
  public void handleSiftDetector() {
    show(siftDetectorUI);
  }

  private void show(final Showable showable) {
    workspace.getOpImageFile().ifPresent(imageFile -> showable.show(imageFile.getFile().getName()));
  }
}

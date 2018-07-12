package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.function.border.GridSiftDetector;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.ui.controller.pane.tpfinal.GridSiftDetectorUI;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import java.io.File;
import java.util.Collections;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class TPFinalMenuController extends Menu {

  private static final String LAYOUT_PATH = "ui/menu/tpFinalMenu.fxml";

  private final Showable gridSiftDetectorUI;

  private Workspace workspace;

  private GridSiftDetector lastGridSiftDetector;
  private String lastTrainImagesFolderPath;
  private int lastGridSiftK;

  public TPFinalMenuController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
    this.gridSiftDetectorUI = GridSiftDetectorUI
        .newInstance((workspace, testImage, trainImagesFolder, k) -> {
          if (trainImagesFolder == null) {
            return;
          }

          final Workspace newWorkspace = Workspace.newInstance();
          if (newWorkspace == null) {
            return;
          }

          if (lastTrainImagesFolderPath == null
              || !lastTrainImagesFolderPath.equals(trainImagesFolder.getPath())
              || k != lastGridSiftK) {
            lastTrainImagesFolderPath = trainImagesFolder.getPath();
            lastGridSiftK = k;
            lastGridSiftDetector = new GridSiftDetector(trainImagesFolder, k);
          }

          final File imageFound = lastGridSiftDetector.apply(testImage);
          newWorkspace.loadImages(Collections.singletonList(imageFound));
          System.out.println("Image found: " + imageFound.getPath());
        });
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    this.gridSiftDetectorUI.setWorkspace(workspace);
  }

  @FXML
  public void handleGridSiftDetector() {
    workspace.getOpImageFile()
        .ifPresent(imageFile -> gridSiftDetectorUI.show(imageFile.getFile().getName()));
  }
}

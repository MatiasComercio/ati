package ar.edu.itba.ati.idp.ui;

import ar.edu.itba.ati.idp.ui.controller.Workspace;
import java.io.File;
import javafx.stage.FileChooser;

public class SaveFileChooser {
  private static final String TITLE = "Save as...";

  private final FileChooser fileChooser;

  public SaveFileChooser() {
    this.fileChooser = new FileChooser();
    this.fileChooser.setTitle(TITLE);
  }

  public void chooseToSaveAsUsingWorkspace(final Workspace workspace) {
    final File imageFilePath = workspace.getImageFilePath();
    if (imageFilePath == null) {
      return;
    }
    this.fileChooser.setInitialDirectory(imageFilePath.getParentFile());
    this.fileChooser.setInitialFileName(imageFilePath.getName());
    final File file = fileChooser.showSaveDialog(workspace.getStage());
    if (file != null) {
      workspace.saveAs(file);
    }
  }
}

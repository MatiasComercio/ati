package ar.edu.itba.ati.idp.ui.controller.pane.tpfinal;

import static ar.edu.itba.ati.idp.ui.component.InputExtractors.getPositiveIntIE;

import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import java.io.File;
import javafx.stage.DirectoryChooser;

public class GridSiftDetectorUI implements Showable {

  private static final String STAGE_TITLE = "Grid-Sift Detector";
  private static final String GRID_SIDE_SIZE = "Grid side size";
  private static final String INT_PROMPT = "1";
  private static final int DEFAULT_GRID_SIDE_SIZE = 10;

  private final DirectoryChooser directoryChooser;
  private final Showable showableUI;

  private Workspace workspace;

  private GridSiftDetectorUI(final GridSiftDetectorApplier applier) {
    final Field<Integer> gridSizeIE = Field
        .newInstance(GRID_SIDE_SIZE, INT_PROMPT, getPositiveIntIE());
    gridSizeIE.setValue(DEFAULT_GRID_SIDE_SIZE);

    this.directoryChooser = new DirectoryChooser();
    this.showableUI = FloatingPane.newInstance(STAGE_TITLE, (lambdaWorkspace, imageFile) ->
        applier.apply(
            lambdaWorkspace,
            imageFile.getFile(), // The image loaded in the workspace
            directoryChooser.showDialog(lambdaWorkspace.getStage()), // Testing images
            gridSizeIE.getValue()
        ), gridSizeIE);
  }

  public static GridSiftDetectorUI newInstance(final GridSiftDetectorApplier applier) {
    return new GridSiftDetectorUI(applier);
  }

  @Override
  public void show(final String imageFileName) {
    if (workspace == null) {
      return;
    }

    this.showableUI.show(imageFileName);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    this.showableUI.setWorkspace(workspace);
  }

  public interface GridSiftDetectorApplier {

    void apply(final Workspace workspace, final File testImage, final File trainImagesFolder,
        final int k);
  }
}

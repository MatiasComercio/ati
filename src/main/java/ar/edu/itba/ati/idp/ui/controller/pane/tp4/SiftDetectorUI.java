package ar.edu.itba.ati.idp.ui.controller.pane.tp4;

import static ar.edu.itba.ati.idp.ui.component.InputExtractors.getPositiveDoubleIE;
import static ar.edu.itba.ati.idp.ui.component.InputExtractors.getPositiveIntIE;

import ar.edu.itba.ati.idp.ui.OpenFileChooser;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

public class SiftDetectorUI implements Showable {
  private static final String STAGE_TITLE = "Sift Detector";
  private static final String MATCHING_PERCENTAGE = "Matching Percentage";
  private static final String MATCHING_DISTANCE = "Matching Distance";
  private static final String INT_PROMPT = "1";
  private static final String DOUBLE_PROMPT = "1.0";
  private static final int DEFAULT_MATCHING_DISTANCE = 100;
  private static final double DEFAULT_MATCHING_PERCENTAGE = .5d;

  private final OpenFileChooser openFileChooser;
  private final Showable showableUI;
  private final Field<Integer> matchingDistanceIE;
  private final Field<Double> matchingPercentageIE;

  private Workspace workspace;

  private SiftDetectorUI(final SiftDetectorApplier applier) {
    this.openFileChooser = new OpenFileChooser();
    this.matchingDistanceIE = Field.newInstance(MATCHING_DISTANCE, INT_PROMPT, getPositiveIntIE());
    this.matchingPercentageIE = Field.newInstance(MATCHING_PERCENTAGE, DOUBLE_PROMPT, getPositiveDoubleIE());

    setDefaultValues();

    //noinspection CodeBlock2Expr
    this.showableUI = FloatingPane.newInstance(STAGE_TITLE, (lambdaWorkspace, imageFile) -> {
      applier.apply(lambdaWorkspace,
                    imageFile.getFile().getPath(), // Image1: the one loaded in the workspace
                    // Image2: the one loaded from the file chooser.
                    openFileChooser.chooseToLoadOneManually(lambdaWorkspace).getPath(),
                    matchingDistanceIE.getValue(),
                    matchingPercentageIE.getValue()
                    );
    }, matchingDistanceIE, matchingPercentageIE);
  }

  public static SiftDetectorUI newInstance(final SiftDetectorApplier applier) {
    return new SiftDetectorUI(applier);
  }

  @Override
  public void show(final String imageFileName) {
    if (workspace == null) return;
    showableUI.show(imageFileName);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    showableUI.setWorkspace(workspace);
  }

  private void setDefaultValues() {
    matchingDistanceIE.setValue(DEFAULT_MATCHING_DISTANCE);
    matchingPercentageIE.setValue(DEFAULT_MATCHING_PERCENTAGE);
  }

  public interface SiftDetectorApplier {
    void apply(Workspace workspace, String imageFile1, String imageFile2,
               int matchingDistance, double matchingPercentage);
  }
}
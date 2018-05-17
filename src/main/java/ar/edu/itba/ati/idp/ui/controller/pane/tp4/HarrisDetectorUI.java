package ar.edu.itba.ati.idp.ui.controller.pane.tp4;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.InputExtractors;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

public class HarrisDetectorUI implements Showable {
  private static final String STAGE_TITLE = "Harris Detector";
  private static final String INTEGER_PROMPT = "1";
  private static final String DOUBLE_PROMPT = "1.0";
  private static final String GAUSS_SIGMA = "Sigma";
  private static final String GAUSS_SIDE_LENGTH = "Gauss Mask Side Length";
  private static final String CIM_ID = "CIM ID";
  private static final String K = "K";
  private static final String ACCEPTANCE_PERCENTAGE = "Acceptance Percentage";

  private static final double DEFAULT_SIGMA = 2.0;
  private static final int DEFAULT_GAUSS_SIDE_LENGTH = 7;
  private static final int DEFAULT_CIM_ID = 2;
  private static final double DEFAULT_K = 0.04;
  private static final double DEFAULT_ACCEPTANCE_PERCENTAGE = .8;

  private final Showable showableUI;
  private final Field<Double> gaussSigmaIE;
  private final Field<Integer> gaussSideLengthIE;
  private final Field<Integer> cimIdIE;
  private final Field<Double> kIE;
  private final Field<Double> acceptancePercentage;

  private Workspace workspace;

  private HarrisDetectorUI(final HarrisDetectorApplier applier) {
    final InputExtractor<Double> doubleIE = InputExtractors.getDoubleIE();
    final InputExtractor<Double> positiveDoubleIE = InputExtractors.getPositiveDoubleIE();
    final InputExtractor<Integer> positiveIntIE = InputExtractors.getPositiveIntIE();
    gaussSigmaIE = Field.newInstance(GAUSS_SIGMA, DOUBLE_PROMPT, positiveDoubleIE);
    gaussSideLengthIE = Field.newInstance(GAUSS_SIDE_LENGTH, INTEGER_PROMPT, positiveIntIE);
    cimIdIE = Field.newInstance(CIM_ID, INTEGER_PROMPT, positiveIntIE);
    kIE = Field.newInstance(K, DOUBLE_PROMPT, positiveDoubleIE);
    acceptancePercentage = Field.newInstance(ACCEPTANCE_PERCENTAGE, DOUBLE_PROMPT, positiveDoubleIE);

    setDefaultValues();

    //noinspection CodeBlock2Expr
    this.showableUI = FloatingPane.newInstance(STAGE_TITLE, (workspace1, imageFile) -> {
      applier.apply(workspace1, imageFile,
                    gaussSigmaIE.getValue(), gaussSideLengthIE.getValue(), cimIdIE.getValue(),
                    kIE.getValue(), acceptancePercentage.getValue());
    }, new Field[][] { // Three visual rows of input elements
        {gaussSigmaIE, gaussSideLengthIE},
        {cimIdIE, kIE, acceptancePercentage},
    });
  }

  public static HarrisDetectorUI newInstance(final HarrisDetectorApplier applier) {
    return new HarrisDetectorUI(applier);
  }

  @Override
  public void show(final String imageFileName) {
    if (workspace == null) return;
    showableUI.show(imageFileName);
  }

  private void setDefaultValues() {
    // Set default values.
    gaussSigmaIE.setValue(DEFAULT_SIGMA);
    gaussSideLengthIE.setValue(DEFAULT_GAUSS_SIDE_LENGTH);
    cimIdIE.setValue(DEFAULT_CIM_ID);
    kIE.setValue(DEFAULT_K);
    acceptancePercentage.setValue(DEFAULT_ACCEPTANCE_PERCENTAGE);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    showableUI.setWorkspace(workspace);
  }

  public interface HarrisDetectorApplier {

    void apply(final Workspace workspace, final ImageFile imageFile,
               final double gaussSigma, final int gaussSideLength,
               final int cimId, final double k,
               final double acceptancePercentage);
  }
}
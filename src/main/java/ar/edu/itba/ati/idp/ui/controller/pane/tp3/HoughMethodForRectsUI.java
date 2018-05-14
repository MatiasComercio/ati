package ar.edu.itba.ati.idp.ui.controller.pane.tp3;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.InputExtractors;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

public class HoughMethodForRectsUI implements Showable {
  private static final String STAGE_TITLE = "Hough Method - Rects";
  private static final String DOUBLE_PROMPT = "1.0";
  private static final String RHO_START = "Rho Start";
  private static final String RHO_STEP = "Rho Step";
  private static final String RHO_END = "Rho End";
  private static final String THETA_START = "Theta Start";
  private static final String THETA_STEP = "Theta Step";
  private static final String THETA_END = "Theta End";
  private static final String EPSILON = "Epsilon";
  private static final String ACCEPTANCE_PERCENTAGE = "Acceptance Percentage";
  private static final String ACCEPTANCE_PERCENTAGE_PROMPT = "min: 0.0; max: 1.0";

  private static final double BOUND_DEGREE = 90.0;
  private static final double DEFAULT_EPSILON = .9;
  private static final double DEFAULT_ACCEPTANCE_PERCENTAGE = .8;
  private static final double RHO_DEFAULT_STEP = 1.0;
  private static final double THETA_DEFAULT_STEP = 45.0;

  private final Showable showableUI;
  private final Field<Double> rhoStartIE;
  private final Field<Double> rhoStepIE;
  private final Field<Double> rhoEndIE;
  private final Field<Double> thetaStartIE;
  private final Field<Double> thetaStepIE;
  private final Field<Double> thetaEndIE;
  private final Field<Double> epsilonIE;
  private final Field<Double> acceptancePercentageIE;

  private Workspace workspace;

  private HoughMethodForRectsUI(final HoughMethodForRectsApplier applier) {
    final InputExtractor<Double> doubleIE = InputExtractors.getDoubleIE();
    final InputExtractor<Double> positiveDoubleIE = InputExtractors.getPositiveDoubleIE();
    rhoStartIE = Field.newInstance(RHO_START, DOUBLE_PROMPT, doubleIE);
    rhoStepIE = Field.newInstance(RHO_STEP, DOUBLE_PROMPT, positiveDoubleIE);
    rhoEndIE = Field.newInstance(RHO_END, DOUBLE_PROMPT, doubleIE);
    thetaStartIE = Field.newInstance(THETA_START, DOUBLE_PROMPT, doubleIE);
    thetaStepIE = Field.newInstance(THETA_STEP, DOUBLE_PROMPT, positiveDoubleIE);
    thetaEndIE = Field.newInstance(THETA_END, DOUBLE_PROMPT, doubleIE);
    epsilonIE = Field.newInstance(EPSILON, DOUBLE_PROMPT, doubleIE);
    acceptancePercentageIE = Field.newInstance(ACCEPTANCE_PERCENTAGE, ACCEPTANCE_PERCENTAGE_PROMPT, doubleIE);

    //noinspection CodeBlock2Expr
    this.showableUI = FloatingPane.newInstance(STAGE_TITLE, (workspace1, imageFile) -> {
      applier.apply(workspace1, imageFile,
                    rhoStartIE.getValue(), rhoStepIE.getValue(), rhoEndIE.getValue(),
                    thetaStartIE.getValue(), thetaStepIE.getValue(), thetaEndIE.getValue(),
                    epsilonIE.getValue(), acceptancePercentageIE.getValue());
    }, new Field[][] { // Three visual rows of input elements
        {rhoStartIE, rhoStepIE, rhoEndIE},
        {thetaStartIE, thetaStepIE, thetaEndIE},
        {epsilonIE, acceptancePercentageIE}
    });
  }

  public static HoughMethodForRectsUI newInstance(final HoughMethodForRectsApplier applier) {
    return new HoughMethodForRectsUI(applier);
  }

  @Override
  public void show(final String imageFileName) {
    showableUI.show(imageFileName);

    if (workspace == null) return;
    workspace.getOpImageFile().ifPresent(this::setDefaults);
  }

  private void setDefaults(final ImageFile imageFile) {
    // Set default values.
    final ImageMatrix imageMatrix = imageFile.getImageMatrix();
    final int d = max(imageMatrix.getWidth(), imageMatrix.getHeight());
    final double diagonal = ceil(sqrt(2) * d); // So as to not show all decimals.
    rhoStartIE.setValue(-diagonal);
    rhoStepIE.setValue(RHO_DEFAULT_STEP);
    rhoEndIE.setValue(diagonal);
    thetaStartIE.setValue(-BOUND_DEGREE);
    thetaStepIE.setValue(THETA_DEFAULT_STEP);
    thetaEndIE.setValue(BOUND_DEGREE);
    epsilonIE.setValue(DEFAULT_EPSILON);
    acceptancePercentageIE.setValue(DEFAULT_ACCEPTANCE_PERCENTAGE);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    showableUI.setWorkspace(workspace);
  }

  public interface HoughMethodForRectsApplier {
    void apply(final Workspace workspace, final ImageFile imageFile,
               final double rhoStart, final double rhoStep, final double rhoEnd,
               final double thetaStart, final double thetaStep, final double thetaEnd,
               final double epsilon, final double acceptancePercentage);
  }
}
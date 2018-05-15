package ar.edu.itba.ati.idp.ui.controller.pane.tp3;

import static java.lang.Math.max;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.InputExtractors;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import java.util.Optional;
import javafx.scene.shape.Rectangle;

public class HoughMethodForCirclesUI implements Showable {
  private static final String STAGE_TITLE = "Hough Method - Circles";
  private static final String DOUBLE_PROMPT = "1.0";
  private static final String A_START = "a Start";
  private static final String A_STEP = "a Step";
  private static final String A_END = "a End";
  private static final String B_START = "b Start";
  private static final String B_STEP = "b Step";
  private static final String B_END = "b End";
  private static final String RADIUS_START = "Radius Start";
  private static final String RADIUS_STEP = "Radius Step";
  private static final String RADIUS_END = "Radius End";
  private static final String EPSILON = "Epsilon";
  private static final String ACCEPTANCE_PERCENTAGE = "Acceptance Percentage";
  private static final String ACCEPTANCE_PERCENTAGE_PROMPT = "min: 0.0; max: 1.0";

  private static final double DEFAULT_START_RADIUS = 0.0;
  private static final double DEFAULT_EPSILON = .9;
  private static final double DEFAULT_ACCEPTANCE_PERCENTAGE = .8;
  private static final double DEFAULT_STEP = 1.0;

  private final Showable showableUI;
  private final Field<Double> aStartIE;
  private final Field<Double> aStepIE;
  private final Field<Double> aEndIE;
  private final Field<Double> bStartIE;
  private final Field<Double> bStepIE;
  private final Field<Double> bEndIE;
  private final Field<Double> radiusStartIE;
  private final Field<Double> radiusStepIE;
  private final Field<Double> radiusEndIE;
  private final Field<Double> epsilonIE;
  private final Field<Double> acceptancePercentageIE;

  private Workspace workspace;

  private HoughMethodForCirclesUI(final HoughMethodForRectsApplier applier) {
    final InputExtractor<Double> doubleIE = InputExtractors.getDoubleIE();
    final InputExtractor<Double> positiveDoubleIE = InputExtractors.getPositiveDoubleIE();
    aStartIE = Field.newInstance(A_START, DOUBLE_PROMPT, doubleIE);
    aStepIE = Field.newInstance(A_STEP, DOUBLE_PROMPT, positiveDoubleIE);
    aEndIE = Field.newInstance(A_END, DOUBLE_PROMPT, doubleIE);
    bStartIE = Field.newInstance(B_START, DOUBLE_PROMPT, doubleIE);
    bStepIE = Field.newInstance(B_STEP, DOUBLE_PROMPT, positiveDoubleIE);
    bEndIE = Field.newInstance(B_END, DOUBLE_PROMPT, doubleIE);
    radiusStartIE = Field.newInstance(RADIUS_START, DOUBLE_PROMPT, doubleIE);
    radiusStepIE = Field.newInstance(RADIUS_STEP, DOUBLE_PROMPT, positiveDoubleIE);
    radiusEndIE = Field.newInstance(RADIUS_END, DOUBLE_PROMPT, doubleIE);
    epsilonIE = Field.newInstance(EPSILON, DOUBLE_PROMPT, doubleIE);
    acceptancePercentageIE = Field.newInstance(ACCEPTANCE_PERCENTAGE, ACCEPTANCE_PERCENTAGE_PROMPT, doubleIE);

    //noinspection CodeBlock2Expr
    this.showableUI = FloatingPane.newInstance(STAGE_TITLE, (workspace1, imageFile) -> {
      applier.apply(workspace1, imageFile,
                    aStartIE.getValue(), aStepIE.getValue(), aEndIE.getValue(),
                    bStartIE.getValue(), bStepIE.getValue(), bEndIE.getValue(),
                    radiusStartIE.getValue(), radiusStepIE.getValue(), radiusEndIE.getValue(),
                    epsilonIE.getValue(), acceptancePercentageIE.getValue());
    }, new Field[][] { // Three visual rows of input elements
        {aStartIE, aStepIE, aEndIE},
        {bStartIE, bStepIE, bEndIE},
        {radiusStartIE, radiusStepIE, radiusEndIE},
        {epsilonIE, acceptancePercentageIE}
    });
  }

  public static HoughMethodForCirclesUI newInstance(final HoughMethodForRectsApplier applier) {
    return new HoughMethodForCirclesUI(applier);
  }

  @Override
  public void show(final String imageFileName) {
    if (workspace == null) return;
    showableUI.show(imageFileName);
    workspace.getOpImageFile().ifPresent(imageFile -> showWithDefaults(imageFile, workspace.getSelectedArea()));
  }

  private void showWithDefaults(final ImageFile imageFile,
                                // Nasty but useful
                                @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                                final Optional<Rectangle> opRectangle) {
    // Set default values.
    final ImageMatrix imageMatrix = imageFile.getImageMatrix();
    final double maxRadius = max(imageMatrix.getWidth(), imageMatrix.getHeight());
    final Rectangle rectangle = opRectangle.orElse(new Rectangle(0, 0, imageMatrix.getWidth(), imageMatrix.getHeight()));
    final double aStart = rectangle.getX();
    final double aEnd = aStart + rectangle.getWidth();
    final double bStart = rectangle.getY();
    final double bEnd = bStart + rectangle.getHeight();
    aStartIE.setValue(aStart);
    aStepIE.setValue(DEFAULT_STEP);
    aEndIE.setValue(aEnd);
    bStartIE.setValue(bStart);
    bStepIE.setValue(DEFAULT_STEP);
    bEndIE.setValue(bEnd);
    radiusStartIE.setValue(DEFAULT_START_RADIUS);
    radiusStepIE.setValue(DEFAULT_STEP);
    radiusEndIE.setValue(maxRadius);
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
               final double aStart, final double aStep, final double aEnd,
               final double bStart, final double bStep, final double bEnd,
               final double radiusStart, final double radiusStep, final double radiusEnd,
               final double epsilon, final double acceptancePercentage);
  }
}
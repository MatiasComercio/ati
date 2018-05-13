package ar.edu.itba.ati.idp.ui.controller.pane.tp3;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

public class SusanBorderAndCornerDetectorUI implements Showable {
  private static final String STAGE_TITLE = "Susan Border And Corner Detector";
  private static final String THRESHOLD = "Threshold";
  private static final String THRESHOLD_PROMPT = "1.0";
  private static final InputExtractor<Double> THRESHOLD_IE = textField -> {
    Double value;
    try {
      value = Double.parseDouble(textField);
    } catch (final NumberFormatException exception) {
      value = null; // Not a Double
    }

    return value != null && value >= 0 ? value : null;
  };

  private final Showable showableUI;

  private SusanBorderAndCornerDetectorUI(final Showable showableUI) {
    this.showableUI = showableUI;
  }

  public static SusanBorderAndCornerDetectorUI newInstance(final SusanBorderAndCornerDetectorApplier detectorApplier) {
    final Field<Double> thresholdIE = Field.newInstance(THRESHOLD, THRESHOLD_PROMPT, THRESHOLD_IE);
    //noinspection CodeBlock2Expr
    final Showable showableUI = FloatingPane.newInstance(STAGE_TITLE, (workspace, imageFile) -> {
      detectorApplier.apply(workspace, imageFile, thresholdIE.getValue());
    }, thresholdIE);
    return new SusanBorderAndCornerDetectorUI(showableUI);
  }

  @Override
  public void show(final String imageFileName) {
    showableUI.show(imageFileName);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    showableUI.setWorkspace(workspace);
  }

  public interface SusanBorderAndCornerDetectorApplier {
    void apply(final Workspace workspace, final ImageFile imageFile, final double threshold);
  }
}

package ar.edu.itba.ati.idp.ui.controller.pane.tp2;

import ar.edu.itba.ati.idp.function.border.ZeroCrossesBorderDetector;
import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

public class LoGBorderDetectorUI implements Showable {
  private static final String STAGE_TITLE = "LoG Border Detector";
  private static final String SIGMA_LABEL = "Sigma";
  private static final String SIGMA_PROMPT = "1.0";
  private static final InputExtractor<Double> SIGMA_IE = textField -> {
    Double value;
    try {
      value = Double.parseDouble(textField);
    } catch (final NumberFormatException exception) {
      value = null; // Not a Double
    }

    return value != null && value > 0 ? value : null;
  };

  private final Field<Double> sigmaIE;
  private final Showable showableUI;

  private LoGBorderDetectorUI(final String stageTitle, final Field<Double> sigmaIE) {
    this.sigmaIE = sigmaIE;
    this.showableUI = FloatingPane.newInstance(stageTitle, this::handleApply, sigmaIE);
  }

  // `Unused` warning suppressed as it is needed for lambda call format.
  private void handleApply(final Workspace workspace,
                           @SuppressWarnings("unused") final ImageFile imageFile) {
     workspace.applyToImage(ZeroCrossesBorderDetector.newLoG(sigmaIE.getValue()));
  }

  public static LoGBorderDetectorUI newInstance() {
    final Field<Double> sigmaIE = Field.newInstance(SIGMA_LABEL, SIGMA_PROMPT, SIGMA_IE);
    return new LoGBorderDetectorUI(STAGE_TITLE, sigmaIE);
  }

  @Override
  public void show(final String imageFileName) {
    showableUI.show(imageFileName);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    showableUI.setWorkspace(workspace);
  }
}

package ar.edu.itba.ati.idp.ui.controller.pane.tp3;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CannyBorderDetectorUI implements Showable {
  private static final String STAGE_TITLE = "Canny Border Detector";
  private static final String SIGMAS_LABEL = "Sigmas";
  private static final String SIGMAS_PROMPT = "1.0 ;  2.5";
  private static final InputExtractor<Double[]> SIGMAS_IE = textField -> {
    final Set<Double> values = new HashSet<>();

    try {
      final String[] textValues = textField.split("\\s*;\\s*");
      for (final String textValue : textValues) {
        final Double value = Double.parseDouble(textValue);
        if (value < 0) {
          return null; // Not a valid value
        }
        values.add(value);
      }
    } catch (final NumberFormatException exception) {
      return null; // Not a Double
    }

    return values.toArray(new Double[0]);
  };

  private final Showable showableUI;

  private CannyBorderDetectorUI(final Showable showableUI) {
    this.showableUI = showableUI;
  }

  public static CannyBorderDetectorUI newInstance(final CannyDetectorApplier detectorApplier) {
    final Field<Double[]> sigmasIE = Field.newInstance(SIGMAS_LABEL, SIGMAS_PROMPT, SIGMAS_IE);
    //noinspection CodeBlock2Expr
    final Showable showableUI = FloatingPane.newInstance(STAGE_TITLE, (workspace, imageFile) -> {
      final double[] sigmas = Arrays.stream(sigmasIE.getValue()).mapToDouble(Double::doubleValue).toArray();
      detectorApplier.apply(workspace, imageFile, sigmas);
    }, sigmasIE);

    return new CannyBorderDetectorUI(showableUI);
  }

  @Override
  public void show(final String imageFileName) {
    showableUI.show(imageFileName);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    showableUI.setWorkspace(workspace);
  }

  public interface CannyDetectorApplier {
    void apply(final Workspace workspace, final ImageFile imageFile, final double[] sigmas);
  }
}

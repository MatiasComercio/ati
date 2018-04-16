package ar.edu.itba.ati.idp.ui.controller.pane.tp2;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

public class AnisotropicDiffusionUI implements Showable {
  private static final String TIMES_LABEL = "Times";
  private static final String TIMES_PROMPT = "1";
  private static final String SIGMA_LABEL = "Sigma";
  private static final String SIGMA_PROMPT = "1.0";
  private static final InputExtractor<Integer> TIMES_IE = textField -> {
    Integer value;
    try {
      value = Integer.parseInt(textField);
    } catch (final NumberFormatException exception) {
      value = null; // Not an Integer
    }

    return value != null && value > 0 ? value : null;
  };

  private static final InputExtractor<Double> SIGMA_IE = textField -> {
    Double value;
    try {
      value = Double.parseDouble(textField);
    } catch (final NumberFormatException exception) {
      value = null; // Not a Double
    }

    return value != null && value > 0 ? value : null;
  };

  private final Showable showableUI;

  private AnisotropicDiffusionUI(final Showable showableUI) {
    this.showableUI = showableUI;
  }

  public static AnisotropicDiffusionUI newInstance(final String stageTitle, final AnisotropicDiffusionApplier diffusionApplier) {
    final Field<Integer> timesIE = Field.newInstance(TIMES_LABEL, TIMES_PROMPT, TIMES_IE);
    final Field<Double> sigmaIE = Field.newInstance(SIGMA_LABEL, SIGMA_PROMPT, SIGMA_IE);
    //noinspection CodeBlock2Expr
    final Showable showableUI = FloatingPane.newInstance(stageTitle, (workspace, imageFile) -> {
      diffusionApplier.apply(workspace, imageFile, timesIE.getValue(), sigmaIE.getValue());
    }, timesIE, sigmaIE);

    return new AnisotropicDiffusionUI(showableUI);
  }

  @Override
  public void show(final String imageFileName) {
    showableUI.show(imageFileName);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    showableUI.setWorkspace(workspace);
  }

  public interface AnisotropicDiffusionApplier {
    void apply(final Workspace workspace, final ImageFile imageFile, final int times, final double sigma);
  }
}

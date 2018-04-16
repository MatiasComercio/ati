package ar.edu.itba.ati.idp.ui.controller.pane.tp2;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.component.Field;
import ar.edu.itba.ati.idp.ui.component.FloatingPane;
import ar.edu.itba.ati.idp.ui.component.InputExtractor;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

public class IsotropicDiffusionUI implements Showable {
  private static final String STAGE_TITLE = "Isotropic Diffusion";
  private static final String TIMES_LABEL = "Times";
  private static final String TIMES_PROMPT = "1";
  private static final InputExtractor<Integer> TIMES_IE = textField -> {
    Integer value;
    try {
      value = Integer.parseInt(textField);
    } catch (final NumberFormatException exception) {
      value = null; // Not an Integer
    }

    return value != null && value > 0 ? value : null;
  };

  private final Showable showableUI;

  private IsotropicDiffusionUI(final Showable showableUI) {
    this.showableUI = showableUI;
  }

  public static IsotropicDiffusionUI newInstance(final IsometricDiffusionApplier diffusionApplier) {
    final Field<Integer> timesIE = Field.newInstance(TIMES_LABEL, TIMES_PROMPT, TIMES_IE);
    //noinspection CodeBlock2Expr
    final Showable showableUI = FloatingPane.newInstance(STAGE_TITLE, (workspace, imageFile) -> {
      diffusionApplier.apply(workspace, imageFile, timesIE.getValue());
    }, timesIE);
    return new IsotropicDiffusionUI(showableUI);
  }

  @Override
  public void show(final String imageFileName) {
    showableUI.show(imageFileName);
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    showableUI.setWorkspace(workspace);
  }

  public interface IsometricDiffusionApplier {
    void apply(final Workspace workspace, final ImageFile imageFile, final int times);
  }
}

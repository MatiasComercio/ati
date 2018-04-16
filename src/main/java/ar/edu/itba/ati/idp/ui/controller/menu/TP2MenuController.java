package ar.edu.itba.ati.idp.ui.controller.menu;

import static ar.edu.itba.ati.idp.function.threshold.GlobalThresholding.GLOBAL_THRESHOLDING;
import static ar.edu.itba.ati.idp.function.threshold.OptimumThreshold.OPTIMUM_THRESHOLD;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.border.Diffusion;
import ar.edu.itba.ati.idp.function.border.DirectionalBorderDetector;
import ar.edu.itba.ati.idp.function.border.GradientBorderDetector;
import ar.edu.itba.ati.idp.function.border.ZeroCrossesBorderDetector;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.ui.controller.pane.tp2.AnisotropicDiffusionUI;
import ar.edu.itba.ati.idp.ui.controller.pane.tp2.IsotropicDiffusionUI;
import ar.edu.itba.ati.idp.ui.controller.pane.tp2.LoGBorderDetectorUI;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class TP2MenuController extends Menu {

  private static final String LAYOUT_PATH = "ui/menu/tp2Menu.fxml";
  private static final String ANISOTROPIC_PREPEND = "Anisotropic ";
  private static final String ANISOTROPIC_APPEND = " Diffusion";

  private final Showable loGBorderDetectorUI;
  private final Showable isotropicDiffusionUI;
  private final Showable anisotropicLeclercDiffusionUI;
  private final Showable anisotropicLorentzDiffusionUI;
  private final Showable anisotropicMinDiffusionUI;

  private Workspace workspace;

  @SuppressWarnings("CodeBlock2Expr")
  public TP2MenuController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
    loGBorderDetectorUI = LoGBorderDetectorUI.newInstance();
    isotropicDiffusionUI = IsotropicDiffusionUI.newInstance((workspace, imageFile, times) -> {
      workspace.applyToImage(Diffusion.newIsometric(times));
    });
    anisotropicLeclercDiffusionUI = AnisotropicDiffusionUI.newInstance(buildAnisotropicStageName("Leclerc"),
                                                                       (workspace, imageFile, times, sigma) -> {
      workspace.applyToImage(Diffusion.newLeclerc(times, sigma));
    });
    anisotropicLorentzDiffusionUI = AnisotropicDiffusionUI.newInstance(buildAnisotropicStageName("Lorentz"),
                                                                       (workspace, imageFile, times, sigma) -> {
      workspace.applyToImage(Diffusion.newLorentz(times, sigma));
    });
    anisotropicMinDiffusionUI = AnisotropicDiffusionUI.newInstance(buildAnisotropicStageName("Min"),
                                                                   (workspace, imageFile, times, sigma) -> {
      workspace.applyToImage(Diffusion.newMin(times, sigma));
    });
  }

  private static String buildAnisotropicStageName(final String diffusionName) {
    return ANISOTROPIC_PREPEND + diffusionName + ANISOTROPIC_APPEND;
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    loGBorderDetectorUI.setWorkspace(workspace);
    isotropicDiffusionUI.setWorkspace(workspace);
    anisotropicLeclercDiffusionUI.setWorkspace(workspace);
    anisotropicLorentzDiffusionUI.setWorkspace(workspace);
    anisotropicMinDiffusionUI.setWorkspace(workspace);
  }

  @FXML
  public void handlePrewittBorderDetector() {
    apply(GradientBorderDetector.PREWITT);
  }

  @FXML
  public void handleSobelBorderDetector() {
    apply(GradientBorderDetector.SOBEL);
  }

  @FXML
  public void handleUnnamedDirectionalBorderDetector() {
    apply(DirectionalBorderDetector.UNNAMED);
  }

  @FXML
  public void handleKirschDirectionalBorderDetector() {
    apply(DirectionalBorderDetector.KIRSCH);
  }

  @FXML
  public void handlePrewittDirectionalBorderDetector() {
    apply(DirectionalBorderDetector.PREWITT);
  }

  @FXML
  public void handleSobelDirectionalBorderDetector() {
    apply(DirectionalBorderDetector.SOBEL);
  }

  @FXML
  public void handleLaplaceBorderDetector() {
    apply(ZeroCrossesBorderDetector.getLaplace());
  }

  @FXML
  public void handleLaplaceSlopeBorderDetector() {
    apply(ZeroCrossesBorderDetector.getLaplaceSlopes());
  }

  @FXML
  public void handleLoGBorderDetector() {
    show(loGBorderDetectorUI);
  }

  @FXML
  public void handleIsotropicDiffusion() {
    show(isotropicDiffusionUI);
  }

  @FXML
  public void handleAnisotropicLeclercDiffusion() {
    show(anisotropicLeclercDiffusionUI);
  }

  @FXML
  public void handleAnisotropicLorentzDiffusion() {
    show(anisotropicLorentzDiffusionUI);
  }

  @FXML
  public void handleAnisotropicMinDiffusion() {
    show(anisotropicMinDiffusionUI);
  }

  @FXML
  public void handleGlobalThresholding() {
    apply(GLOBAL_THRESHOLDING);
  }

  @FXML
  public void handleOptimumThreshold() {
    apply(OPTIMUM_THRESHOLD);
  }

  private void apply(final UniquePixelsBandOperator op) {
    workspace.getOpImageFile().ifPresent(imgF -> workspace.applyToImage(op));
  }

  private void show(final Showable showable) {
    workspace.getOpImageFile().ifPresent(imageFile -> showable.show(imageFile.getFile().getName()));
  }
}

package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.CannyFilter;
import ar.edu.itba.ati.idp.function.filter.SusanFilter;
import ar.edu.itba.ati.idp.function.objectdetector.CircleHoughMethod;
import ar.edu.itba.ati.idp.function.objectdetector.RectHoughMethod;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.ui.controller.pane.tp3.CannyBorderDetectorUI;
import ar.edu.itba.ati.idp.ui.controller.pane.tp3.HoughMethodForCirclesUI;
import ar.edu.itba.ati.idp.ui.controller.pane.tp3.HoughMethodForRectsUI;
import ar.edu.itba.ati.idp.ui.controller.pane.tp3.SusanBorderAndCornerDetectorUI;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class TP3MenuController extends Menu {
  private static final String LAYOUT_PATH = "ui/menu/tp3Menu.fxml";

  private final Showable cannyBorderDetectorUI;
  private final Showable susanBorderAndCornerDetectorUI;
  private final Showable houghMethodForRectsUI;
  private final Showable houghMethodForCirclesUI;

  private Workspace workspace;

  @SuppressWarnings("CodeBlock2Expr")
  public TP3MenuController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
    cannyBorderDetectorUI = CannyBorderDetectorUI.newInstance((workspace, imageFile, sigmas) -> {
      workspace.applyToImage(CannyFilter.newInstance(sigmas));
    });
    susanBorderAndCornerDetectorUI = SusanBorderAndCornerDetectorUI.newInstance((workspace, imageFile, threshold) -> {
      workspace.applyToImage(SusanFilter.newInstance(threshold));
    });
    houghMethodForRectsUI = HoughMethodForRectsUI.newInstance((workspace, imageFile,
                                                               rhoStart, rhoStep, rhoEnd,
                                                               thetaStart, thetaStep, thetaEnd,
                                                               epsilon, acceptancePercentage) -> {
      workspace.applyToImage(RectHoughMethod.newInstance(rhoStart, rhoStep, rhoEnd,
                                                         thetaStart, thetaStep, thetaEnd,
                                                         epsilon, acceptancePercentage));
    });
    houghMethodForCirclesUI = HoughMethodForCirclesUI.newInstance((workspace, imageFile,
                                                                   aStart, aStep, aEnd,
                                                                   bStart, bStep, bEnd,
                                                                   radiusStart, radiusStep, radiusEnd,
                                                                   epsilon, acceptancePercentage) -> {
      workspace.applyToImage(CircleHoughMethod.newInstance(aStart, aStep, aEnd,
                                                           bStart, bStep, bEnd,
                                                           radiusStart, radiusStep, radiusEnd,
                                                           epsilon, acceptancePercentage));
    });
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
    cannyBorderDetectorUI.setWorkspace(workspace);
    susanBorderAndCornerDetectorUI.setWorkspace(workspace);
    houghMethodForRectsUI.setWorkspace(workspace);
    houghMethodForCirclesUI.setWorkspace(workspace);
  }

  @FXML
  public void handleCannyBorderDetector() {
    show(cannyBorderDetectorUI);
  }

  @FXML
  public void handleSusanBorderAndCornerDetector() {
    show(susanBorderAndCornerDetectorUI);
  }

  @FXML
  public void handleHoughMethodForRects() {
    show(houghMethodForRectsUI);
  }

  @FXML
  public void handleHoughMethodForCircles() {
    show(houghMethodForCirclesUI);
  }

  @FXML
  public void handleActiveContoursMethod() {
  }

  private void apply(final UniquePixelsBandOperator op) {
    workspace.getOpImageFile().ifPresent(imgF -> workspace.applyToImage(op));
  }

  private void show(final Showable showable) {
    workspace.getOpImageFile().ifPresent(imageFile -> showable.show(imageFile.getFile().getName()));
  }
}

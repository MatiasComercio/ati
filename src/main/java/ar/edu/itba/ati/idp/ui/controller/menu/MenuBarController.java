package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

/*
 * Thanks:
 * - https://stackoverflow.com/questions/19342259/how-to-create-multiple-javafx-controllers-with-different-fxml-files
 * - https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm
 * - https://stackoverflow.com/questions/23600926/how-to-understand-and-use-fxroot-in-javafx
 * - https://docs.oracle.com/javafx/2/get_started/fxml_tutorial.htm#CHDJIDHE: handle action function (parameter type)
 */
public class MenuBarController extends MenuBar {
  private static final String LAYOUT_PATH = "ui/menu/menuBar.fxml";

  @FXML
  private FileMenuController fileMenu;

  @FXML
  private EditMenuController editMenu;

  @FXML
  private HistogramMenuController histogramMenu;

  public MenuBarController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
  }

  public void setWorkspace(final Workspace workspace) {
    fileMenu.setWorkspace(workspace);
    editMenu.setWorkspace(workspace);
    histogramMenu.setWorkspace(workspace);
  }
}

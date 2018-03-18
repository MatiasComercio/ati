package ar.edu.itba.ati.idp.ui.controller;

import ar.edu.itba.ati.idp.ui.OpenFileChooser;
import ar.edu.itba.ati.idp.ui.SaveFileChooser;
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

  private final OpenFileChooser openFileChooser;
  private final SaveFileChooser saveFileChooser;

  private Workspace workspace;

  public MenuBarController() {
    this.openFileChooser = new OpenFileChooser();
    this.saveFileChooser = new SaveFileChooser();

    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  public void handleItemOpen() {
    openFileChooser.chooseToLoadInWorkspace(workspace);
  }

  @FXML
  public void handleItemOpenInNewWorkspace() {
    openFileChooser.chooseToLoadInWorkspace(Workspace.newInstance());
  }

  @FXML
  public void handleItemNewWorkspace() {
    Workspace.newInstance();
  }

  @FXML
  public void handleItemSave() {
    workspace.save();
  }

  @FXML
  public void handleItemSaveAs() {
    saveFileChooser.chooseToSaveAsUsingWorkspace(workspace);
  }

  @FXML
  public void handleItemClose() {
    workspace.unloadImage();
  }
}

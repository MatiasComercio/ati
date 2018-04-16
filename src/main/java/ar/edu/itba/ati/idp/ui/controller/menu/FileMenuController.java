package ar.edu.itba.ati.idp.ui.controller.menu;

import ar.edu.itba.ati.idp.ui.OpenFileChooser;
import ar.edu.itba.ati.idp.ui.SaveFileChooser;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class FileMenuController extends Menu {
  private static final String LAYOUT_PATH = "ui/menu/fileMenu.fxml";

  private final OpenFileChooser openFileChooser;
  private final SaveFileChooser saveFileChooser;

  private Workspace workspace;

  public FileMenuController() {
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
  public void handleItemDuplicateWorkspace() {
    workspace.duplicate();
  }

  @FXML
  public void handleItemOpenOriginalInNewWorkspace() {
    workspace.openOriginalInNewWorkspace();
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

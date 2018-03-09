package ar.edu.itba.ati.idp.ui;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainMenuBar extends MenuBar {

  // TODO: Sacar de otro lado
  private static final List<String> supportedExtensions = Arrays.asList("raw", "pgm", "ppm", "bmp");
  private static final List<ExtensionFilter> extensionFilters = supportedExtensions.stream()
      .map((ext) -> new ExtensionFilter(ext.toUpperCase(), "*." + ext))
      .collect(Collectors.toList());

  // FIXME: This is ugly, very very ugly
  static {
    extensionFilters
        .add(0, new ExtensionFilter("All supported", supportedExtensions.stream()
            .map(ext -> "*." + ext).collect(Collectors.toList())));
  }

  private final Stage primaryStage;

  private final Menu menuFile = new Menu("File");
  private final MenuItem itemOpen = new MenuItem("Open...");
  private final MenuItem itemSave = new MenuItem("Save");
  private final MenuItem itemSaveAs = new MenuItem("Save as...");
  private final MenuItem itemClose = new MenuItem("Close");

  private final Menu menuEdit = new Menu("Edit");

  private final FileChooser openFileChooser = buildOpenFileChooser();
  private final FileChooser saveFileChooser = buildSaveAsFileChooser();

  public MainMenuBar(final Stage primaryStage) {
    super();
    this.primaryStage = primaryStage;
    this.setUseSystemMenuBar(true); // TODO: Test en windows
    this.menuFile.getItems()
        .addAll(itemOpen, new SeparatorMenuItem(), itemSave, itemSaveAs, new SeparatorMenuItem(),
            itemClose);
    this.getMenus().addAll(menuFile, menuEdit);
    this.itemSave.setDisable(true);
    this.itemSaveAs.setDisable(true);
    this.itemClose.setDisable(true);
  }

  public void setOnOpenAction(final Consumer<File> openHandler) {
    itemOpen.setOnAction(event -> {
      final File file = openFileChooser.showOpenDialog(primaryStage);
      if (file != null) {
        openHandler.accept(file);
        itemSave.setDisable(false);
        itemSaveAs.setDisable(false);
        itemClose.setDisable(false);
      }
    });
  }

  public void setOnSaveAction(final Runnable handler) {
    itemSave.setOnAction(event -> handler.run());
  }

  public void setOnSaveAsAction(final Consumer<File> saveHandler) {
    itemSaveAs.setOnAction(event -> {
      final File file = saveFileChooser.showSaveDialog(primaryStage);
      if (file != null) {
        saveHandler.accept(file);
      }
    });
  }

  public void setOnCloseAction(final Runnable handler) {
    itemClose.setOnAction(event -> {
      handler.run();
      itemSave.setDisable(true);
      itemSaveAs.setDisable(true);
      itemClose.setDisable(true);
    });
  }

  private static FileChooser buildSaveAsFileChooser() {
    final FileChooser saveAsFileChooser = new FileChooser();
    saveAsFileChooser.setTitle("Save as...");
    saveAsFileChooser.setInitialFileName("Untitled"); // TODO: Cambiar al del archivo actual?

    return saveAsFileChooser;
  }

  private static FileChooser buildOpenFileChooser() {
    final FileChooser openFileChooser = new FileChooser();
    openFileChooser.setTitle("Open image...");
    openFileChooser.getExtensionFilters().addAll(extensionFilters);

    return openFileChooser;
  }
}

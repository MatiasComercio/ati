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

// TODO: Habilitar y deshabilitar items del menu dependiendo de si hay o no una imagen abierta
public class MainMenuBar extends MenuBar {

  // TODO: Sacar de otro lado
  private static final List<String> supportedExtensions = Arrays.asList("raw", "pgm", "ppm", "bmp");
  private static final List<ExtensionFilter> extensionFilters = supportedExtensions.stream()
      .map((ext) -> new ExtensionFilter(ext.toUpperCase(), "*." + ext))
      .collect(Collectors.toList());

  static {
    extensionFilters
        .add(0, new ExtensionFilter("All supported", supportedExtensions.stream()
            .map(ext -> "*." + ext).collect(Collectors.toList())));
  }

  private final Stage mainStage;

  private final Menu menuFile = new Menu("File");
  private final MenuItem itemOpen = new MenuItem("Open...");
  private final MenuItem itemSave = new MenuItem("Save");
  private final MenuItem itemSaveAs = new MenuItem("Save as...");
  private final MenuItem itemClose = new MenuItem("Close");

  private final Menu menuEdit = new Menu("Edit");
  private final MenuItem itemEditNoise = new MenuItem("Noise...");
  private final Menu itemMenuEditWithImage = new Menu("With Image");
  private final MenuItem itemEditWithImageAdd = new MenuItem("Add...");
  private final MenuItem itemEditWithImageSubtract = new MenuItem("Subtract...");
  private final MenuItem itemEditWithImageMultiply = new MenuItem("Multiply...");
  private final MenuItem itemEditDinamicRange = new MenuItem("Dinamic Range...");
  private final MenuItem itemEditContrast = new MenuItem("Contrast...");
  private final MenuItem itemEditGamma = new MenuItem("Gamma..."); // TODO: Gamma o Gamma Correction
  private final MenuItem itemEditThreshold = new MenuItem("Threshold...");
  private final MenuItem itemEditApplyNegative = new MenuItem("Apply Negative");

  private final Menu menuView = new Menu("View");
  private final MenuItem itemViewOriginalImage = new MenuItem("Original Image");
  private final MenuItem itemViewHistogram = new MenuItem("Histogram");

  private final Menu menuHelp = new Menu("Help");
  private final MenuItem itemAbout = new MenuItem("About...");

  // TODO: Ver si hace falta tener 2 o con 1 alcanza. Uno para toda la app o solo para el menu?
  private final FileChooser openFileChooser = buildOpenFileChooser();
  private final FileChooser saveFileChooser = buildSaveAsFileChooser();

  public MainMenuBar(final Stage mainStage) {
    super();
    this.mainStage = mainStage;
    this.setUseSystemMenuBar(true); // TODO: Test en windows
    this.menuFile.getItems()
        .addAll(itemOpen, new SeparatorMenuItem(), itemSave, itemSaveAs, new SeparatorMenuItem(),
            itemClose);
    this.itemMenuEditWithImage.getItems()
        .addAll(itemEditWithImageAdd, itemEditWithImageSubtract, itemEditWithImageMultiply);
    this.menuEdit.getItems()
        .addAll(itemEditNoise, itemMenuEditWithImage, itemEditDinamicRange, itemEditContrast,
            itemEditGamma, itemEditThreshold, itemEditApplyNegative);
    this.menuView.getItems().addAll(itemViewOriginalImage, itemViewHistogram);
    this.menuHelp.getItems().addAll(itemAbout);
    this.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);
  }

  public void setOnOpenAction(final Consumer<File> openHandler) {
    itemOpen.setOnAction(event -> {
      final File file = openFileChooser.showOpenDialog(mainStage);
      if (file != null) {
        openHandler.accept(file);
      }
    });
  }

  public void setOnSaveAction(final Runnable handler) {
    itemSave.setOnAction(event -> handler.run());
  }

  public void setOnSaveAsAction(final Consumer<File> saveHandler) {
    itemSaveAs.setOnAction(event -> {
      final File file = saveFileChooser.showSaveDialog(mainStage);
      if (file != null) {
        saveHandler.accept(file);
      }
    });
  }

  public void setOnCloseAction(final Runnable handler) {
    itemClose.setOnAction(event -> handler.run());
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

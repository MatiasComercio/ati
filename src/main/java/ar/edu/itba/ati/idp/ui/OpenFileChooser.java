package ar.edu.itba.ati.idp.ui;

import ar.edu.itba.ati.idp.io.ImageLoader;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class OpenFileChooser {
  private static final String TITLE = "Open image...";
  private static final List<ExtensionFilter> EXTENSION_FILTERS;

  static {
    final Set<String> supportedFileExtensions = ImageLoader.getSupportedFileExtensions();

    EXTENSION_FILTERS = supportedFileExtensions.stream()
        .map((ext) -> new ExtensionFilter(ext.toUpperCase(), extensionRegex(ext)))
        .collect(Collectors.toList()
    );

    EXTENSION_FILTERS.add(0, new ExtensionFilter("All supported",
        supportedFileExtensions.stream()
            .map(OpenFileChooser::extensionRegex)
            .collect(Collectors.toList()))
    );
  }

  private static String extensionRegex(final String ext) {
    return "*." + ext;
  }

  private final FileChooser fileChooser;

  public OpenFileChooser() {
    this.fileChooser = new FileChooser();
    fileChooser.setTitle(TITLE);
    fileChooser.getExtensionFilters().addAll(EXTENSION_FILTERS);
  }

  public void chooseToLoadInWorkspace(final Workspace workspace) {
    final List<File> files = fileChooser.showOpenMultipleDialog(workspace.getStage());
    if (files != null) {
      workspace.loadImages(files);
    }
  }

  public List<File> chooseToLoadManually(final Workspace workspace) {
    return fileChooser.showOpenMultipleDialog(workspace.getStage());
  }

  public File chooseToLoadOneManually(final Workspace workspace) {
    return fileChooser.showOpenDialog(workspace.getStage());
  }
}

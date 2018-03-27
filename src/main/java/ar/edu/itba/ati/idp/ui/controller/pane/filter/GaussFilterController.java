package ar.edu.itba.ati.idp.ui.controller.pane.filter;

import ar.edu.itba.ati.idp.function.filter.Filter;
import ar.edu.itba.ati.idp.function.filter.mask.linear.GaussMask;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GaussFilterController extends VBox {
  private static final Logger LOGGER = LoggerFactory.getLogger(GaussFilterController.class);
  private static final String LAYOUT_PATH = "ui/pane/filter/gaussFilterPane.fxml";
  private static final String STAGE_TITLE = "Gauss Filter";
  private static final KeyCombination ENTER_KEY = new KeyCodeCombination(KeyCode.ENTER);

  private final Stage stage;

  @FXML
  private TextField sigmaField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public GaussFilterController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
    sigmaField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateButtonEnable(newValue)
    );
    this.stage = new Stage(StageStyle.UTILITY);
    this.stage.setScene(new Scene(this));
    this.stage.setResizable(false);
    this.stage.addEventFilter(KeyEvent.KEY_PRESSED, enterHandler());
  }

  private EventHandler<KeyEvent> enterHandler() {
    return keyEvent -> {
      if (ENTER_KEY.match(keyEvent)) {
        if (!applyButton.isDisabled()) {
          handleApply();
        }
        keyEvent.consume(); // Stop passing the event to next node
      }
    };
  }

  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  public void handleApply() {
    if (!workspace.isImageLoaded()) {
      LOGGER.warn("No image loaded on workspace. Skipping...");
      return;
    }

    final double sigma;
    try {
      sigma = Double.valueOf(sigmaField.getText());
    } catch (final NumberFormatException exception) {
      LOGGER.error("`sigma` is not a number");
      return;
    }

    workspace.applyToImage(Filter.newInstance(GaussMask.newInstance(sigma)));
  }

  public void show(final String imageName) {
    stage.setTitle(STAGE_TITLE + " - " + imageName);
    stage.show();
    // Set floating at the center-right side of the screen
    final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    this.stage.setX(bounds.getMinX() + bounds.getWidth() - this.stage.getWidth());
    this.stage.setY(bounds.getMinY() + (bounds.getHeight() - this.stage.getHeight()) / 2);
  }

  private void updateButtonEnable(final String newValue) {
    if (isValidInput(newValue)) {
      applyButton.setDisable(false);
    } else {
      applyButton.setDisable(true);
    }
  }

  private static boolean isValidInput(final String input) {
    if (input == null || input.equals("")) {
      return false;
    }

    final double value;
    try {
      value = Double.parseDouble(input);
    } catch (final NumberFormatException exception) {
      return false;
    }

    return value >= 0; // negative values make o sense
  }
}

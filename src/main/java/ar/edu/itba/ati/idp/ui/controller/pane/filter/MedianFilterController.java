package ar.edu.itba.ati.idp.ui.controller.pane.filter;

import ar.edu.itba.ati.idp.function.filter.Filter;
import ar.edu.itba.ati.idp.function.filter.mask.nonlinear.MedianMask;
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

public class MedianFilterController extends VBox {
  private static final Logger LOGGER = LoggerFactory.getLogger(MedianFilterController.class);
  private static final String LAYOUT_PATH = "ui/pane/filter/medianFilterPane.fxml";
  private static final String STAGE_TITLE = "Median Filter";
  private static final KeyCombination ENTER_KEY = new KeyCodeCombination(KeyCode.ENTER);

  private final Stage stage;

  @FXML
  private TextField widthField;

  @FXML
  private TextField heightField;

  @FXML
  private Button applyButton;

  private Workspace workspace;

  public MedianFilterController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    widthField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateField(widthField, newValue, heightField.getText())
    );

    heightField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateField(heightField, newValue, widthField.getText())
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

    final int width;
    final int height;
    try {
      width = Integer.valueOf(widthField.getText());
      height = Integer.valueOf(heightField.getText());
    } catch (final NumberFormatException exception) {
      LOGGER.error("`width` or `height` is not an integer");
      return;
    }

    workspace.applyToImage(Filter.newInstance(MedianMask.newInstance(width, height)));
  }

  public void show(final String imageName) {
    stage.setTitle(STAGE_TITLE + " - " + imageName);
    stage.show();
    // Set floating at the center-right side of the screen
    final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    this.stage.setX(bounds.getMinX() + bounds.getWidth() - this.stage.getWidth());
    this.stage.setY(bounds.getMinY() + (bounds.getHeight() - this.stage.getHeight()) / 2);
  }

  private void updateField(final TextField thisField, final String newValue,
                           final String theOtherFieldValue) {
      if (newValue.matches("[\\D]")) { // If the new value has any non-digit character
        thisField.setText(newValue.replaceAll("[\\D]", "")); // Remove them all
      } else {
        updateButtonEnable(newValue, theOtherFieldValue);
      }
  }

  private void updateButtonEnable(final String newValue, final String theOtherFieldValue) {
    if (areValidInputs(newValue, theOtherFieldValue)) {
      applyButton.setDisable(false);
    } else {
      applyButton.setDisable(true);
    }
  }

  private static boolean areValidInputs(final String... inputs) {
    for (final String input : inputs) {
      if (!isValidInput(input))
        return false;
    }
    return true;
  }

  private static boolean isValidInput(final String input) {
    if (input == null || input.equals("")) {
      return false;
    }

    final int value;
    try {
      value = Integer.parseInt(input);
    } catch (final NumberFormatException exception) {
      return false;
    }

    return value >= 2;
  }
}

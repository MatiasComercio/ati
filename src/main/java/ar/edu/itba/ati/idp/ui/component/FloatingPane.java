package ar.edu.itba.ati.idp.ui.component;

import static ar.edu.itba.ati.idp.ui.Constants.CONSTANTS;
import static ar.edu.itba.ati.idp.ui.component.CustomEvent.NEW_INVALID_INPUT;
import static ar.edu.itba.ati.idp.ui.component.CustomEvent.NEW_VALID_INPUT;

import ar.edu.itba.ati.idp.ui.controller.Workspace;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FloatingPane extends VBox implements Showable {
  private final String stageTitle;
  private final ApplyHandler applyHandler;
  private final Stage stage;
  private final List<Field> fields;
  private final Button applyButton;

  protected Workspace workspace;

  private FloatingPane(final String stageTitle, final ApplyHandler applyHandler,
                       final Field[][] fields) {
    this.stageTitle = stageTitle;
    this.applyHandler = applyHandler;
    // Own configuration.
    selfConfigure();
    // Stage configuration.
    this.stage = newStage();
    // Own elements configuration.
    final List<Node> children = getChildren();
    children.add(title(stageTitle));
    final List<Field> fieldsList = new LinkedList<>();
    for (final Field[] fieldsRow : fields) {
      final List<Field> fieldsRowList = Arrays.asList(fieldsRow);
      children.add(wrapFields(fieldsRowList));
      fieldsList.addAll(fieldsRowList);
    }
    this.fields = fieldsList;
    this.applyButton = newButton();
    children.add(this.applyButton);

    // Configure actions depending on fields events.
    this.fields.forEach(field -> {
      // If this field's input is now valid & all the other too => enable the apply button.
      field.addEventHandler(NEW_VALID_INPUT.getEventType(), event -> {
        if (this.fields.stream().allMatch(Field::isValid)) {
          this.applyButton.setDisable(false);
        }
      });

      // As this field is invalid => disable the apply button.
      field.addEventHandler(NEW_INVALID_INPUT.getEventType(), event -> this.applyButton.setDisable(true));
    });

    // This is just to configure the button enabled/disabled
    if (this.fields.isEmpty()) {
      applyButton.setDisable(false); // No input to validate
    } else { // Only enabled if all valid => simulate a text change
      final Field aField = this.fields.get(0);
      //noinspection unchecked
      aField.setValue(aField.getValue() == null ? "" : aField.getValue());
    }
  }

  public static FloatingPane newInstance(final String stageTitle, final ApplyHandler applyHandler,
                                         final Field... fields) {
    return new FloatingPane(stageTitle, applyHandler, new Field[][] {fields});
  }

  public static FloatingPane newInstance(final String stageTitle, final ApplyHandler applyHandler,
                                         final Field[]... fields) {
    return new FloatingPane(stageTitle, applyHandler, fields);
  }

  private void selfConfigure() {
    setAlignment(Pos.CENTER);
    setSpacing(CONSTANTS.getSpacing());
    setPadding(new Insets(CONSTANTS.getPadding()));
  }

  private Stage newStage() {
    final Stage stage = new Stage(StageStyle.UTILITY);
    stage.setScene(new Scene(this));
    stage.setResizable(false);
    stage.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    return stage;
  }

  private void handleKeyPressed(final KeyEvent keyEvent) {
    if (CONSTANTS.getEnterKey().match(keyEvent)) {
      if (!applyButton.isDisabled()) {
        handleApply(keyEvent);
      }
      keyEvent.consume(); // Stop passing the event to next node
    }
  }

  private Node title(final String text) {
    final Label title = new Label(text);
    title.setFont(Font.font(CONSTANTS.getFontFamily(), CONSTANTS.getFontSize()));
    return title;
  }

  private Node wrapFields(final List<Field> fields) {
    final HBox hBox = new HBox(CONSTANTS.getSpacing());
    hBox.setAlignment(Pos.CENTER);
    hBox.getChildren().addAll(fields);
    return hBox;
  }

  private Button newButton() {
    final Button applyButton = new Button(CONSTANTS.getApplyButtonText());
    applyButton.setOnAction(this::handleApply);
    return applyButton;
  }

  // Suppressed so as to be able to use the lambda notation.
  public void handleApply(@SuppressWarnings("unused") final Event event) {
    workspace.getOpImageFile().ifPresent(imageFile -> applyHandler.handle(workspace, imageFile));
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    this.workspace = workspace;
  }

  @Override
  public void show(final String imageName) {
    stage.setTitle(stageTitle + " - " + imageName);
    stage.show(); // This should be before positioning the floating window.
    // Set floating at the center-right side of the screen
    final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    stage.setX(bounds.getMinX() + bounds.getWidth() - this.stage.getWidth());
    stage.setY(bounds.getMinY() + (bounds.getHeight() - this.stage.getHeight()) / 2);
  }
}

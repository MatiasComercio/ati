package ar.edu.itba.ati.idp.ui.component;

import static ar.edu.itba.ati.idp.ui.Constants.CONSTANTS;

import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Field<T> extends VBox {
  private final TextField textField;
  private final InputExtractor<T> inputExtractor;

  private Field(final Label label, final TextField textField, final InputExtractor<T> inputExtractor) {
    this.textField = textField;
    this.inputExtractor = inputExtractor;
    // Own configuration.
    selfConfigure();
    // Children configuration.
    final List<Node> children = getChildren();
    children.add(label);
    children.add(textField);
    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (inputExtractor.isValid(newValue)) {
        fireEvent(CustomEvent.NEW_VALID_INPUT.getEvent());
      } else {
        fireEvent(CustomEvent.NEW_INVALID_INPUT.getEvent());
      }
    });
  }

  public static <T> Field<T> newInstance(final String labelText, final String promptText,
                                         final InputExtractor<T> inputExtractor) {
    return new Field<>(newLabel(labelText), newTextField(promptText), inputExtractor);
  }

  private static Label newLabel(final String labelText) {
    final Label label = new Label(labelText);
    label.setAlignment(Pos.CENTER);
    return label;
  }

  private static TextField newTextField(final String promptText) {
    final TextField textField = new TextField();
    textField.setAlignment(Pos.CENTER);
    textField.setPrefWidth(CONSTANTS.getTextFieldPrefWidth());
    textField.setPromptText(promptText);
    return textField;
  }

  private void selfConfigure() {
    setAlignment(Pos.CENTER);
    setSpacing(CONSTANTS.getSpacing());
  }

  public T getValue() {
    return inputExtractor.getValue(textField.getText());
  }

  public boolean isValid() {
    return getValue() != null;
  }
}

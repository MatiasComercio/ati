package ar.edu.itba.ati.idp.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public enum Constants {
  CONSTANTS;

  private static final KeyCombination ENTER_KEY = new KeyCodeCombination(KeyCode.ENTER);
  private static final String APPLY_BUTTON_TEXT = "Apply";
  private static final int PADDING = 20;
  private static final String FONT_FAMILY = "System Bold";
  private static final double FONT_SIZE = 13d;
  private static final double SPACING = 10d;
  private static final double TEXT_FIELD_PREF_WIDTH = 100d;

  public KeyCombination getEnterKey() {
    return ENTER_KEY;
  }

  public String getApplyButtonText() {
    return APPLY_BUTTON_TEXT;
  }

  public int getPadding() {
    return PADDING;
  }

  public String getFontFamily() {
    return FONT_FAMILY;
  }

  public double getFontSize() {
    return FONT_SIZE;
  }

  public double getSpacing() {
    return SPACING;
  }

  public double getTextFieldPrefWidth() {
    return TEXT_FIELD_PREF_WIDTH;
  }
}

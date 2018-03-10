package ar.edu.itba.ati.idp.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class UnsavedChangesAlert extends Alert {

  public UnsavedChangesAlert(final String title, final String content) {
    super(AlertType.CONFIRMATION);
    this.setTitle(title);
    this.setHeaderText(null);
    this.setContentText(content);
    this.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
  }
}

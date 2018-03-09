package ar.edu.itba.ati.idp.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class UnsavedChangesOpenAlert extends Alert {

  public UnsavedChangesOpenAlert() {
    super(AlertType.CONFIRMATION);
    this.setTitle("You have unsaved changes");
    this.setHeaderText(null);
    this.setContentText("Do you want to save the current image before opening the new one?");
    this.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
  }
}

package ar.edu.itba.ati.idp.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class UnsavedChangesCloseAlert extends Alert {

  public UnsavedChangesCloseAlert() {
    super(AlertType.CONFIRMATION);
    this.setTitle("You have unsaved changes");
    this.setHeaderText(null);
    this.setContentText("Do you want to save the current image before closing it?");
    this.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
  }
}

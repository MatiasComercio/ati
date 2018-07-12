package ar.edu.itba.ati.idp;

import ar.edu.itba.ati.idp.ui.controller.Workspace;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
  public static void main(String[] args) {
    // Initialize OpenCV Library
    nu.pattern.OpenCV.loadLocally();
    // Launch javafx application
    launch(args);
  }

  @Override
  public void start(final Stage mainStage) {
    Workspace.newInstance();
  }
}

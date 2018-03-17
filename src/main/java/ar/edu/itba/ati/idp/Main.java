package ar.edu.itba.ati.idp;

import ar.edu.itba.ati.idp.model.Workspace;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage mainStage) {
    Workspace.newInstance();
  }
}

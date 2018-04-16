package ar.edu.itba.ati.idp.ui.component;

import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.ui.controller.Workspace;

@FunctionalInterface
public interface ApplyHandler {
  void handle(Workspace workspace, ImageFile imageFile);
}

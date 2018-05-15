package ar.edu.itba.ati.idp.ui.controller;

import ar.edu.itba.ati.idp.ui.SelectableAreaImageView;
import java.util.Optional;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

public class ImagePaneController extends ScrollPane {

  private final SelectableAreaImageView selectableAreaImageView;

  private ImageView imageView;

  @SuppressWarnings("WeakerAccess")
  public ImagePaneController() {
    this.imageView = new ImageView();
    this.selectableAreaImageView = new SelectableAreaImageView(imageView);
    this.selectableAreaImageView.enableSelection();
    final BorderPane borderPane = new BorderPane(this.selectableAreaImageView);
    this.setContent(borderPane);
    this.setFitToWidth(true);
    this.setFitToHeight(true);
  }

  public void loadImage(final Image image) {
    this.selectableAreaImageView.clearSelection(); // Just to reset all selections, if any
    this.imageView.setImage(image);
  }

  public void unloadImage() {
    this.selectableAreaImageView.clearSelection();
    this.imageView.setImage(null);
  }

  public Optional<Rectangle> getSelectedArea() {
    return selectableAreaImageView.getSelectionRectangle();
  }
}

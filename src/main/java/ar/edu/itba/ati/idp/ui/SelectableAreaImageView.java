package ar.edu.itba.ati.idp.ui;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class SelectableAreaImageView extends Group {

  private static final Paint OUTSIDE_SELECTION_COLOR = Color.color(0, 0, 0, 0.5);

  private final ImageView imageView;

  private final EventHandler<MouseEvent> onMousePressedEventHandler;
  private final EventHandler<MouseEvent> onMouseDraggedEventHandler;

  private ResizableRectangle selectionRectangle;
  private Collection<Rectangle> outsideSelectionRectangles;

  public SelectableAreaImageView(final ImageView imageView) {
    super(imageView);

    this.imageView = requireNonNull(imageView);
    this.onMousePressedEventHandler = buildOnMousePressedEventHandler();
    this.onMouseDraggedEventHandler = buildOnMouseDraggedEventHandler();
  }

  public void enableSelection() {
    imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
  }

  public void disableSelection() {
    imageView.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    imageView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
  }

  public void clearSelection() {
    if (selectionRectangle == null || outsideSelectionRectangles == null) {
      return;
    }

    getChildren().removeAll(outsideSelectionRectangles);
    getChildren().remove(selectionRectangle);
    getChildren().removeAll(selectionRectangle.getSideResizers());
    getChildren().removeAll(selectionRectangle.getCornerResizers());

    selectionRectangle = null;
    outsideSelectionRectangles = null;
  }

  public Optional<Rectangle> getSelectionRectangle() {
    return Optional.ofNullable(selectionRectangle);
  }

  private EventHandler<MouseEvent> buildOnMousePressedEventHandler() {
    return event -> {
      if (event.isSecondaryButtonDown()) {
        return;
      }

      if (selectionRectangle != null || outsideSelectionRectangles != null) {
        clearSelection();
        return;
      }

      selectionRectangle = new ResizableRectangle(event.getX(), event.getY(), 0, 0);
      outsideSelectionRectangles = buildOutsideSelectionRectangles(selectionRectangle);

      setPickOnBounds(true);
      getChildren().addAll(outsideSelectionRectangles);
      getChildren().add(selectionRectangle);
      getChildren().addAll(selectionRectangle.getSideResizers());
      getChildren().addAll(selectionRectangle.getCornerResizers());
    };
  }

  private EventHandler<MouseEvent> buildOnMouseDraggedEventHandler() {
    return event -> {
      if (event.isSecondaryButtonDown()) {
        return;
      }

      if (selectionRectangle == null || outsideSelectionRectangles == null) {
        return;
      }

      final double offsetX = event.getX() - selectionRectangle.getX();
      final double offsetY = event.getY() - selectionRectangle.getY();

      if (offsetX > 0) {
        if (event.getX() > imageView.getImage().getWidth()) {
          selectionRectangle.setWidth(imageView.getImage().getWidth() - selectionRectangle.getX());
        } else {
          selectionRectangle.setWidth(offsetX);
        }
      } else {
        final double oldX = selectionRectangle.getX();

        if (event.getX() < 0) {
          selectionRectangle.setX(0);
        } else {
          selectionRectangle.setX(event.getX());
        }

        selectionRectangle.setWidth(oldX - selectionRectangle.getX());
      }

      if (offsetY > 0) {
        if (event.getY() > imageView.getImage().getHeight()) {
          selectionRectangle
              .setHeight(imageView.getImage().getHeight() - selectionRectangle.getY());
        } else {
          selectionRectangle.setHeight(offsetY);
        }
      } else {
        final double oldY = selectionRectangle.getY();

        if (event.getY() < 0) {
          selectionRectangle.setY(0);
        } else {
          selectionRectangle.setY(event.getY());
        }

        selectionRectangle.setHeight(oldY - selectionRectangle.getY());
      }
    };
  }

  private Collection<Rectangle> buildOutsideSelectionRectangles(final Rectangle rectangle) {
    final Rectangle darkAreaTop = new Rectangle(0, 0, OUTSIDE_SELECTION_COLOR);
    final Rectangle darkAreaLeft = new Rectangle(0, 0, OUTSIDE_SELECTION_COLOR);
    final Rectangle darkAreaRight = new Rectangle(0, 0, OUTSIDE_SELECTION_COLOR);
    final Rectangle darkAreaBottom = new Rectangle(0, 0, OUTSIDE_SELECTION_COLOR);

    darkAreaTop.widthProperty().bind(imageView.getImage().widthProperty());
    darkAreaTop.heightProperty().bind(rectangle.yProperty());

    darkAreaLeft.yProperty().bind(rectangle.yProperty());
    darkAreaLeft.widthProperty().bind(rectangle.xProperty());
    darkAreaLeft.heightProperty().bind(rectangle.heightProperty());

    darkAreaRight.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty()));
    darkAreaRight.yProperty().bind(rectangle.yProperty());
    darkAreaRight.widthProperty()
        .bind(imageView.getImage().widthProperty()
            .subtract(rectangle.xProperty().add(rectangle.widthProperty())));
    darkAreaRight.heightProperty().bind(rectangle.heightProperty());

    darkAreaBottom.yProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()));
    darkAreaBottom.widthProperty().bind(imageView.getImage().widthProperty());
    darkAreaBottom.heightProperty().bind(
        imageView.getImage().heightProperty()
            .subtract(rectangle.yProperty().add(rectangle.heightProperty())));

    darkAreaTop.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    darkAreaTop.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);

    darkAreaLeft.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    darkAreaLeft.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);

    darkAreaRight.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    darkAreaRight.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);

    darkAreaBottom.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    darkAreaBottom.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);

    return Arrays.asList(darkAreaTop, darkAreaLeft, darkAreaBottom, darkAreaRight);
  }
}

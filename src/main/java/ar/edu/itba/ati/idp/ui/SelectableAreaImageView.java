package ar.edu.itba.ati.idp.ui;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
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

  private double rectangleStartX;
  private double rectangleStartY;

  public SelectableAreaImageView(final ImageView imageView) {
    super(imageView);

    this.imageView = requireNonNull(imageView);
    this.onMousePressedEventHandler = buildOnMousePressedEventHandler();
    this.onMouseDraggedEventHandler = buildOnMouseDraggedEventHandler();
  }

  private EventHandler<MouseEvent> buildOnMousePressedEventHandler() {
    return event -> {
      if (event.isSecondaryButtonDown()) {
        return;
      }

      // If there is a click and there are any rectangles => clear selection
      if (selectionRectangle != null || outsideSelectionRectangles != null) {
        clearSelection();
        return;
      }

      // Else, create all rectangles with the current click/mouse position
      rectangleStartX = event.getX();
      rectangleStartY = event.getY();

      selectionRectangle = new ResizableRectangle(event.getX(), event.getY(), 0, 0);
      outsideSelectionRectangles = buildOutsideSelectionRectangles(selectionRectangle);

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

      // This may happen is mouse is clicked outside this element's scope and dragged in while pressed
      if (selectionRectangle == null || outsideSelectionRectangles == null) {
        return;
      }

      // Note that the mouse should be still pressed
      // (initial pressed event handled by the previous event handler) to reach here correctly
      final double offsetX = event.getX() - rectangleStartX;
      final double offsetY = event.getY() - rectangleStartY;

      if (offsetX > 0) {
        // TODO: set with Math.min(offset, imageView.getImage().getWidth() - rectangleStartX)
        if (event.getX() > imageView.getImage().getWidth()) {
          selectionRectangle.setWidth(imageView.getImage().getWidth() - rectangleStartX);
        } else {
          selectionRectangle.setWidth(offsetX);
        }
      } else {
        // TODO: set with Math.max(0, event.getX())
        if (event.getX() < 0) {
          selectionRectangle.setX(0);
        } else {
          selectionRectangle.setX(event.getX());
        }

        selectionRectangle.setWidth(rectangleStartX - selectionRectangle.getX());
      }

      // TODO: same as before
      if (offsetY > 0) {
        if (event.getY() > imageView.getImage().getHeight()) {
          selectionRectangle
              .setHeight(imageView.getImage().getHeight() - rectangleStartY);
        } else {
          selectionRectangle.setHeight(offsetY);
        }
      } else {
        if (event.getY() < 0) {
          selectionRectangle.setY(0);
        } else {
          selectionRectangle.setY(event.getY());
        }

        selectionRectangle.setHeight(rectangleStartY - selectionRectangle.getY());
      }
    };
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

  /**
   * <pre>
   * -------------------------
   * |          top          |
   * |_______________________|
   * | left |rectangle| right|
   * |______|_________|______|
   * |         bottom        |
   * -------------------------
   *
   * The ImageView is the outer rectangle.
   * The inner rectangle is the one given as argument.
   * top, left, right, bottom rectangles are the "outside selection rectangles" returned.
   *
   * All outside rectangles have their sizes bound
   *   (i.e., changing dynamically according) to the selection rectangle size.
   *
   * These outside rectangles are also attached the mouse event handlers
   * </pre>
   */
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

    addSelectionMouseHandlers(darkAreaTop);
    addSelectionMouseHandlers(darkAreaLeft);
    addSelectionMouseHandlers(darkAreaRight);
    addSelectionMouseHandlers(darkAreaBottom);

    return Arrays.asList(darkAreaTop, darkAreaLeft, darkAreaBottom, darkAreaRight);
  }

  private void addSelectionMouseHandlers(final Node node) {
    node.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    node.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
  }

  public void enableSelection() {
    addSelectionMouseHandlers(imageView);
  }

  public void disableSelection() {
    clearSelection();
    removeSelectionMouseHandlers(imageView);
  }

  private void removeSelectionMouseHandlers(final Node node) {
    node.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
    node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
  }

  // TODO: Add IntRectangle2D?
  public Optional<Rectangle2D> getSelectionRectangle() {
    if (selectionRectangle == null) {
      return Optional.empty();
    }

    return Optional.of(new Rectangle2D(selectionRectangle.getX(), selectionRectangle.getY(),
        selectionRectangle.getWidth(), selectionRectangle.getHeight()));
  }
}

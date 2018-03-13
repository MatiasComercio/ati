package ar.edu.itba.ati.idp.ui;

import java.util.Arrays;
import java.util.Collection;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ResizableRectangle extends Rectangle {

  private static final Paint STROKE_COLOR = Color.RED;
  private static final int STROKE_WIDTH = 1;

  private static final double RESIZER_SIDE = 4;
  private static final double RESIZE_MIN_X = 10;
  private static final double RESIZE_MIN_Y = 10;

  private double maxX = Double.MAX_VALUE;
  private double maxY = Double.MAX_VALUE;

  private double lastClickX;
  private double lastClickY;

  private final Collection<? extends Shape> sideResizers;
  private final Collection<? extends Shape> cornerResizers;

  public ResizableRectangle(final double x, final double y, final double width,
      final double height) {
    super(x, y, width, height);
    setStroke(STROKE_COLOR);
    setStrokeWidth(STROKE_WIDTH);
    setFill(Color.TRANSPARENT);
    addMoveHandlers(this);

    final RectangleResizers resizers = new RectangleResizers(this, RESIZER_SIDE,
        RESIZE_MIN_X, RESIZE_MIN_Y);

    this.sideResizers = resizers.getSideResizers();
    this.cornerResizers = resizers.getCornerResizers();
  }

  public ResizableRectangle(final double x, final double y, final double width,
      final double height, final Paint strokeColor) {
    this(x, y, width, height);
    setStroke(strokeColor);
  }

  public Collection<? extends Shape> getSideResizers() {
    return sideResizers;
  }

  public Collection<? extends Shape> getCornerResizers() {
    return cornerResizers;
  }

  public void setMaxX(final double maxX) {
    this.maxX = maxX;
  }

  public void setMaxY(final double maxY) {
    this.maxY = maxY;
  }

  private void addMoveHandlers(final Rectangle moveRect) {
    moveRect.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
        moveRect.getParent().setCursor(Cursor.HAND));

    moveRect.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      moveRect.getParent().setCursor(Cursor.MOVE);
      lastClickX = event.getX();
      lastClickY = event.getY();
    });

    moveRect.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
        moveRect.getParent().setCursor(Cursor.HAND));

    moveRect.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
        moveRect.getParent().setCursor(Cursor.DEFAULT));

    moveRect.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
      final double offsetX = event.getX() - lastClickX;
      final double offsetY = event.getY() - lastClickY;
      final double newX = getX() + offsetX;
      final double newY = getY() + offsetY;

      if (isValidX(this, newX)) {
        setX(newX);
      }

      if (isValidY(this, newY)) {
        setY(newY);
      }

      lastClickX = event.getX();
      lastClickY = event.getY();
    });
  }

  private static boolean isValidX(final Rectangle rectangle, final double x) {
    return x >= rectangle.getParent().getLayoutBounds().getMinX()
        && x + rectangle.getWidth() <= rectangle.getParent().getLayoutBounds().getWidth();
  }

  private static boolean isValidY(final Rectangle rectangle, final double y) {
    return y >= rectangle.getParent().getLayoutBounds().getMinY()
        && y + rectangle.getHeight() <= rectangle.getParent().getLayoutBounds().getHeight();
  }

  private static boolean isValidWidth(final Rectangle rectangle, final double width) {
    return rectangle.getX() + width <= rectangle.getParent().getLayoutBounds().getWidth();
  }

  private static boolean isValidHeight(final Rectangle rectangle, final double height) {
    return rectangle.getY() + height <= rectangle.getParent().getLayoutBounds().getHeight();
  }

  private static final class RectangleResizers {

    private final Rectangle rectangle;

    private final DoubleBinding leftXBinding;
    private final DoubleBinding middleXBinding;
    private final DoubleBinding rightXBinding;
    private final DoubleBinding topYBinding;
    private final DoubleBinding middleYBinding;
    private final DoubleBinding bottomYBinding;
    private final double resizerSide;
    private final double resizeMinX;
    private final double resizeMinY;

    private final Collection<Rectangle> sideRectangles;
    private final Collection<Rectangle> cornerRectangles;

    public RectangleResizers(final Rectangle rectangle, final double resizerSide,
        final double resizeMinX, final double resizeMinY) {
      this.rectangle = rectangle;
      this.resizerSide = resizerSide;
      this.resizeMinX = resizeMinX;
      this.resizeMinY = resizeMinY;
      this.leftXBinding = Bindings.selectDouble(rectangle.xProperty());
      this.rightXBinding = this.leftXBinding.add(rectangle.widthProperty())
          .subtract(RESIZER_SIDE);
      this.middleXBinding = this.leftXBinding.add(rectangle.widthProperty().divide(2.0))
          .subtract(RESIZER_SIDE / 2.0);
      this.topYBinding = Bindings.selectDouble(rectangle.yProperty());
      this.bottomYBinding = this.topYBinding.add(rectangle.heightProperty())
          .subtract(RESIZER_SIDE);
      this.middleYBinding = this.topYBinding.add(rectangle.heightProperty().divide(2.0))
          .subtract(RESIZER_SIDE / 2.0);
      this.sideRectangles = Arrays
          .asList(buildNResizeRectangle(), buildEResizeRectangle(), buildSResizeRectangle(),
              buildWResizeRectangle());
      this.cornerRectangles = Arrays
          .asList(buildNWResizeRectangle(), buildNEResizeRectangle(), buildSWResizeRectangle(),
              buildSEResizeRectangle());
    }

    public Collection<Rectangle> getSideResizers() {
      return sideRectangles;
    }

    public Collection<Rectangle> getCornerResizers() {
      return cornerRectangles;
    }

    private Rectangle buildNWResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(leftXBinding, topYBinding,
          Cursor.NW_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetX = event.getX() - rectangle.getX();
        final double offsetY = event.getY() - rectangle.getY();
        final double newX = rectangle.getX() + offsetX;
        final double newY = rectangle.getY() + offsetY;

        if (newX >= 0 && newX <= rectangle.getX() + rectangle.getWidth() - resizeMinX) {
          rectangle.setX(newX);
          rectangle.setWidth(rectangle.getWidth() - offsetX);
        }

        if (newY >= 0 && newY <= rectangle.getY() + rectangle.getHeight() - resizeMinY) {
          rectangle.setY(newY);
          rectangle.setHeight(rectangle.getHeight() - offsetY);
        }
      });

      return resizeRectangle;
    }

    private Rectangle buildNEResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(rightXBinding, topYBinding,
          Cursor.NE_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetX = event.getX() - rectangle.getX();
        final double offsetY = event.getY() - rectangle.getY();
        final double newY = rectangle.getY() + offsetY;

        if (offsetX >= 0 && offsetX >= resizeMinX) {
          rectangle.setWidth(offsetX);
        }

        if (newY >= 0 && newY <= rectangle.getY() + rectangle.getHeight() - resizeMinY) {
          rectangle.setY(newY);
          rectangle.setHeight(rectangle.getHeight() - offsetY);
        }
      });

      return resizeRectangle;
    }

    private Rectangle buildSWResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(leftXBinding, bottomYBinding,
          Cursor.SW_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetX = event.getX() - rectangle.getX();
        final double offsetY = event.getY() - rectangle.getY();
        final double newX = rectangle.getX() + offsetX;

        if (newX >= 0 && newX <= rectangle.getX() + rectangle.getWidth() - resizeMinX) {
          rectangle.setX(newX);
          rectangle.setWidth(rectangle.getWidth() - offsetX);
        }

        if (offsetY >= 0 && offsetY <= rectangle.getY() + rectangle.getHeight() + resizeMinY) {
          rectangle.setHeight(offsetY);
        }
      });

      return resizeRectangle;
    }

    private Rectangle buildSEResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(rightXBinding, bottomYBinding,
          Cursor.SE_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetX = event.getX() - rectangle.getX();
        final double offsetY = event.getY() - rectangle.getY();

        if (offsetX >= 0 && offsetX <= rectangle.getX() + rectangle.getWidth() + resizeMinX) {
          rectangle.setWidth(offsetX);
        }

        if (offsetY >= 0 && offsetY <= rectangle.getY() + rectangle.getHeight() + resizeMinY) {
          rectangle.setHeight(offsetY);
        }
      });

      return resizeRectangle;
    }

    private Rectangle buildNResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(middleXBinding, topYBinding,
          Cursor.N_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetY = event.getY() - rectangle.getY();
        final double newY = rectangle.getY() + offsetY;

        if (newY >= 0 && newY <= rectangle.getY() + rectangle.getHeight()) {
          rectangle.setY(newY);
          rectangle.setHeight(rectangle.getHeight() - offsetY);
        }
      });

      return resizeRectangle;
    }

    private Rectangle buildEResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(rightXBinding, middleYBinding,
          Cursor.E_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetX = event.getX() - rectangle.getX();

        if (offsetX >= 0 && offsetX <= rectangle.getX() + rectangle.getWidth() - 5) {
          rectangle.setWidth(offsetX);
        }
      });

      return resizeRectangle;
    }

    private Rectangle buildSResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(middleXBinding, bottomYBinding,
          Cursor.S_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetY = event.getY() - rectangle.getY();

        if (offsetY >= 0 && offsetY <= rectangle.getY() + rectangle.getHeight() - 5) {
          rectangle.setHeight(offsetY);
        }

      });

      return resizeRectangle;
    }

    private Rectangle buildWResizeRectangle() {
      final Rectangle resizeRectangle = buildResizeRectangle(leftXBinding, middleYBinding,
          Cursor.W_RESIZE);

      resizeRectangle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        final double offsetX = event.getX() - rectangle.getX();
        final double newX = rectangle.getX() + offsetX;

        if (newX >= 0 && newX <= rectangle.getX() + rectangle.getWidth() - 5) {
          rectangle.setX(newX);
          rectangle.setWidth(rectangle.getWidth() - offsetX);
        }
      });

      return resizeRectangle;
    }

    private Rectangle buildResizeRectangle(final DoubleBinding xBinding,
        final DoubleBinding yBinding, final Cursor overCursor) {
      final Rectangle resizeRect = new Rectangle(resizerSide, resizerSide);

      resizeRect.fillProperty().bind(rectangle.strokeProperty());

      resizeRect.xProperty().bind(xBinding);
      resizeRect.yProperty().bind(yBinding);

      resizeRect.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
          resizeRect.getParent().setCursor(overCursor));

      resizeRect.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
          resizeRect.getParent().setCursor(Cursor.DEFAULT));

      return resizeRect;
    }
  }
}

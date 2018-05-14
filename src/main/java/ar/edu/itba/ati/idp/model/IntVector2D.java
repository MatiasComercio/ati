package ar.edu.itba.ati.idp.model;

public class IntVector2D {

  private final int x;
  private final int y;

  private IntVector2D(final int x, final int y) {
    this.x = x;
    this.y = y;
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public static IntVector2D of(final int x, final int y) {
    return new IntVector2D(x, y);
  }
}

package ar.edu.itba.ati.idp.utils;

public abstract class Doubles {
  private static final double DEFAULT_DELTA = 1e-10;

  public static boolean equal(final double d1, final double d2) {
    return equal(d1, d2, DEFAULT_DELTA);
  }

  public static boolean equal(final double d1, final double d2, final double delta) {
    return Double.compare(d1, d2) == 0 || (Math.abs(d1 - d2) <= delta);
  }
}

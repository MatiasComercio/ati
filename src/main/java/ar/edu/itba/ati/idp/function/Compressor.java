package ar.edu.itba.ati.idp.function;

public enum Compressor implements UniquePixelsBandOperator {
  INSTANCE;

  @Override
  public double[][] apply(final double[][] m) {
    /*
     * return the same matrix; the only intended effect of this is to compress the bands, which is
     * already achieved by getting this method called.
     */
    return m;
  }
}

package ar.edu.itba.ati.idp.function.filter.mask.nonlinear;

public final class MedianMask extends WeightedMedianMask {
  private static final WeightCalculator WEIGHT_CALCULATOR = (maskX, maskY) -> 1;

  private MedianMask(final int width, final int height) {
    super(width, height, WEIGHT_CALCULATOR);
  }

  public static MedianMask newInstance(final int width, final int height) {
    return new MedianMask(width, height);
  }

  // This method is declared so as to override the static inherited `newInstance` method.
  public static MedianMask newInstance(final int width, final int height,
                                       @SuppressWarnings("unused") final WeightCalculator ignored) {
    return new MedianMask(width, height);
  }
}

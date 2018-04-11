package ar.edu.itba.ati.idp.function.filter.mask.linear;

public class PrewittMask extends AbstractLinearRotatableMask<PrewittMask> {
  private static final double[][] BASE_MASK = {
      { -1, 0, 1 },
      { -1, 0, 1 },
      { -1, 0, 1 },
  };

  private PrewittMask(final double[][] mask) {
    super(mask);
  }

  public static PrewittMask newInstance() {
    return new PrewittMask(BASE_MASK);
  }

  @Override
  protected PrewittMask newInstance(final double[][] mask) {
    return new PrewittMask(mask);
  }
}

package ar.edu.itba.ati.idp.function.filter.mask.linear;

public class SobelMask extends AbstractLinearRotatableMask<SobelMask> {
  private static final double[][] BASE_MASK = {
      { -1, 0, 1},
      { -2, 0, 2},
      { -1, 0, 1},
  };

  private SobelMask(final double[][] mask) {
    super(mask);
  }

  public static SobelMask newInstance() {
    return new SobelMask(BASE_MASK);
  }

  @Override
  protected SobelMask newInstance(final double[][] mask) {
    return new SobelMask(mask);
  }
}

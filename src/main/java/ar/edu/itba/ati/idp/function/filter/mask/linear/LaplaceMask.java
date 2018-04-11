package ar.edu.itba.ati.idp.function.filter.mask.linear;

public class LaplaceMask extends AbstractLinearMask {
  private static LaplaceMask instance;

  private static final double[][] BASE_MASK = new double[][] {
      {  0, -1,  0 },
      { -1,  4, -1 },
      {  0, -1,  0 },
  };

  private LaplaceMask() {
    super(BASE_MASK[0].length, BASE_MASK.length);
  }

  @Override
  protected void initializeMask() {
    this.mask = BASE_MASK;
  }

  public static LaplaceMask getInstance() {
    if (instance == null) {
       instance = new LaplaceMask();
    }
    return instance;
  }

  /* testing-only */ double[][] getMask() {
    return mask;
  }
}

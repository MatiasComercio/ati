package ar.edu.itba.ati.idp.function.filter.mask.linear;

public class UnnamedTP2AMask extends AbstractLinearRotatableMask<UnnamedTP2AMask> {
  private static final double[][] BASE_MASK = {
      {  1,  1,  1 },
      {  1, -2,  1 },
      { -1, -1, -1 },
  };

  private UnnamedTP2AMask(final double[][] mask) {
    super(mask);
  }

  public static UnnamedTP2AMask newInstance() {
    return new UnnamedTP2AMask(BASE_MASK);
  }

  @Override
  protected UnnamedTP2AMask newInstance(final double[][] mask) {
    return new UnnamedTP2AMask(mask);
  }
}

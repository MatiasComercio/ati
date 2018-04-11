package ar.edu.itba.ati.idp.function.filter.mask.linear;

public class UnknownTP2AMask extends AbstractLinearRotatableMask<UnknownTP2AMask> {
  private static final double[][] BASE_MASK = {
      {  1,  1,  1 },
      {  1, -2,  1 },
      { -1, -1, -1 },
  };

  private UnknownTP2AMask(final double[][] mask) {
    super(mask);
  }

  public static UnknownTP2AMask newInstance() {
    return new UnknownTP2AMask(BASE_MASK);
  }

  @Override
  protected UnknownTP2AMask newInstance(final double[][] mask) {
    return new UnknownTP2AMask(mask);
  }
}

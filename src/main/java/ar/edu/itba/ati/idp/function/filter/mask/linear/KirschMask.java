package ar.edu.itba.ati.idp.function.filter.mask.linear;

public class KirschMask extends AbstractLinearRotatableMask<KirschMask> {
  private static final double[][] BASE_MASK = {
      {  5,  5,  5 },
      { -3,  0, -3 },
      { -3, -3, -3 },
  };
  
  private KirschMask(final double[][] mask) {
    super(mask);
  }

  public static KirschMask newInstance() {
    return new KirschMask(BASE_MASK);
  }
  
  @Override
  protected KirschMask newInstance(final double[][] mask) {
    return new KirschMask(mask);
  }
}

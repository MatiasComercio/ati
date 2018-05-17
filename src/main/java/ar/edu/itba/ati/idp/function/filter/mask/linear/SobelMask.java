package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static ar.edu.itba.ati.idp.utils.MatrixRotator.Degree.D180;
import static ar.edu.itba.ati.idp.utils.MatrixRotator.Degree.D270;
import static ar.edu.itba.ati.idp.utils.MatrixRotator.Degree.D90;

public class SobelMask extends AbstractLinearRotatableMask<SobelMask> {
  // Note that the derivative direction is `right` in this default case.
  private static final double[][] BASE_MASK = {
      { -1, 0, 1},
      { -2, 0, 2},
      { -1, 0, 1},
  };

  private static final SobelMask RIGHT_MASK = newInstance();

  private SobelMask(final double[][] mask) {
    super(mask);
  }

  @Deprecated // In favour of explicitly requiring the direction.
  public static SobelMask newInstance() {
    return new SobelMask(BASE_MASK);
  }

  @SuppressWarnings("unused")
  public static SobelMask newInstanceLeft() {
    return RIGHT_MASK.rotate(D180);
  }

  @SuppressWarnings("unused")
  public static SobelMask newInstanceRight() {
    return RIGHT_MASK;
  }

  @SuppressWarnings("unused")
  public static SobelMask newInstanceUp() {
    return RIGHT_MASK.rotate(D270);
  }

  @SuppressWarnings("unused")
  public static SobelMask newInstanceDown() {
    return RIGHT_MASK.rotate(D90);
  }

  @Override
  protected SobelMask newInstance(final double[][] mask) {
    return new SobelMask(mask);
  }
}

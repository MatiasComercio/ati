package ar.edu.itba.ati.idp.function.border;

import static ar.edu.itba.ati.idp.utils.MatrixSlopesProcessors.REAL_SLOPES_PROCESSOR;
import static ar.edu.itba.ati.idp.utils.MatrixSlopesProcessors.THRESHOLD_SLOPES_PROCESSOR;

import ar.edu.itba.ati.idp.function.filter.ZeroCrossesFilter;
import ar.edu.itba.ati.idp.function.filter.mask.linear.LaplaceMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.LoGMask;

public class ZeroCrossesBorderDetector {
  private static final ZeroCrossesFilter LAPLACE = ZeroCrossesFilter.newInstance(LaplaceMask.getInstance(), THRESHOLD_SLOPES_PROCESSOR);
  private static final ZeroCrossesFilter LAPLACE_SLOPES = ZeroCrossesFilter.newInstance(LaplaceMask.getInstance(), REAL_SLOPES_PROCESSOR);

  public static ZeroCrossesFilter getLaplace() {
    return LAPLACE;
  }

  public static ZeroCrossesFilter getLaplaceSlopes() {
    return LAPLACE_SLOPES;
  }

  public static ZeroCrossesFilter newLoG(final Double sigma) {
    return ZeroCrossesFilter.newInstance(LoGMask.newInstance(sigma), REAL_SLOPES_PROCESSOR);
  }
}

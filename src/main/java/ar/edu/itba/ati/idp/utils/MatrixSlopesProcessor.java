package ar.edu.itba.ati.idp.utils;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;

public interface MatrixSlopesProcessor extends DoubleArray2DUnaryOperator {

  /**
   * Search slopes at the given {@code matrix}
   * and return a matrix whose values represent the result of the search for each position.
   * <p>
   * A slope is zero cross or sign change from one cell to its consecutive cell
   * or the next one if the consecutive is zero.
   * <p>
   * Slopes are search first in left -> right disposition (i.e., going along each row),
   * and then in top -> down disposition (i.e., going along each column).
   * <p>
   * The returned matrix values (that represent the slope for each position)
   * are implementation-dependent.
   *
   * @param matrix The matrix where to search the zero-crosses.
   * @return The matrix whose values represent the result of the search.
   */
  @Override
  double[][] apply(final double[][] matrix);
}

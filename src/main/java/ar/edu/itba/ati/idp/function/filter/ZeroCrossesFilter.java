package ar.edu.itba.ati.idp.function.filter;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.function.filter.mask.Mask;
import ar.edu.itba.ati.idp.utils.MatrixSlopesProcessor;

public class ZeroCrossesFilter implements DoubleArray2DUnaryOperator {
  private final Filter<Mask> filter;
  private final MatrixSlopesProcessor matrixSlopesProcessor;

  private ZeroCrossesFilter(final Filter<Mask> filter,
                            final MatrixSlopesProcessor matrixSlopesProcessor) {
    this.filter = filter;
    this.matrixSlopesProcessor = matrixSlopesProcessor;
  }

  public static ZeroCrossesFilter newInstance(final Mask mask,
                                              final MatrixSlopesProcessor matrixSlopesProcessor) {
    return new ZeroCrossesFilter(Filter.newInstance(mask), matrixSlopesProcessor);
  }

  @Override
  public double[][] apply(final double[][] pixels) {
    return filter.andThen(matrixSlopesProcessor).apply(pixels);
  }
}

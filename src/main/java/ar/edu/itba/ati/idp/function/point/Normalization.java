package ar.edu.itba.ati.idp.function.point;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import ar.edu.itba.ati.idp.utils.ArrayUtils;


// TODO: Update or remove
public class Normalization implements DoubleArray2DUnaryOperator {

  private static final double MAX_VALUE = 255; // TODO: Sacar de ImageMatrix

  @Override
  public double[][] apply(final double[][] matrix) {
    final double[] minAndMax = ArrayUtils.minAndMax(matrix);
    final double m = MAX_VALUE / (minAndMax[1] - minAndMax[0]);
    final double b = -m * minAndMax[0];

    for (int y = 0; y < matrix.length; y++) {
      for (int x = 0; x < matrix[y].length; x++) {
        matrix[y][x] = m * matrix[y][x] + b;
      }
    }

    return matrix;
  }
}

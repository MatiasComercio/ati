package ar.edu.itba.ati.idp.utils;

import java.util.Arrays;

public abstract class Arrays2D {

  public static double[][] copyOf(final double[][] original) {
    final double[][] copy = new double[original.length][];

    for (int i = 0; i < original.length; i++) {
      copy[i] = Arrays.copyOf(original[i], original[i].length);
    }

    return copy;
  }

  public static double[] minAndMax(final double[][] matrix) {
    double min = matrix[0][0];
    double max = matrix[0][0];

    for (final double[] row : matrix) {
      for (final double value : row) {
        if (value > max) {
          max = value;
        }

        if (value < min) {
          min = value;
        }
      }
    }

    return new double[]{min, max};
  }
}

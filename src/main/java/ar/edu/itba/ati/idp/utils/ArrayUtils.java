package ar.edu.itba.ati.idp.utils;

import java.util.Arrays;

public abstract class ArrayUtils {

  public static double[][][] copyOf(final double[][][] original) {
    final double[][][] copy = new double[original.length][][];

    for (int i = 0; i < original.length; i++) {
      copy[i] = copyOf(original[i]);
    }

    return copy;
  }

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

  public static int[] minAndMax(final int[][] matrix) {
    int min = matrix[0][0];
    int max = matrix[0][0];

    for (final int[] row : matrix) {
      for (final int value : row) {
        if (value > max) {
          max = value;
        }

        if (value < min) {
          min = value;
        }
      }
    }

    return new int[]{min, max};
  }

  public static double getClampedValue(final double[][] matrix, final int x, final int y) {
    final int clampedY = y < 0 ? 0 : y >= matrix.length ? matrix.length - 1 : y;
    final int clampedX = x < 0 ? 0 : x >= matrix[clampedY].length ? matrix[clampedY].length - 1 : x;
    return matrix[clampedY][clampedX];
  }

  public static double[][] newWithSizeOf(final double[][] matrix) {
    return new double[matrix.length][matrix[0].length]; // Assumes all rows have the same #cols.
  }

  public static double[] newWithSizeOf(final double[] array) {
    return new double[array.length];
  }
}

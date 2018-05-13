package ar.edu.itba.ati.idp.utils;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;

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

  public static double averageValue(final double[][] matrix) {
    double sum = 0.0;
    int total = 0;

    for (int y = 0; y < matrix.length; y++) {
      for (int x = 0; x < matrix[y].length; x++) {
        sum += matrix[y][x];
        total++;
      }
    }

    return sum / total;
  }

  public static double averageValue(final double[][] matrix, final int x0, final int y0,
      final int w, final int h) {
    double sum = 0.0;
    int total = 0;

    for (int y = y0; y < matrix.length && y < h; y++) {
      for (int x = x0; x < matrix[y].length && x < w; x++) {
        sum += matrix[y][x];
        total++;
      }
    }

    return sum / total;
  }

  public static double standardDeviation(final double[][] matrix) {
    return standardDeviation(matrix, averageValue(matrix));
  }

  public static double standardDeviation(final double[][] matrix, final double average) {
    double var = 0.0;
    int total = 0;

    for (int y = 0; y < matrix.length; y++) {
      for (int x = 0; x < matrix[y].length; x++) {
        var += pow((matrix[y][x] - average), 2);
        total++;
      }
    }
    var /= total;

    return sqrt(var);
  }

//  public static boolean anyMatchAround(final double[][] m, final int r, final int c,
//      final DoublePredicate predicate, final EnumSet<Direction> directions) {
//    for (final Direction direction : directions) {
//      final OptionalDouble valueInDirection = direction.getValueInDirection(m, r, c);
//
//      if (valueInDirection.isPresent() && predicate.test(valueInDirection.getAsDouble())) {
//        return true;
//      }
//    }
//
//    return false;
//  }

  public static boolean anyMatchAround(final double[][] m, final int x, final int y,
      final DoublePredicate predicate, final EnumSet<DegreeDirection> directions) {
    for (final DegreeDirection direction : directions) {
      final OptionalDouble[] valuesInDirection = direction.getValuesInDirection(m, x, y);

      for (final OptionalDouble valueInDirection : valuesInDirection) {
        if (valueInDirection.isPresent() && predicate.test(valueInDirection.getAsDouble())) {
          return true;
        }
      }
    }

    return false;
  }

  public static double reduceAround(final double[][] m, final int x, final int y,
      final double identity, final DoubleBinaryOperator accumulator,
      final EnumSet<DegreeDirection> directions) {
    double accumulated = identity;

    for (final DegreeDirection direction : directions) {
      final OptionalDouble[] valuesInDirection = direction.getValuesInDirection(m, x, y);

      for (final OptionalDouble valueInDirection : valuesInDirection) {
        if (valueInDirection.isPresent()) {
          accumulated = accumulator.applyAsDouble(accumulated, valueInDirection.getAsDouble());
        }
      }
    }

    return accumulated;
  }

  public static double[] minAndMaxAround(final double[][] m, final int x, final int y,
      final EnumSet<DegreeDirection> directions) {
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;

    for (final DegreeDirection direction : directions) {
      final OptionalDouble[] valuesInDirection = direction.getValuesInDirection(m, x, y);

      for (final OptionalDouble valueInDirection : valuesInDirection) {
        if (valueInDirection.isPresent()) {
          final double value = valueInDirection.getAsDouble();

          if (value > max) {
            max = value;
          }

          if (value < min) {
            min = value;
          }
        }
      }
    }

    return new double[]{min, max};
  }

  /** Inclusive end */
  public static double[] newRangedArray(final double start, final double step, final double end) {
    final int length = (int) ((end - start) / step) + 1; // Floor operation; then +1 for inclusive end
    final double[] rangeArray = new double[length];
    for (int i = 0; i < rangeArray.length; i++) {
      rangeArray[i] = start + i * step;
    }
    return rangeArray;
  }

  public enum DegreeDirection {
    DEGREE_0(Direction.W, Direction.E),
    DEGREE_45(Direction.SW, Direction.NE),
    DEGREE_90(Direction.S, Direction.N),
    DEGREE_135(Direction.SE, Direction.NW);

    private final Direction fromDirection;
    private final Direction toDirection;

    DegreeDirection(final Direction fromDirection, final Direction toDirection) {
      this.fromDirection = fromDirection;
      this.toDirection = toDirection;
    }

    public OptionalDouble[] getValuesInDirection(final double[][] matrix, final int x,
        final int y) {
      return new OptionalDouble[]{fromDirection.getValueInDirection(matrix, x, y),
          toDirection.getValueInDirection(matrix, x, y)};
    }

    public void forEachValueInDirection(final double[][] matrix, final int x,
        final int y, final DoubleConsumer consumer) {
      fromDirection.getValueInDirection(matrix, x, y).ifPresent(consumer);
      toDirection.getValueInDirection(matrix, x, y).ifPresent(consumer);
    }

    public <T> void forEachValueInDirection(final T[][] matrix, final int x,
        final int y, final Consumer<T> consumer) {
      fromDirection.getValueInDirection(matrix, x, y).ifPresent(consumer);
      toDirection.getValueInDirection(matrix, x, y).ifPresent(consumer);
    }

    public static DegreeDirection fromDegree(double degree) {
      if (degree < 0 || degree > 360) {
        throw new IllegalArgumentException("Invalid degree");
      }

      if (degree > 180) {
        degree -= 180;
      }

      if (degree < 22.5) {
        return DEGREE_0;
      }

      if (degree < 67.5) {
        return DEGREE_45;
      }

      if (degree < 112.5) {
        return DEGREE_90;
      }

      if (degree < 157.5) {
        return DEGREE_135;
      }

      return DEGREE_0;
    }
  }

  public enum Direction {
    // (y, x)
    NW(-1, -1),
    N(-1, 0),
    NE(-1, +1),
    W(0, -1),
    E(0, +1),
    SW(+1, -1),
    S(+1, 0),
    SE(+1, +1);

    private final int xValue;
    private final int yValue;

    Direction(final int xValue, final int yValue) {
      this.xValue = xValue;
      this.yValue = yValue;
    }

    public OptionalDouble getValueInDirection(final double[][] matrix, final int x, final int y) {
      final int desiredY = y + yValue;
      final int desiredX = x + xValue;

      if (desiredY < 0 || desiredY >= matrix.length || desiredX < 0
          || desiredX >= matrix[desiredY].length) {
        return OptionalDouble.empty();
      }

      return OptionalDouble.of(matrix[desiredY][desiredX]);
    }

    public <T> Optional<T> getValueInDirection(final T[][] matrix, final int x, final int y) {
      final int desiredY = y + yValue;
      final int desiredX = x + xValue;

      if (desiredY < 0 || desiredY >= matrix.length || desiredX < 0
          || desiredX >= matrix[desiredY].length) {
        return Optional.empty();
      }

      return Optional.ofNullable(matrix[desiredY][desiredX]);
    }
  }
}

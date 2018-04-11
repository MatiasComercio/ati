package ar.edu.itba.ati.idp.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum MatrixRotator {
  INSTANCE;

  /*
   * Expect square odd matrix.
   * It can (but won't) rotate even square matrices, but it make no sense to do that, as for
   * even matrix it will happen that rotate(rotate(m, D45), D45) != rotate(m, D90), which means
   * that two consecutive 45 degree rotations won't (because it cannot be achieved)
   * be equal to a direct 90 degree rotation.
   */
  public double[][] rotate(final double[][] m, final Degree degree) {
    final int sideSize = m.length;
    if (sideSize != m[0].length || sideSize % 2 == 0) {
      throw new IllegalArgumentException("Only square odd matrix are accepted");
    }


    final double[][] rotatedMatrix = ArrayUtils.copyOf(m);

    // int division on purpose. The second term acts as a "ceil" operation.
    final int rectangles = sideSize / 2 + sideSize % 2;

    for (int startI = 0; startI <= rectangles; startI++) {
      final int endI = (sideSize - 1) - startI; // Inclusive boundary.
      if (endI <= startI) {
        continue;
      }
      final Sides sides = Sides.newInstance(startI, endI);
      sides.rotateValues(m, rotatedMatrix, degree);
    }

    return rotatedMatrix;
  }

  @SuppressWarnings("unused")
  public enum Degree {
    D0(0), D45(1), D90(2), D135(3), D180(4), D225(5), D270(6), D315(7);

    private final int rotation;

    Degree(final int rotation) {
      this.rotation = rotation;
    }
  }

  private static final class Sides {
    private final Set<AbstractSide> sides;
    private final int rectangleSideSize;
    private final int totalIndexes;

    private Sides(final Set<AbstractSide> sides, final int rectangleSideSize,
                  final int totalIndexes) {
      this.sides = Collections.unmodifiableSet(sides);
      this.rectangleSideSize = rectangleSideSize;
      this.totalIndexes = totalIndexes;
    }

    private static Sides newInstance(final int startI, final int endI) {
      final int rectangleSideSize = endI - startI + 1;
      // Each corner is counted 2 per side => we are removing this extra count.
      final int totalIndexes = 4 * rectangleSideSize - 4; // Indexes in range [0, totalIndexes - 1].

      /*
       * We'll grab important array indexes and name them by their matrix position.
       * Note that in each case, we increment a rectangle side size,
       * and subtract the double-counted corner cells, if any.
       */
      final int topLeftIndex = 0;
      final int topRightIndex = rectangleSideSize - 1;
      final int bottomRightIndex = 2 * rectangleSideSize - 2;
      final int bottomLeftIndex = 3 * rectangleSideSize - 3;

      final Set<AbstractSide> sides = new HashSet<>();

      sides.add(new TopSide(startI, endI, topLeftIndex, topRightIndex, startI)); // clamped: Y
      sides.add(new RightSide(startI, endI, topRightIndex, bottomRightIndex, endI)); // clamped: X
      sides.add(new BottomSide(startI, endI, bottomRightIndex, bottomLeftIndex, endI)); // clamped: Y
      sides.add(new LeftSide(startI, endI, bottomLeftIndex, totalIndexes, startI)); // clamped: X

      return new Sides(sides, rectangleSideSize, totalIndexes);
    }

    private void rotateValues(final double[][] matrix, final double[][] rotatedMatrix,
                             final Degree degree) {
      // indexes for each side go from 0 to (rectangleSideSize - 1)
      int offsetDistance = (degree.rotation * (rectangleSideSize - 1)) / 2; // int division on purpose.

      for (final AbstractSide side : sides) {
        side.processValues(matrix, (value, arrayIndex) -> {
          final int rotatedArrayPosition = (arrayIndex + offsetDistance) % totalIndexes;
          for (final AbstractSide destinationSide : sides) {
            if (destinationSide.putValue(rotatedMatrix, value, rotatedArrayPosition)) {
              return;
            }
          }
        });
      }
    }
  }
  
  @SuppressWarnings("WeakerAccess")
  private static abstract class AbstractSide {
    protected final int startMatrixIndex;
    protected final int endMatrixIndex;
    protected final int startArrayIndex;
    protected final int endArrayIndex;

    protected AbstractSide(final int startMatrixIndex, final int endMatrixIndex,
                           final int startArrayIndex, final int endArrayIndex) {
      this.startMatrixIndex = startMatrixIndex;
      this.endMatrixIndex = endMatrixIndex;
      this.startArrayIndex = startArrayIndex;
      this.endArrayIndex = endArrayIndex;
    }

    public void processValues(final double[][] matrix, final ValueProcessor valueProcessor) {
      for (int matrixIndex = startMatrixIndex; matrixIndex <= endMatrixIndex; matrixIndex++) {
        final int normalizedIndex = normalize(matrixIndex);
        valueProcessor.process(getValue(matrix, matrixIndex), getArrayIndex(normalizedIndex));
      }
    }

    public boolean putValue(final double[][] rotatedMatrix, final double value,
                            final int rotatedArrayPosition) {
      if (!(startArrayIndex <= rotatedArrayPosition && rotatedArrayPosition <= endArrayIndex)) {
        return false;
      }

      final int[] matrixIndexes = getMatrixIndexes(rotatedArrayPosition);
      rotatedMatrix[matrixIndexes[1]][matrixIndexes[0]] = value;
      return true;
    }

    protected int normalize(final int matrixIndex) {
      return matrixIndex - startMatrixIndex;
    }

    protected int denormalize(final int matrixIndex) {
      return matrixIndex + startMatrixIndex;
    }

    protected abstract double getValue(double[][] matrix, int matrixIndex);

    protected abstract int getArrayIndex(final int normalizedIndex);

    // return [x,y] indexes
    protected abstract int[] getMatrixIndexes(final int rotatedArrayPosition);

  }

  /*
    Sides & their particular attributes.
    - TOP: [topLeftIndex, topRightIndex] => elementsCount = topRightIndex - topLeftIndex + 1
      value = m[startI][x]
      normalizedIndex = (x - startI)
      arrayPosition = normalizedIndex

    - RIGHT: (topRightIndex, bottomRightIndex] => elementsCount = bottomRightIndex - topRightIndex
      value = m[y][endI]
      normalizedIndex = (y - startI)
      arrayPosition = normalizedIndex + topRightIndex

    - BOTTOM: (bottomRightIndex, bottomLeftIndex] => elementsCount = bottomLeftIndex - bottomRightIndex
      value = m[endI][x]
      normalizedIndex = (x - startI)
      arrayPosition = bottomLeftIndex - normalizedIndex

    - LEFT: (bottomLeftIndex, topRightIndex) => elementsCount = totalIndexes - bottomLeftIndex - 1
      // Note that topRightIndex % totalIndexes = 0 ==> they are equivalent (mod totalIndexes)
      value = m[y][startI]
      normalizedIndex = (y - startI)
      // Mod operation just in case 0 is included here.
      arrayPosition = (totalIndexes - normalizedIndex) % totalIndexes
  */

  private static final class TopSide extends AbstractSide {
    private final int y;

    private TopSide(final int startX, final int endX,
                      final int startArrayIndex, final int endArrayIndex,
                      final int y) {
      super(startX, endX, startArrayIndex, endArrayIndex);
      this.y = y;
    }

    @Override
    protected double getValue(final double[][] matrix, final int matrixIndex) {
      return matrix[y][matrixIndex];
    }

    @Override
    protected int getArrayIndex(final int normalizedIndex) {
      return normalizedIndex;
    }

    @Override
    protected int[] getMatrixIndexes(final int rotatedArrayPosition) {
      final int x = denormalize(rotatedArrayPosition);
      return new int[] { x, y };
    }
  }

  private static final class RightSide extends AbstractSide {
    private final int x;

    private RightSide(final int startY, final int endY,
                      final int startArrayIndex, final int endArrayIndex,
                      final int x) {
      super(startY, endY, startArrayIndex, endArrayIndex);
      this.x = x;
    }

    @Override
    protected double getValue(final double[][] matrix, final int matrixIndex) {
      return matrix[matrixIndex][x];
    }

    @Override
    protected int getArrayIndex(final int normalizedIndex) {
      return normalizedIndex + startArrayIndex;
    }

    @Override
    protected int[] getMatrixIndexes(final int rotatedArrayPosition) {
      final int y = denormalize(rotatedArrayPosition - startArrayIndex);
      return new int[] { x, y };
    }
  }

  private static final class BottomSide extends AbstractSide {
    private final int y;

    private BottomSide(final int startX, final int endX,
                      final int startArrayIndex, final int endArrayIndex,
                      final int y) {
      super(startX, endX, startArrayIndex, endArrayIndex);
      this.y = y;
    }

    @Override
    protected double getValue(final double[][] matrix, final int matrixIndex) {
      return matrix[y][matrixIndex];
    }

    @Override
    protected int getArrayIndex(final int normalizedIndex) {
      return endArrayIndex - normalizedIndex;
    }

    @Override
    protected int[] getMatrixIndexes(final int rotatedArrayPosition) {
      final int x = denormalize(endArrayIndex - rotatedArrayPosition);
      return new int[] { x, y };
    }
  }

  private static final class LeftSide extends AbstractSide {
    private final int x;

    private LeftSide(final int startY, final int endY,
                        final int startArrayIndex, final int endArrayIndex,
                        final int x) {
      super(startY, endY, startArrayIndex, endArrayIndex);
      this.x = x;
    }

    @Override
    protected double getValue(final double[][] matrix, final int matrixIndex) {
      return matrix[matrixIndex][x];
    }

    @Override
    protected int getArrayIndex(final int normalizedIndex) {
      return endArrayIndex - normalizedIndex;
    }

    @Override
    protected int[] getMatrixIndexes(final int rotatedArrayPosition) {
      final int y = denormalize(endArrayIndex - rotatedArrayPosition);
      return new int[] { x, y };
    }
  }


  @FunctionalInterface
  private interface ValueProcessor {
    void process(double value, int arrayIndex);
  }
}

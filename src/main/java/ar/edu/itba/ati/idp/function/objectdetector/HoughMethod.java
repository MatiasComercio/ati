package ar.edu.itba.ati.idp.function.objectdetector;

import ar.edu.itba.ati.idp.function.ColorOverUniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.objectdetector.HoughMethod.AbstractAccumCell.Point;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HoughMethod implements ColorOverUniquePixelsBandOperator {
  private final AccumMatrix accumMatrix;

  protected HoughMethod(final AccumMatrix accumMatrix) {
    this.accumMatrix = accumMatrix;
  }

  @Override
  public final double[][][] apply(final double[][] pixels) {
    // Require thresholdized image => will be validate during the first pixel's iteration.

    /*
     * Validating that the rho is withing bounds makes no sense, as the only cost of this is
     * extra iterations (i.e., computation cost), which is not our fault.
     * This cannot generate any kind of strange behaviour on us => it won't be validated.
     */

    // For each pixel, apply the accum matrix logic.
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        final int pixel = (int) pixels[y][x];
        if (pixel != ImageMatrix.getMaxNormalizedPixelValue()) {
          if (pixel != 0) { // Neither a white nor a black pixel => not binary pixels => error.
            throw new IllegalArgumentException("Pixel matrix is not binary");
          }

          continue; // Not a white pixel
        }

        final Point pixelPoint = new Point(x, y);
        accumMatrix.process(pixelPoint);
      }
    }

    // Plot the found shapes.
    final double[][] shapesPixelMatrix = ArrayUtils.newWithSizeOf(pixels);
    accumMatrix.plotIn(shapesPixelMatrix);
    return new double[][][] {shapesPixelMatrix};
  }

  protected static class AccumMatrix {
    private final List<AbstractAccumCell> accumCells;
    private final double epsilon;
    private int maxAccumValue;

    protected AccumMatrix(final List<AbstractAccumCell> accumCells, final double epsilon) {
      this.accumCells = accumCells;
      this.epsilon = epsilon;
      this.maxAccumValue = 0;
    }

    private void process(final Point pixelPoint) {
      accumCells.forEach(aCell -> processCell(pixelPoint, aCell));
    }


    private void processCell(final Point pixelPoint, final AbstractAccumCell aCell) {
      if (aCell.doesEquationMatchPixelPoint(pixelPoint, epsilon)) {
        aCell.addMatchingPixelPoint(pixelPoint);
        final int numMatchingPixelPoints = aCell.getNumMatchingPixelPoints();
        if (maxAccumValue < numMatchingPixelPoints) {
          maxAccumValue = numMatchingPixelPoints;
        }
      }
    }

    private void plotIn(final double[][] shapesPixelMatrix) {
      // Plot the found shapes, only the ones above the 80% of the max accum registered.
      accumCells.forEach(aCell -> plotCellIn(shapesPixelMatrix, aCell));
    }

    private void plotCellIn(final double[][] shapesPixelMatrix, final AbstractAccumCell aCell) {
      if (aCell.getNumMatchingPixelPoints() * .8 > maxAccumValue) {
        for (final Point point : aCell.matchingPixelPoints) {
          shapesPixelMatrix[point.y][point.x] = ImageMatrix.getMaxNormalizedPixelValue();
        }
      }
    }
  }

  protected static abstract class AbstractAccumCell {
    private final Set<Point> matchingPixelPoints;

    protected AbstractAccumCell() {
      this.matchingPixelPoints = new HashSet<>();
    }

    protected abstract boolean doesEquationMatchPixelPoint(final Point pixelPoint, final double epsilon);

    private void addMatchingPixelPoint(final Point pixelPoint) {
      matchingPixelPoints.add(pixelPoint);
    }

    private int getNumMatchingPixelPoints() {
      return matchingPixelPoints.size();
    }

    protected static final class Point {
      private final int x;
      private final int y;

      private Point(final int x, final int y) {
        this.x = x;
        this.y = y;
      }

      public int getX() {
        return x;
      }

      public int getY() {
        return y;
      }
    }
  }
}

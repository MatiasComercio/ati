package ar.edu.itba.ati.idp.function.threshold;

import static ar.edu.itba.ati.idp.function.threshold.OptimumThreshold.getOptimumThreshold;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.minAndMaxAround;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.standardDeviation;

import ar.edu.itba.ati.idp.function.UniquePixelsBandOperator;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.utils.ArrayUtils.DegreeDirection;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;
import javafx.geometry.Point2D;

// TODO: CONSULTAR
public enum HysteresisThreshold implements UniquePixelsBandOperator {
  INSTANCE;

  private static final double MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();

  @Override
  public double[][] apply(final double[][] pixels) {
    final int optimumThreshold = getOptimumThreshold(pixels);
    final double standardDeviation = standardDeviation(pixels);
    final double t1 = optimumThreshold - standardDeviation;
    final double t2 = optimumThreshold + standardDeviation;
    final double[][] newPixels = new double[pixels.length][];
    final Queue<Point2D> queue = new LinkedList<>();

    for (int y = 0; y < newPixels.length; y++) {
      newPixels[y] = new double[pixels[y].length];
      Arrays.fill(newPixels[y], -1);

      for (int x = 0; x < newPixels[y].length; x++) {
        if (pixels[y][x] > t2) {
          newPixels[y][x] = MAX_VALUE;
        } else if (pixels[y][x] < t1) {
          newPixels[y][x] = 0.0;
        } else {
          final double[] minAndMaxAround = minAndMaxAround(newPixels, x, y,
              EnumSet.allOf(DegreeDirection.class));
          if (minAndMaxAround[1] == 255.0) {
            newPixels[y][x] = MAX_VALUE;
          } else if (minAndMaxAround[1] == 0.0) {
            newPixels[y][x] = 0.0;
          } else {
            queue.add(new Point2D(x, y));
          }
        }
      }
    }

    while (!queue.isEmpty()) {
      final Point2D pixelPosition = queue.remove();
      final int x = (int) pixelPosition.getX();
      final int y = (int) pixelPosition.getY();

      final double[] minAndMaxAround = minAndMaxAround(newPixels, x, y,
          EnumSet.allOf(DegreeDirection.class));
      if (minAndMaxAround[1] == 255.0) {
        newPixels[y][x] = MAX_VALUE;
      } else if (minAndMaxAround[1] == 0.0) {
        newPixels[y][x] = 0.0;
      } else {
        queue.add(pixelPosition);
      }
    }

    return newPixels;
  }
}

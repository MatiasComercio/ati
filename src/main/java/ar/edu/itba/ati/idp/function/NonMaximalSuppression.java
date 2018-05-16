package ar.edu.itba.ati.idp.function;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.DegreeDirection.fromDegree;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.anyMatchAround;

import ar.edu.itba.ati.idp.utils.ArrayUtils;
import java.util.EnumSet;

public final class NonMaximalSuppression {

  private NonMaximalSuppression() {
  }

  public static double[][] apply(final double[][] magnitudes, final double[][] directions) {
    // It is assumed that both matrices are of the same size.
    final double[][] result = ArrayUtils.newWithSizeOf(magnitudes);

    for (int y = 0; y < magnitudes.length; y++) {
      for (int x = 0; x < magnitudes[y].length; x++) {
        final double currentValue = magnitudes[y][x];
        final double direction = directions[y][x];

        final boolean isThereAGreaterNeighbour = anyMatchAround(magnitudes, x, y,// For this currentValue (at (x,y)) at the magnitudes matrix
                                                      EnumSet.of(fromDegree(direction)), // For neighbours in this direction
                                                      (p) -> p > currentValue // Check if any of these neighbours (p) is greater than us
        );
         // If there's any grater neighbour => we have to turn ourselves off.
        result[y][x] = isThereAGreaterNeighbour ? 0.0 : currentValue;
      }
    }

    return result;
  }
}

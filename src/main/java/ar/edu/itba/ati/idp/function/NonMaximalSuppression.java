package ar.edu.itba.ati.idp.function;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.anyMatchAround;

import ar.edu.itba.ati.idp.utils.ArrayUtils.DegreeDirection;
import java.util.EnumSet;

public abstract class NonMaximalSuppression {

  public static double[][] apply(final double[][] pixels, final double[][] gradientAngles) {
    final double[][] borderMagnitudes = new double[pixels.length][];

    for (int y = 0; y < pixels.length; y++) {
      borderMagnitudes[y] = new double[pixels[y].length];

      for (int x = 0; x < pixels[0].length; x++) {
        final double currentValue = pixels[y][x];

        if (currentValue != 0.0 && anyMatchAround(pixels, x, y, (p) -> p > currentValue,
            EnumSet.of(DegreeDirection.fromDegree(gradientAngles[y][x])))) {
          borderMagnitudes[y][x] = 0.0;
        } else {
          borderMagnitudes[y][x] = currentValue;
        }
      }
    }

    return borderMagnitudes;
  }
}

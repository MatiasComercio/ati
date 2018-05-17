package ar.edu.itba.ati.idp.function;

import static ar.edu.itba.ati.idp.function.RealTimeTracking.Type.BACKGROUND;
import static ar.edu.itba.ati.idp.function.RealTimeTracking.Type.LIN;
import static ar.edu.itba.ati.idp.function.RealTimeTracking.Type.LOUT;
import static ar.edu.itba.ati.idp.function.RealTimeTracking.Type.OBJECT;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.allMatchAround;
import static ar.edu.itba.ati.idp.utils.ArrayUtils.forEachAround;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import ar.edu.itba.ati.idp.function.filter.mask.linear.GaussMask;
import ar.edu.itba.ati.idp.model.IntBinaryPredicate;
import ar.edu.itba.ati.idp.model.IntVector2D;
import ar.edu.itba.ati.idp.utils.ArrayUtils.DegreeDirection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.DoublePredicate;

public class RealTimeTracking implements ColorOverRawPixelsMatrixOperator {

  private static final double GAUSS_MASK_SIGMA = 1.0;

  private final GaussMask gaussMask;

  private double[][] phi;
  private Set<IntVector2D> linPositions;
  private Set<IntVector2D> loutPositions;

//  private double[] sumBackgroundColor;
//  private double backgroundSize = 0.0;
  private double[] sumObjectColor;
  private double objectSize = 0.0;

  private final int iterationLimit1stCycle;
  private final int iterationLimit2ndCycle;
  private final int initialX0;
  private final int initialY0;
  private final int initialW;
  private final int initialH;

  public RealTimeTracking(final int iterationLimit1stCycle, final int iterationLimit2ndCycle,
      final int initialX0, final int initialY0, final int initialW, final int initialH) {
    this.iterationLimit1stCycle = iterationLimit1stCycle;
    this.iterationLimit2ndCycle = iterationLimit2ndCycle;
    this.initialX0 = initialX0;
    this.initialY0 = initialY0;
    this.initialW = initialW;
    this.initialH = initialH;
    this.gaussMask = GaussMask.newInstance(GAUSS_MASK_SIGMA, iterationLimit2ndCycle);
  }

  @Override
  public double[][][] apply(final double[][][] pixels) {
    // Step 1
    if (phi == null || linPositions == null || loutPositions == null) {
      initialize(pixels);
    } else {
      update(pixels);
    }

    int iteration1stCycle = 0;
    do {
      // Cycle 1
      while (!endConditionMet(pixels) && iteration1stCycle++ < iterationLimit1stCycle) {
        cycleIteration(pixels, (x, y) -> speed(pixels, x, y) > 0.0,
            (x, y) -> speed(pixels, x, y) < 0.0);
      }

      // Cycle 2
      for (int i = 0; i < iterationLimit2ndCycle; i++) {
        cycleIteration(pixels, (x, y) -> gaussMask.apply(phi, x, y) < 0.0,
            (x, y) -> gaussMask.apply(phi, x, y) > 0.0);
      }
    } while (!endConditionMet(pixels) && iteration1stCycle < iterationLimit1stCycle);

    return phiToDoubleMatrix();
  }

  private void cycleIteration(final double[][][] pixels, final IntBinaryPredicate switchInPredicate,
      final IntBinaryPredicate switchOutPredicate) {
    final Iterator<IntVector2D> loutBeforeStepIt = new HashSet<>(loutPositions).iterator();
    while (loutBeforeStepIt.hasNext()) {
      final IntVector2D position = loutBeforeStepIt.next();

      if (switchInPredicate.test(position.x(), position.y())) {
        loutBeforeStepIt.remove();
        switchIn(pixels, position);
      }
    }

    // For each pixel (x,y) ∈ LIN
    // If ∀y ∈ N(x), φ(y) < 0
    // Remove it from LIN
    // Update phi value to object
    fixInconsistentValuesInPhi(linPositions, Type::isInterior, OBJECT);

    final Iterator<IntVector2D> linBeforeStepIt = new HashSet<>(linPositions).iterator();
    while (linBeforeStepIt.hasNext()) {
      final IntVector2D position = linBeforeStepIt.next();

      if (switchOutPredicate.test(position.x(), position.y())) {
        // Remove from LIN
        linBeforeStepIt.remove();
        switchOut(pixels, position);
      }
    }

    // For each pixel (x,y) ∈ LOUT
    // If ∀y ∈ N(x), φ(y) > 0
    // Remove it from LOUT
    // Update phi value to background
    fixInconsistentValuesInPhi(loutPositions, Type::isExterior, BACKGROUND);
  }

  private boolean endConditionMet(final double[][][] pixels) {
    return linPositions.stream().allMatch(p -> speed(pixels, p.x(), p.y()) >= 0)
        && loutPositions.stream().allMatch(p -> speed(pixels, p.x(), p.y()) <= 0);
  }

  private void update(final double[][][] pixels) {
    // TODO
  }

  /**
   * Moves the curve outward one pixel at (x,y) by switching it from LOUT to LIN and adding all its
   * neighboring exterior pixels to LOUT
   */
  private void switchIn(final double[][][] pixels, final IntVector2D position) {
    final int x = position.x();
    final int y = position.y();

    // Remove from LOUT
    loutPositions.remove(position);
//    subtractColors(sumBackgroundColor, pixels, x, y);
//    backgroundSize--;

    // Add to LIN
    phi[y][x] = LIN.doubleValue();
//    addColors(sumObjectColor, pixels, x, y);
//    objectSize++;
    linPositions.add(position);

    // Check background neighbours
    forEachAround(phi, x, y, EnumSet.of(DegreeDirection.DEGREE_0, DegreeDirection.DEGREE_90),
        (xn, yn) -> {
          if (phi[yn][xn] == BACKGROUND.doubleValue()) {
            // Add to LOUT
            phi[yn][xn] = LOUT.doubleValue();
            loutPositions.add(IntVector2D.of(xn, yn));
          }
        });
  }

  /**
   * Similar to switchIn() but moves the curve inward one pixel at (x,y) ∈ LIN
   */
  private void switchOut(final double[][][] pixels, final IntVector2D position) {
    final int x = position.x();
    final int y = position.y();

    // Remove from LIN
    linPositions.remove(position);
//    subtractColors(sumObjectColor, pixels, x, y);
//    objectSize--;

    // Add to LOUT
    phi[y][x] = LOUT.doubleValue();
//    addColors(sumBackgroundColor, pixels, x, y);
//    backgroundSize++;
    loutPositions.add(position);

    // Check background neighbours
    forEachAround(phi, x, y, EnumSet.of(DegreeDirection.DEGREE_0, DegreeDirection.DEGREE_90),
        (xn, yn) -> {
          if (phi[yn][xn] == OBJECT.doubleValue()) {
            // Add to LIN
            phi[yn][xn] = LIN.doubleValue();
            linPositions.add(IntVector2D.of(xn, yn));
          }
        });
  }

  private void fixInconsistentValuesInPhi(final Set<IntVector2D> positions,
      final DoublePredicate fixCondition, final Type fixValue) {
    final Iterator<IntVector2D> it = positions.iterator();
    while (it.hasNext()) {
      final IntVector2D position = it.next();

      if (allMatchAround(phi, position.x(), position.y(),
          EnumSet.of(DegreeDirection.DEGREE_0, DegreeDirection.DEGREE_90), fixCondition)) {
        it.remove();
        phi[position.y()][position.x()] = fixValue.doubleValue();
      }
    }
  }

  // TODO: Select color
  private double[][][] phiToDoubleMatrix() {
    final double[][][] result = new double[sumObjectColor.length][phi.length][phi[0].length];

    for (int y = 0; y < phi.length; y++) {
      for (int x = 0; x < phi[y].length; x++) {
        for (int b = 0; b < sumObjectColor.length; b++) {
          result[b][y][x] = phi[y][x] == LIN.doubleValue() ? 255 : 0.0;
        }
      }
    }

    return result;
  }

  private void initialize(final double[][][] firstPixels) {
    this.phi = new double[firstPixels[0].length][firstPixels[0][0].length];
    this.linPositions = new HashSet<>();
    this.loutPositions = new HashSet<>();
    this.sumObjectColor = new double[firstPixels.length];
//    this.sumBackgroundColor = new double[firstPixels.length];

    for (int y = 0; y < firstPixels[0].length; y++) {
      for (int x = 0; x < firstPixels[0][0].length; x++) {
        if (isInitialObject(x, y, initialX0, initialY0, initialW, initialH)) {
          phi[y][x] = OBJECT.doubleValue();
          addColors(sumObjectColor, firstPixels, x, y);
          objectSize++;
        } else if (isInitialLIN(x, y, initialX0, initialY0, initialW, initialH)) {
          phi[y][x] = LIN.doubleValue();
          addColors(sumObjectColor, firstPixels, x, y);
          objectSize++;
          linPositions.add(IntVector2D.of(x, y));
        } else if (isInitialLOUT(x, y, initialX0, initialY0, initialW, initialH)) {
          phi[y][x] = LOUT.doubleValue();
//          addColors(sumBackgroundColor, firstPixels, x, y);
//          backgroundSize++;
          loutPositions.add(IntVector2D.of(x, y));
        } else {
          phi[y][x] = BACKGROUND.doubleValue();
//          addColors(sumBackgroundColor, firstPixels, x, y);
//          backgroundSize++;
        }
      }
    }
  }

  private static void addColors(final double[] color, final double[][][] pixels, final int x,
      final int y) {
    for (int b = 0; b < color.length; b++) {
      color[b] += pixels[b][y][x];
    }
  }

  private static void subtractColors(final double[] color, final double[][][] pixels, final int x,
      final int y) {
    for (int b = 0; b < color.length; b++) {
      color[b] -= pixels[b][y][x];
    }
  }

  private static boolean isInitialObject(final int x, final int y, final int initialX0,
      final int initialY0, final int initialW, final int initialH) {
    return x > initialX0 && x < initialX0 + initialW
        && y > initialY0 && y < initialY0 + initialH;
  }

  private static boolean isInitialLIN(final int x, final int y, final int initialX0,
      final int initialY0, final int initialW, final int initialH) {
    return ((x == initialX0 || x == initialX0 + initialW)
        && (y >= initialY0 && y <= initialY0 + initialH))
        || ((y == initialY0 || y == initialY0 + initialH)
        && (x >= initialX0 && x <= initialX0 + initialW));
  }

  private static boolean isInitialLOUT(final int x, final int y, final int initialX0,
      final int initialY0, final int initialW, final int initialH) {
    return ((x == initialX0 - 1 || x == initialX0 + initialW + 1)
        && (y >= initialY0 && y <= initialY0 + initialH))
        || ((y == initialY0 - 1 || y == initialY0 + initialH + 1)
        && (x >= initialX0 && x <= initialX0 + initialW));
  }

  private double speed(final double[][][] colors, final int x, final int y) {
    double norm = 0.0;

    for (int b = 0; b < colors.length; b++) {
      norm += pow(sumObjectColor[b] / objectSize - colors[b][y][x], 2);
    }

    return sqrt(norm) / sqrt(256 * 256 * colors.length) < 0.1 ? 1 : -1;

//    double norm = 0.0;
//
//    for (int b = 0; b < colors.length; b++) {
//      norm += pow(colors[b][y][x] - sumObjectColor[b] / objectSize, 2);
//    }
//
//    return sqrt(norm) / (256 * sqrt(sumObjectColor.length)) < 0.1 ? 1.0 : -1.0;

//    double norm1 = 0.0;
//    double norm2 = 0.0;
//
//    for (int b = 0; b < colors.length; b++) {
//      norm1 += pow(colors[b][y][x] - sumObjectColor[b] / objectSize, 2);
//      norm2 += pow(colors[b][y][x] - sumBackgroundColor[b] / backgroundSize, 2);
//    }
//
//    return log(sqrt(norm2) / sqrt(norm1));

//    return log(
//        abs((sumBackgroundColor / backgroundSize) - value)
//            / abs((sumObjectColor / objectSize) - value));
  }

  public enum Type {
    BACKGROUND(3.0), LOUT(1.0), LIN(-1.0), OBJECT(-3.0);

    private final double doubleValue;

    Type(final double doubleValue) {
      this.doubleValue = doubleValue;
    }

    public double doubleValue() {
      return doubleValue;
    }

    public static boolean isInterior(final double doubleValue) {
      return doubleValue == LIN.doubleValue || doubleValue == OBJECT.doubleValue;
    }

    public static boolean isExterior(final double doubleValue) {
      return doubleValue == LOUT.doubleValue || doubleValue == BACKGROUND.doubleValue;
    }
  }
}

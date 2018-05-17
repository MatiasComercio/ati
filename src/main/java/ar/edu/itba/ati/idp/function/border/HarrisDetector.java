package ar.edu.itba.ati.idp.function.border;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.newWithSizeOf;
import static java.lang.Math.pow;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import ar.edu.itba.ati.idp.function.ColorOverUniquePixelsBandOperator;
import ar.edu.itba.ati.idp.function.filter.GaussFilter;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.function.filter.mask.linear.SobelMask;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.model.ImageMatrix.Band;
import java.util.Map;

public class HarrisDetector implements ColorOverUniquePixelsBandOperator {
  private static final double MAX_VALUE = ImageMatrix.getMaxNormalizedPixelValue();
  private static final Map<Integer, CIM> CIM_BY_ID;
  static {
    CIM_BY_ID = unmodifiableMap(stream(CIM.values()).collect(toMap(CIM::getId, identity())));
  }

  private final GaussFilter gaussFilter;
  private final RotatableMask<SobelMask> maskDx;
  private final RotatableMask<SobelMask> maskDy;
  private final CIM cim;
  private final double k;
  private final double acceptancePercentage;

  private HarrisDetector(final GaussFilter gaussFilter,
                         final RotatableMask<SobelMask> maskDx,
                         final RotatableMask<SobelMask> maskDy,
                         final CIM cim,
                         final double k,
                         final double acceptancePercentage) {
    this.gaussFilter = gaussFilter;
    this.maskDx = maskDx;
    this.maskDy = maskDy;
    this.cim = cim;
    this.k = k;
    this.acceptancePercentage = acceptancePercentage;
  }

  public static HarrisDetector newInstance(final double gaussSigma, final int gaussSideLength,
                                           final int cimId, final double k,
                                           final double acceptancePercentage
  ) {
    return new HarrisDetector(
        GaussFilter.newInstance(gaussSigma, gaussSideLength),
        SobelMask.newInstanceRight(), SobelMask.newInstanceUp(),
        cimIdToCIM(cimId), k, acceptancePercentage
    );
  }

  private static CIM cimIdToCIM(final int cimId) {
    final CIM cim = CIM_BY_ID.get(cimId);
    if (cim == null) {
      throw new IllegalArgumentException("CIM with cimId " + cimId + " does not exists");
    }
    return cim;
  }

  @Override
  public double[][][] apply(final double[][] pixels) {
    final double[][][] result = new double[][][] {
        newWithSizeOf(pixels), newWithSizeOf(pixels), newWithSizeOf(pixels)
    };

    // Step 1: Calculate dx & dy with Sobel Mask
    // Step 2 - part 1: Calculate pow 2 of each (x,y)
    // Step 3 - part 1: Calculate dxy of each (x,y)
    final double[][] dx2Matrix = newWithSizeOf(pixels);
    final double[][] dy2Matrix = newWithSizeOf(pixels);
    final double[][] dxyMatrix = newWithSizeOf(pixels);
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        final double dx = maskDx.apply(pixels, x, y);
        final double dy = maskDy.apply(pixels, x, y);
        dx2Matrix[y][x] = pow(dx, 2);
        dy2Matrix[y][x] = pow(dy, 2);
        dxyMatrix[y][x] = dx * dy;
      }
    }

    // Step 2 - part 2: apply the gauss filter to each dx2, dy2 matrix.
    final double[][] gaussDx2Matrix = gaussFilter.apply(dx2Matrix);
    final double[][] gaussDy2Matrix = gaussFilter.apply(dy2Matrix);
    // Step 3 - part 3: apply the gauss filter to dxy matrix.
    final double[][] gaussDxyMatrix = gaussFilter.apply(dxyMatrix);

    // Step 4 - Calculate CI values with a desired CIM
    final double[][]ciValues = newWithSizeOf(gaussDxyMatrix);
    double maxCIValue = Double.MIN_VALUE;
    for (int y = 0; y < gaussDxyMatrix.length; y++) {
      for (int x = 0; x < gaussDxyMatrix[y].length; x++) {
        final double ciValue = cim.apply(gaussDx2Matrix[y][x], gaussDy2Matrix[y][x], gaussDxyMatrix[y][x], k);
        ciValues[y][x] = ciValue;
        if (ciValue > maxCIValue) {
          maxCIValue = ciValue;
        }
      }
    }

    // Step 5 - Find sections within the acceptance percentage.
    final int cornerBandIndex = Band.RED.getBandIndex();
    for (int y = 0; y < gaussDxyMatrix.length; y++) {
      for (int x = 0; x < gaussDxyMatrix[y].length; x++) {
        if (ciValues[y][x] > maxCIValue * acceptancePercentage) {
          result[cornerBandIndex][y][x] = MAX_VALUE;
        }
      }
    }

    // We are done :D
    return result;
  }

  private enum CIM { // I think it stands for corner intensity method.
    CIM1(1) {
      @Override
      double apply(final double dx2, final double dy2, final double dxy, final double k) {
        return (dx2 * dy2 - pow(dxy, 2)) - k * pow(dx2 + dy2, 2);
      }
    },
    CIM2(2) {
      @Override
      double apply(final double dx2, final double dy2, final double dxy, final double k) {
        return (dx2 * dy2 - pow(dxy, 2)) / (dx2 + dy2 + k); // k acts as epsilon here
      }
    },
    CIM3(3) {
      @Override
      double apply(final double dx2, final double dy2, final double dxy, final double k) {
        return (dx2 * dy2 - pow(dxy, 4)) - k * pow(dx2 + dy2, 2);
      }
    };

    abstract double apply(double dx2, double dy2, double dxy, double k);

    private final int id;

    CIM(final int id) {
      this.id = id;
    }

    private int getId() {
      return id;
    }
  }
}

package ar.edu.itba.ati.idp.function.filter.mask.nonlinear;

import static ar.edu.itba.ati.idp.utils.ArrayUtils.getClampedValue;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

import ar.edu.itba.ati.idp.function.filter.mask.AbstractMask;
import java.util.function.DoubleBinaryOperator;

public class SusanMask extends AbstractMask {

  private static final int N_MAX = 37;
  private static final double[][] BASE_MASK = {
      {0, 0, 1, 1, 1, 0, 0},
      {0, 1, 1, 1, 1, 1, 0},
      {1, 1, 1, 1, 1, 1, 1},
      {1, 1, 1, 1, 1, 1, 1},
      {1, 1, 1, 1, 1, 1, 1},
      {0, 1, 1, 1, 1, 1, 0},
      {0, 0, 1, 1, 1, 0, 0},
  };

  private final DoubleBinaryOperator comparator;

  private SusanMask(final double[][] mask, final DoubleBinaryOperator comparator) {
    super(mask[0].length, mask.length);
    this.comparator = comparator;
  }

  public static SusanMask newInstance(final double comparisonThreshold) {
    return new SusanMask(BASE_MASK, new SimpleComparator(comparisonThreshold));
  }

  public static SusanMask newSmotherInstance(final double comparisonThreshold) {
    return new SusanMask(BASE_MASK, new SmotherComparator(comparisonThreshold));
  }

  @Override
  protected void initializeMask() {
    this.mask = BASE_MASK;
  }

  @Override
  protected double applyMaskTo(final double[][] pixels, final int currCoreX, final int currCoreY) {
    final double[] n = new double[]{0};

    iterateMask((maskX, maskY) -> {
      final double maskValue = getMaskPixel(maskX, maskY);
      final double imagePixel =
          getClampedValue(pixels, currCoreX + maskX, currCoreY + maskY) * maskValue;
      if (imagePixel != 0.0) {
        n[0] += comparator.applyAsDouble(imagePixel, pixels[currCoreY][currCoreX]);
      }
    });

    return 1 - n[0] / N_MAX;
  }

  private static class SimpleComparator implements DoubleBinaryOperator {

    private final double threshold;

    private SimpleComparator(final double threshold) {
      this.threshold = threshold;
    }

    @Override
    public double applyAsDouble(final double iOther, final double iCore) {
      return abs(iOther - iCore) <= threshold ? 1.0 : 0.0;
    }
  }

  private static class SmotherComparator implements DoubleBinaryOperator {

    private final double threshold;

    private SmotherComparator(final double threshold) {
      this.threshold = threshold;
    }

    @Override
    public double applyAsDouble(final double iOther, final double iCore) {
      return exp(-1 * pow((iOther - iCore) / threshold, 6.0));
    }
  }
}

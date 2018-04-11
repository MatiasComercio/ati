package ar.edu.itba.ati.idp.function.filter;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static ar.edu.itba.ati.idp.utils.Doubles.equal;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import ar.edu.itba.ati.idp.function.filter.mask.AbstractMask;
import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.utils.MatrixRotator;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Before;
import org.junit.Test;

public class DirectionalFilterTest {
  private static final ThreadLocalRandom LOCAL_RANDOM = ThreadLocalRandom.current();

  private double[][] pixels;
  private int matrixSize;

  @Before
  public void initialize() {
    // Build a random odd square matrix (odd so as the matching mask can be rotated).
    // We are using the fact that the matrix size is equal to the mask size for simplicity sake.
    matrixSize = LOCAL_RANDOM.nextInt(3, 10);
    matrixSize += (matrixSize % 2 == 0 ? 1 : 0);
    pixels = TEST_HELPER.buildRandomMatrix(matrixSize, matrixSize);
  }

  @Test
  public void testApply() {
    // Build all possible mask rotations.
    final Map<Degree, double[][]> matrixPerRotation = buildMatrixPerRotation(matrixSize, matrixSize);

    // Get the max matrix from all possible rotations.
    final double[][] maxAbsMask = getMaxAbsMatrixFromAllMatrices(matrixPerRotation.values(), matrixSize);
    // We will later use the absolute rule: |ab| = |a||b|

    // given
    final Map<Degree, DummyMask> maskPerDegree = new EnumMap<>(Degree.class);
    for (final Entry<Degree, double[][]> entry : matrixPerRotation.entrySet()) {
      maskPerDegree.put(entry.getKey(), spy(new DummyMask(entry.getValue())));
    }
    final DummyMask baseMask = maskPerDegree.get(Degree.D0);
    for (final Degree degree : Degree.values()) {
      given(baseMask.rotate(degree)).willReturn(maskPerDegree.get(degree));
    }

    // when
    final DirectionalFilter<DummyMask> gradientFilter = DirectionalFilter.newInstance(baseMask);
    final double[][] newPixels = gradientFilter.apply(pixels);

    // then
    // Verify that it uses all the mask rotations
    //noinspection ResultOfMethodCallIgnored
    maskPerDegree.values().forEach(mask -> verify(mask, atLeastOnce()).applyMaskTo(any(double[][].class), anyInt(), anyInt()));

    // Verify that it gets all the max abs pixel values at the newPixels matrix.
    for (int y = 0; y < newPixels.length; y++) {
      for (int x = 0; x < newPixels[0].length; x++) {
        final double expectedValue = Math.abs(pixels[y][x]) * maxAbsMask[y][x];
        assertTrue(equal(expectedValue, newPixels[y][x]));
      }
    }
  }

  private Map<Degree, double[][]> buildMatrixPerRotation(final int width, final int height) {
    final Map<Degree, double[][]> matrixPerRotation = new EnumMap<>(Degree.class);
    for (final Degree degree : Degree.values()) {
      matrixPerRotation.put(degree, TEST_HELPER.buildRandomMatrix(width, height));
    }
    return matrixPerRotation;
  }

  private double[][] getMaxAbsMatrixFromAllMatrices(final Collection<double[][]> allMatrices,
                                                     final int matrixSize) {
    final double[][] maxAbsMatrix = new double[matrixSize][matrixSize];
    for(final double[][] matrix : allMatrices) {
      for (int y = 0; y < matrix.length; y++) {
        for (int x = 0; x < matrix[y].length; x++) {
          final double value = Math.abs(matrix[y][x]);
          if (value > maxAbsMatrix[y][x]) {
            maxAbsMatrix[y][x] = value;
          }
        }
      }
    }
    return maxAbsMatrix;
  }

  private static class DummyMask extends AbstractMask implements RotatableMask<DummyMask> {

    private DummyMask(final double[][] mask) {
      super(mask.length, mask.length);
      this.mask = mask;
    }

    @Override
    protected void initializeMask() {
      // Already initialized
    }

    @Override
    protected double applyMaskTo(final double[][] pixels, final int currCoreX,
                                 final int currCoreY) {
      return pixels[currCoreY][currCoreX] * mask[currCoreY][currCoreX];
    }

    @Override
    public DummyMask rotate(final Degree degree) {
      return new DummyMask(MatrixRotator.INSTANCE.rotate(mask, degree));
    }
  }
}
package ar.edu.itba.ati.idp.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class MatrixRotatorTest {
  private static final double[][] ODD_PIXELS = new double[][] {
      {1 ,2 ,3 ,4 , 5},
      {16,17,18,19, 6},
      {15,24,25,20, 7},
      {14,23,22,21, 8},
      {13,12,11,10, 9}
  };

  private static final Map<Degree, double[][]> EXPECTED_PIXELS_PER_DEGREE_ODD_MATRIX;
  private static final Map<Degree, double[][]> EXPECTED_PIXELS_PER_DEGREE_EVEN_MATRIX;

  private Object[] oddMatrixAllRotations() {
    final List<Object> parameters = new LinkedList<>();
    EXPECTED_PIXELS_PER_DEGREE_ODD_MATRIX.forEach((key, value) -> parameters.add(new Object[]{key, value}));
    return parameters.toArray();
  }

  private Object[] illegalMatrices() {
    return new Object[] {
        new Object[] {
            Degree.D0, new double[][] {
            {1  ,2  ,3  ,4 },
            {12 ,13 ,14 ,5 },
            {11 ,16 ,15 ,6 },
            {10 ,9  ,8  ,7 }
        }
        },
        new Object[] {
            Degree.D0, new double[][] {
            {1 ,2 ,3 ,4 , 5},
            {16,17,18,19, 6},
            {15,24,25,20, 7},
            {14,23,22,21, 8},
        }
        }
    };
  }

  @Test
  @Parameters(method = "oddMatrixAllRotations")
  @TestCaseName("Odd Matrix - Degree: {0}")
  public void oddMatrixAllRotationsTest(final Degree degree, final double[][] expectedNewPixels) {
    final double[][] newPixels = MatrixRotator.INSTANCE.rotate(ODD_PIXELS, degree);
    testAllPixels(newPixels, expectedNewPixels);
  }

  private void testAllPixels(final double[][] newPixels, final double[][] expectedNewPixels) {
    for (int y = 0; y < newPixels.length; y++) {
      for (int x = 0; x < newPixels[y].length; x++) {
        assertEquals(expectedNewPixels[y][x], newPixels[y][x], 1e-10);
      }
    }
  }

  @Test
  @Parameters(method = "illegalMatrices")
  @TestCaseName("Illegal Matrices")
  public void illegalMatricesTest(final Degree degree, final double[][] pixelsToRotate) {
    try {
      MatrixRotator.INSTANCE.rotate(pixelsToRotate, degree);
      fail(); // Exception expected.
    } catch (final Exception ignored) {
    }
  }

  static {
    final Map<Degree, double[][]> oddMap = new EnumMap<>(Degree.class);
    oddMap.put(Degree.D0, new double[][] {
        {1 ,2 ,3 ,4 , 5},
        {16,17,18,19, 6},
        {15,24,25,20, 7},
        {14,23,22,21, 8},
        {13,12,11,10, 9}
    });
    oddMap.put(Degree.D45, new double[][] {
        {15.0,16.0,1.0,2.0,3.0},
        {14.0,24.0,17.0,18.0,4.0},
        {13.0,23.0,25.0,19.0,5.0},
        {12.0,22.0,21.0,20.0,6.0},
        {11.0,10.0,9.0,8.0,7.0}
    });
    oddMap.put(Degree.D90, new double[][] {
        {13.0,14.0,15.0,16.0,1.0},
        {12.0,23.0,24.0,17.0,2.0},
        {11.0,22.0,25.0,18.0,3.0},
        {10.0,21.0,20.0,19.0,4.0},
        {9.0,8.0,7.0,6.0,5.0}
    });
    oddMap.put(Degree.D135, new double[][] {
        {11.0,12.0,13.0,14.0,15.0},
        {10.0,22.0,23.0,24.0,16.0},
        {9.0,21.0,25.0,17.0,1.0},
        {8.0,20.0,19.0,18.0,2.0},
        {7.0,6.0,5.0,4.0,3.0}
    });
    oddMap.put(Degree.D180, new double[][] {
        {9.0,10.0,11.0,12.0,13.0},
        {8.0,21.0,22.0,23.0,14.0},
        {7.0,20.0,25.0,24.0,15.0},
        {6.0,19.0,18.0,17.0,16.0},
        {5.0,4.0,3.0,2.0,1.0}
    });
    oddMap.put(Degree.D225, new double[][] {
        {7.0,8.0,9.0,10.0,11.0},
        {6.0,20.0,21.0,22.0,12.0},
        {5.0,19.0,25.0,23.0,13.0},
        {4.0,18.0,17.0,24.0,14.0},
        {3.0,2.0,1.0,16.0,15.0}
    });
    oddMap.put(Degree.D270, new double[][] {
        {5.0,6.0,7.0,8.0,9.0},
        {4.0,19.0,20.0,21.0,10.0},
        {3.0,18.0,25.0,22.0,11.0},
        {2.0,17.0,24.0,23.0,12.0},
        {1.0,16.0,15.0,14.0,13.0}
    });
    oddMap.put(Degree.D315, new double[][] {
        {3.0,4.0,5.0,6.0,7.0},
        {2.0,18.0,19.0,20.0,8.0},
        {1.0,17.0,25.0,21.0,9.0},
        {16.0,24.0,23.0,22.0,10.0},
        {15.0,14.0,13.0,12.0,11.0}
    });

    EXPECTED_PIXELS_PER_DEGREE_ODD_MATRIX = Collections.unmodifiableMap(oddMap);

    final Map<Degree, double[][]> evenMap = new EnumMap<>(Degree.class);
    evenMap.put(Degree.D0, new double[][] {
        {1  ,2  ,3  ,4 },
        {12 ,13 ,14 ,5 },
        {11 ,16 ,15 ,6 },
        {10 ,9  ,8  ,7 }
    });
    evenMap.put(Degree.D45, new double[][] {
        {12 ,1 ,2  ,3 },
        {11 ,13 ,14 ,4 },
        {10 ,16 ,15 ,5 },
        {9  ,8  ,7  ,6 }
    });
    evenMap.put(Degree.D90, new double[][] {
        {10 ,11 ,12 ,1 },
        {9  ,16 ,13 ,2 },
        {8  ,15 ,14 ,3 },
        {7  ,6  ,5  ,4 }
    });
    evenMap.put(Degree.D135, new double[][] {
        {8  ,9  ,10 ,11 },
        {7  ,16 ,13 ,12 },
        {6  ,15 ,14 ,1  },
        {5  ,4  ,3  ,2  }
    });
    evenMap.put(Degree.D180, new double[][] {
        {7  ,8  ,9  ,10 },
        {6  ,15 ,16 ,11 },
        {5  ,14 ,13 ,12 },
        {4  ,3  ,2  ,1  }
    });
    evenMap.put(Degree.D225, new double[][] {
        {5  ,6  ,7  ,8  },
        {4  ,15 ,16 ,9  },
        {3  ,14 ,13 ,10 },
        {2  ,1  ,12 ,11 }
    });
    evenMap.put(Degree.D270, new double[][] {
        {4  ,5  ,6  ,7  },
        {3  ,14 ,15 ,8  },
        {2  ,13 ,16 ,9  },
        {1  ,12 ,11 ,10 }
    });
    evenMap.put(Degree.D315, new double[][] {
        {2  ,3  ,4  ,5  },
        {1  ,14 ,15 ,6  },
        {12 ,13 ,16 ,7  },
        {11 ,10 ,9  ,8 }
    });
    EXPECTED_PIXELS_PER_DEGREE_EVEN_MATRIX = Collections.unmodifiableMap(evenMap);
  }
}

package ar.edu.itba.ati.idp.function.filter.mask.linear;

import static ar.edu.itba.ati.idp.TestHelper.TEST_HELPER;
import static org.junit.Assert.assertEquals;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class LoGMaskTest {
  @SuppressWarnings("unused") // Used by JUnitParams at `maskTest` method.
  public Object[] allMasksToTest() {
    return new Object[] {
        new Object[] {1, EXPECTED_MASK_SIGMA_1},
    };
  }

  @Test
  @Parameters(method = "allMasksToTest")
  @TestCaseName("Sigma: {0}")
  public void maskTest(final double sigma, final double[][] expectedMask) {
    final LoGMask loGMask = LoGMask.newInstance(sigma);
    final double[][] mask = loGMask.getMask();
    final int expectedHeight = expectedMask.length;
    final int expectedWidth = expectedMask[0].length;

    assertEquals(expectedHeight, mask.length);
    assertEquals(expectedWidth, mask[0].length);

    for (int y = 0; y < expectedHeight; y++) {
      for (int x = 0; x < expectedWidth; x++) {
        TEST_HELPER.assertEqual(expectedMask[y][x], mask[y][x], x, y);
      }
    }
  }

  @Test
  public void bigSigmaTest() { // New size: 6 * sigma + 1, without any sigma bound restriction.
    // Odd case
    LoGMask loGMask = LoGMask.newInstance(11);
    double[][] mask = loGMask.getMask();
    int expectedSize = 67;

    assertEquals(expectedSize, mask.length);
    assertEquals(expectedSize, mask[0].length);

    // Even case
    loGMask = LoGMask.newInstance(12);
    mask = loGMask.getMask();
    expectedSize = 73;

    assertEquals(expectedSize, mask.length);
    assertEquals(expectedSize, mask[0].length);
  }

  // Calculated with octave
  /*
   * LoGMask.m
   *
   format long

   function res = LoG(x,y,sigma)
     res = - 1/(sqrt(2*pi)*sigma^3) * (2-(x^2+y^2)/sigma^2) * exp(-(x^2+y^2)/(2*sigma^2));
   endfunction

   function res = mask(size, sigma)
     a = zeros(7,7);
     k = floor(size/2);
     o = k+1;
     for y=-k:k
       for x=-k:k
         a(y+o, x+o) = LoG(x, y, sigma);
       endfor
     endfor

     res = a;
   endfunction

   sigma = 1;
   size = 7;
   mask(size, sigma)

   ans =

   0.000787734218660   0.006597640061005   0.021504415528313   0.031022938883566   0.021504415528313   0.006597640061005   0.000787734218660
   0.006597640061005   0.043841296471685   0.098241529613300   0.107981933026376   0.098241529613300   0.043841296471685   0.006597640061005
   0.021504415528313   0.098241529613300  -0.000000000000000  -0.241970724519143  -0.000000000000000   0.098241529613300   0.021504415528313
   0.031022938883566   0.107981933026376  -0.241970724519143  -0.797884560802865  -0.241970724519143   0.107981933026376   0.031022938883566
   0.021504415528313   0.098241529613300  -0.000000000000000  -0.241970724519143  -0.000000000000000   0.098241529613300   0.021504415528313
   0.006597640061005   0.043841296471685   0.098241529613300   0.107981933026376   0.098241529613300   0.043841296471685   0.006597640061005
   0.000787734218660   0.006597640061005   0.021504415528313   0.031022938883566   0.021504415528313   0.006597640061005   0.000787734218660

   sum(sum(a))
   ans = -0.0204831668573477

   This mask also matches the one exhibited in the class presentation: sigma = 1, size = 7x7.

   sigma = 3;
   size = 7;
   mask(size, sigma)

   ans =

  -0.0000000000000000  -0.0039867285912213  -0.0075356213352136  -0.0089618786858942  -0.0075356213352136  -0.0039867285912213  -0.0000000000000000
  -0.0039867285912213  -0.0105265006711491  -0.0161662685325689  -0.0184044118407940  -0.0161662685325689  -0.0105265006711491  -0.0039867285912213
  -0.0075356213352136  -0.0161662685325689  -0.0235054642069323  -0.0264012957645304  -0.0235054642069323  -0.0161662685325689  -0.0075356213352136
  -0.0089618786858942  -0.0184044118407940  -0.0264012957645304  -0.0295512800297358  -0.0264012957645304  -0.0184044118407940  -0.0089618786858942
  -0.0075356213352136  -0.0161662685325689  -0.0235054642069323  -0.0264012957645304  -0.0235054642069323  -0.0161662685325689  -0.0075356213352136
  -0.0039867285912213  -0.0105265006711491  -0.0161662685325689  -0.0184044118407940  -0.0161662685325689  -0.0105265006711491  -0.0039867285912213
  -0.0000000000000000  -0.0039867285912213  -0.0075356213352136  -0.0089618786858942  -0.0075356213352136  -0.0039867285912213  -0.0000000000000000

   */

  private static final double[][] EXPECTED_MASK_SIGMA_1 = new double[][] {
      { 0.000787734218660, 0.006597640061005, 0.021504415528313, 0.031022938883566, 0.021504415528313, 0.006597640061005, 0.000787734218660 },
      { 0.006597640061005, 0.043841296471685, 0.098241529613300, 0.107981933026376, 0.098241529613300, 0.043841296471685, 0.006597640061005 },
      { 0.021504415528313, 0.098241529613300,-0.000000000000000,-0.241970724519143,-0.000000000000000, 0.098241529613300, 0.021504415528313 },
      { 0.031022938883566, 0.107981933026376,-0.241970724519143,-0.797884560802865,-0.241970724519143, 0.107981933026376, 0.031022938883566 },
      { 0.021504415528313, 0.098241529613300,-0.000000000000000,-0.241970724519143,-0.000000000000000, 0.098241529613300, 0.021504415528313 },
      { 0.006597640061005, 0.043841296471685, 0.098241529613300, 0.107981933026376, 0.098241529613300, 0.043841296471685, 0.006597640061005 },
      { 0.000787734218660, 0.006597640061005, 0.021504415528313, 0.031022938883566, 0.021504415528313, 0.006597640061005, 0.000787734218660 },
  };

  /*
    Another function. This one has been found in the paper:
    https://www.researchgate.net/profile/Hatice_Cinar_Akakin/publication/260586629_A_Generalized_Laplacian_of_Gaussian_Filter_for_Blob_Detection_and_Its_Applications/links/561b60c108aea8036722beea/A-Generalized-Laplacian-of-Gaussian-Filter-for-Blob-Detection-and-Its-Applications.pdf

     function res = LoG(x,y,sigma)
       res = (x^2+y^2 - 2*sigma^2)/(pi * sigma^4) * exp(-(x^2+y^2)/(2*sigma^2));
     endfunction

     Compared to the one explained in class, this one has higher module values for each cell for < 1 sigma values,
     but lower values for > 1 sigma values (approx.).
   */
}
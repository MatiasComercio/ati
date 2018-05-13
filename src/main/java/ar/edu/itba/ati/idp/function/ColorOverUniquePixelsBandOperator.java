package ar.edu.itba.ati.idp.function;

/**
 * This interface should be used when there's an explicit need of applying the
 * underneath implemented method to a grey scale image only, and returning a color result.
 * All bands will be compressed to one unique band and that's the pixels matrix that will
 * be passed to the {@link #apply(double[][])} method.
 */
@FunctionalInterface
public interface ColorOverUniquePixelsBandOperator {
  double[][][] apply(double[][] m);
}

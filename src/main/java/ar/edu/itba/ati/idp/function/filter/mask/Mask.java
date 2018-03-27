package ar.edu.itba.ati.idp.function.filter.mask;

public interface Mask {
  double apply(double[][] pixels, int currCoreX, int currCoreY);
}

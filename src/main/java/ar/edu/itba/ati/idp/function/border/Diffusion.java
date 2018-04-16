package ar.edu.itba.ati.idp.function.border;

import ar.edu.itba.ati.idp.function.filter.DiffusionFilter;
import ar.edu.itba.ati.idp.function.filter.DiffusionFilter.DiffusionBorderDetector;

public class Diffusion {

  public static DiffusionFilter newIsometric(final int times) {
    return DiffusionFilter.newInstance(DiffusionBorderDetector.newIsometricDetector(), times);
  }

  public static DiffusionFilter newLeclerc(final int times, final double sigma) {
    return DiffusionFilter.newInstance(DiffusionBorderDetector.newLeclercDetector(sigma), times);
  }

  public static DiffusionFilter newLorentz(final int times, final double sigma) {
    return DiffusionFilter.newInstance(DiffusionBorderDetector.newLorentzDetector(sigma), times);
  }

  public static DiffusionFilter newMin(final int times, final double sigma) {
    return DiffusionFilter.newInstance(DiffusionBorderDetector.newMinDetector(sigma), times);
  }
}

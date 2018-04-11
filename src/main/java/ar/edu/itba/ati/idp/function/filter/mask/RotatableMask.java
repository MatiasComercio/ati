package ar.edu.itba.ati.idp.function.filter.mask;

import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;

public interface RotatableMask<T extends RotatableMask<T>> extends Mask {
  T rotate(Degree degree);
}

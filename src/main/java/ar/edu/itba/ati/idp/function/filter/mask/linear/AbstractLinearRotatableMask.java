package ar.edu.itba.ati.idp.function.filter.mask.linear;

import ar.edu.itba.ati.idp.function.filter.mask.RotatableMask;
import ar.edu.itba.ati.idp.utils.ArrayUtils;
import ar.edu.itba.ati.idp.utils.MatrixRotator;
import ar.edu.itba.ati.idp.utils.MatrixRotator.Degree;

public abstract class AbstractLinearRotatableMask<T extends AbstractLinearRotatableMask<T>> extends AbstractLinearMask implements RotatableMask<T> {
  @SuppressWarnings("WeakerAccess") // Explicitly desired to be protected (it may be subclassed outside its package).
  protected AbstractLinearRotatableMask(final double[][] mask) {
    super(mask.length, mask[0].length);
    this.mask = mask;
  }

  @Override
  protected void initializeMask() {
    // Already assigned at constructor.
  }

  @Override
  public T rotate(final Degree degree) {
    if (mask.length != mask[0].length || mask.length % 2 == 0) {
      throw new IllegalStateException("This subclass has a non rotatable mask, but implements RotatableMask. Aborting...");
    }
    initializeMaskIfNecessary();
    return newInstance(MatrixRotator.INSTANCE.rotate(mask, degree));
  }

  protected abstract T newInstance(final double[][] mask);

  /* testing-only */ double[][] getMask() {
    return ArrayUtils.copyOf(mask);
  }
}

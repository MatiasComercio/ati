package ar.edu.itba.ati.idp.model;

import java.util.Objects;

@FunctionalInterface
public interface IntBinaryPredicate {

  boolean test(final int value1, final int value2);

  default IntBinaryPredicate and(IntBinaryPredicate other) {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) && other.test(v1, v2);
  }

  default IntBinaryPredicate or(IntBinaryPredicate other) {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) || other.test(v1, v2);
  }

  default IntBinaryPredicate negate() {
    return (v1, v2) -> !test(v1, v2);
  }
}

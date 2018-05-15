package ar.edu.itba.ati.idp.ui.component;

public class InputExtractors {
  private static final double EPSILON = 1e-9;

  private static final InputExtractor<Integer> INT_IE = textField -> {
    Integer value;
    try {
      value = Integer.parseInt(textField);
    } catch (final NumberFormatException exception) {
      value = null; // Not an Int
    }

    return value;
  };

  private static final InputExtractor<Integer> POSITIVE_INT_IE = textField -> {
    final Integer value = INT_IE.getValue(textField);

    return value != null && value > 0 ? value : null;
  };

  private static final InputExtractor<Integer> NON_NEGATIVE_INT_IE = textField -> {
    final Integer value = INT_IE.getValue(textField);

    return value != null && value >= 0 ? value : null;
  };

  private static final InputExtractor<Double> DOUBLE_IE = textField -> {
    Double value;
    try {
      value = Double.parseDouble(textField);
    } catch (final NumberFormatException exception) {
      value = null; // Not a Double
    }

    return value;
  };

  private static final InputExtractor<Double> NON_NEGATIVE_DOUBLE_IE = textField -> {
    final Double value = DOUBLE_IE.getValue(textField);

    return value != null && value >= 0 ? value : null;
  };

  private static final InputExtractor<Double> POSITIVE_DOUBLE_IE = textField -> {
    final Double value = DOUBLE_IE.getValue(textField);

    return value != null && value - EPSILON > 0 ? value : null;
  };


  public static InputExtractor<Double> getDoubleIE() {
    return DOUBLE_IE;
  }

  public static InputExtractor<Double> getNonNegativeDoubleIE() {
    return NON_NEGATIVE_DOUBLE_IE;
  }

  public static InputExtractor<Double> getPositiveDoubleIE() {
    return POSITIVE_DOUBLE_IE;
  }

  public static InputExtractor<Integer> getIntIE() {
    return INT_IE;
  }

  public static InputExtractor<Integer> getNonNegativeIntIE() {
    return NON_NEGATIVE_INT_IE;
  }

  public static InputExtractor<Integer> getPositiveIntIE() {
    return POSITIVE_INT_IE;
  }
}

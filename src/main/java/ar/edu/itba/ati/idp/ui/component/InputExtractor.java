package ar.edu.itba.ati.idp.ui.component;

public interface InputExtractor<T> {
  T getValue(String textField);

  default boolean isValid(final String textField) {
    return getValue(textField) != null;
  }
}

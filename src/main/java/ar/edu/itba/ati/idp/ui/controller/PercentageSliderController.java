package ar.edu.itba.ati.idp.ui.controller;

import ar.edu.itba.ati.idp.utils.ResourceLoader;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class PercentageSliderController extends HBox {

  private static final String LAYOUT_PATH = "ui/control/percentageSlider.fxml";

  @FXML
  private Slider slider;

  @FXML
  private Label sliderText;

  private final IntegerProperty sliderValueProperty;

  public PercentageSliderController() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);

    this.sliderValueProperty = new SimpleIntegerProperty((int) slider.getValue());
    this.slider.valueProperty().bindBidirectional(sliderValueProperty);
    this.sliderText.textProperty().bind(sliderValueProperty.asString());
  }

  public IntegerProperty percentageProperty() {
    return sliderValueProperty;
  }

  public int getPercentage() {
    return percentageProperty().get();
  }

  public double getPercentageAsDouble() {
    return percentageProperty().get() / 100.0;
  }
}

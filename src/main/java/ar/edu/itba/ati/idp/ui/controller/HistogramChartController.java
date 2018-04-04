package ar.edu.itba.ati.idp.ui.controller;

import ar.edu.itba.ati.idp.model.ImageHistogram;
import ar.edu.itba.ati.idp.model.ImageHistogram.BandHistogram;
import ar.edu.itba.ati.idp.model.ImageMatrix.Band;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Based on: https://docs.oracle.com/javafx/2/charts/bar-chart.htm
public class HistogramChartController extends VBox {
  private static final String STAGE_TITLE = "Histogram";
  private static final int STAGE_PREF_WIDTH = 300;
  private static final int CHART_PREF_WIDTH = 200;
  private static final int CHART_PREF_HEIGHT = 225;
  private static final int NO_GAP = 0;
//  private static final String X_AXIS_LABEL = "Pixel Value";
//  private static final String Y_AXIS_LABEL = "# Pixels";
  private static final String BAR_COLOR_PROPERTY = "-fx-bar-fill: %s;";

  private Stage stage;

  public HistogramChartController() {
    setAlignment(Pos.CENTER);
    stage = new Stage(StageStyle.UTILITY);
    stage.setScene(new Scene(this));
    stage.setWidth(STAGE_PREF_WIDTH);
    stage.setOnCloseRequest(windowEvent -> clearHistogramCharts());
  }

  public void show(final ImageHistogram imageHistogram, final String imageName) {
    clearHistogramCharts();
    // One histogram chart per pixel band
    imageHistogram.getBandHistograms().forEach(this::showBandHistogramChart);
    stage.setTitle(STAGE_TITLE + " - " + imageName);
    stage.show();
  }

  // Many thanks to:
  // https://stackoverflow.com/questions/44369495/javafx-barchart-set-series-color-by-series-name
  private void showBandHistogramChart(final BandHistogram bandHistogram) {
    // Build & configure chart & axis
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final BarChart<String, Number> histogramChart = new BarChart<>(xAxis, yAxis);
    histogramChart.setBarGap(NO_GAP);
    histogramChart.setCategoryGap(NO_GAP);
    histogramChart.setLegendVisible(false);
    histogramChart.setPrefSize(CHART_PREF_WIDTH, CHART_PREF_HEIGHT);
//    xAxis.setLabel(X_AXIS_LABEL);
//    yAxis.setLabel(Y_AXIS_LABEL);
    yAxis.setForceZeroInRange(true);
    // Build histogram data series
    final Series<String, Number> series = new Series<>();
    final Band band = bandHistogram.getBand();
    final Map<Integer, Integer> plainHistogram = bandHistogram.getPlainHistogram();
    plainHistogram.forEach((key, value) -> series.getData().add(new Data<>(String.valueOf(key), value)));
    histogramChart.getData().add(series);
    final String barStyle = String.format(BAR_COLOR_PROPERTY, band.getHexColor());
    series.getData().forEach(data -> data.getNode().setStyle(barStyle));
    series.setName(band.toString());
    getChildren().add(histogramChart);
  }

  private void clearHistogramCharts() {
    getChildren().clear();
  }
}

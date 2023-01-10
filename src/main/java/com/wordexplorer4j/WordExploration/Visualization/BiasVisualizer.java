package com.wordexplorer4j.WordExploration.Visualization;

import java.util.Arrays;
import java.util.List;

import com.wordexplorer4j.WordExploration.BiasExploration.ProjectedWord;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class BiasVisualizer extends Application implements Visualizer{

    private String[] words;
    private double[] projections;
    private String leftSide;
    private String rightSide;

    public BiasVisualizer() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {}

    public BiasVisualizer(List<ProjectedWord> proyectedWords, List<String> kernelLeft, List<String> kernelRight) {
        words = new String[proyectedWords.size()];
        projections = new double[proyectedWords.size()];
        for (int i = 0; i < proyectedWords.size(); ++i) {
            words[i] = proyectedWords.get(i).getWord();
            projections[i] = proyectedWords.get(i).getProjection();
        }

        leftSide = String.join(", ", kernelRight);
        rightSide = String.join(", ", kernelLeft);
    }
    
    @Override
    public void plot(Stage primaryStage) throws Exception {
        BarChart<Number, String> barChart = getBarPlot();

        Scene scene = new Scene(barChart, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BarChart<Number, String> getBarPlot() {
        NumberAxis xAxis = new NumberAxis(-1, 1, 0.1);
        CategoryAxis yAxis = new CategoryAxis();
        xAxis.setTickLabelRotation(90);

        yAxis.setLabel("Words");
        xAxis.setLabel("← "+ leftSide + "      " + rightSide + " →");

        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<Number, String> posSerie = new XYChart.Series<>();
        XYChart.Series<Number, String> negSerie = new XYChart.Series<>();
        for (int i = 0; i < words.length; ++i) {
            double projection = projections[i];
            XYChart.Data<Number, String> data = new XYChart.Data<Number, String>(projection, words[i]);
            if (projection < 0.0) {
                negSerie.getData().add(data);
            } else {
                posSerie.getData().add(data);
            }
        }
        barChart.getData().addAll(Arrays.asList(negSerie, posSerie));
        return barChart;
    }
}

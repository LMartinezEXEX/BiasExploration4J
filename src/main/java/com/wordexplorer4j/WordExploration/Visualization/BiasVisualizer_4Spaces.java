package com.wordexplorer4j.WordExploration.Visualization;

import java.util.List;

import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class BiasVisualizer_4Spaces extends BiasVisualizer {

    private double[][] projections;
    private String leftSide;
    private String rightSide;
    private String upSide;
    private String downSide;
    
    public BiasVisualizer_4Spaces(List<String> words, double[][] projections, List<String> kernelLeft, List<String> kernelRight, List<String> kernelUp, List<String> kernelDown) {
        super(words.toArray(String[]::new));
        this.projections = projections;

        leftSide = String.join(", ", kernelRight);
        rightSide = String.join(", ", kernelLeft);
        upSide = String.join(", ", kernelUp);
        downSide = String.join(", ", kernelDown);
    }

    @Override
    public void plot(Stage primaryStage) throws Exception {
        ScatterChart<Number, Number> scatterChart = getScatterPlot();

        Scene scene = new Scene(scatterChart, 500, 400);
        scene.getStylesheets().add("com/wordexplorer4j/WordExploration/Styles/Chart.css");
        primaryStage.setScene(scene);
        primaryStage.show();   
    }

    private ScatterChart<Number, Number> getScatterPlot() {
        NumberAxis xAxis = new NumberAxis(-1, 1, 0.1);
        NumberAxis yAxis = new NumberAxis(-1, 1, 0.1);

        yAxis.setLabel("← "+ downSide + "      " + upSide + " →");
        xAxis.setLabel("← "+ leftSide + "      " + rightSide + " →");

        xAxis.setTickLabelsVisible(false);
        yAxis.setTickLabelsVisible(false);
        
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setLegendVisible(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < super.getWords().length; ++i) {
            XYChart.Data<Number, Number> data = new XYChart.Data<Number, Number>(projections[0][i], projections[1][i]);
            data.setNode(new LabeledNode(super.getWords()[i]));

            series.getData().add(data);
        }
        scatterChart.getData().add(series);
        return scatterChart;
    }
}

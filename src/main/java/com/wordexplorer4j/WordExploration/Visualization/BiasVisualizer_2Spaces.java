package com.wordexplorer4j.WordExploration.Visualization;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.wordexplorer4j.WordExploration.BiasExploration.ProjectedWord;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class BiasVisualizer_2Spaces extends BiasVisualizer{

    private double[] projections;
    private String leftSide;
    private String rightSide;
    
    public BiasVisualizer_2Spaces(List<ProjectedWord> proyectedWords, List<String> kernelLeft, List<String> kernelRight) {
        super(proyectedWords.stream().map(ProjectedWord::getWord).toArray(String[]::new));

        if (Objects.isNull(kernelLeft) || Objects.isNull(kernelRight)) {
            throw new IllegalArgumentException("Kernels list definition can not be null");
        }

        String[] words = new String[proyectedWords.size()];
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
        if (Objects.isNull(primaryStage)) {
            throw new IllegalArgumentException("Stage to put chart into can not be null");
        }

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
        for (int i = 0; i < super.getWords().length; ++i) {
            double projection = projections[i];
            XYChart.Data<Number, String> data = new XYChart.Data<Number, String>(projection, super.getWords()[i]);
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

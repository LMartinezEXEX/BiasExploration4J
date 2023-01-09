package com.wordexplorer4j.WordExploration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Visualizer extends Application{
    public static List<Word> wordsToPlot;

    private List<Word> words;

    public Visualizer() {
        this.words = new ArrayList<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {}

    Visualizer(List<Word> words) {
        this.words = words;
    }
    
    public void plot(Stage primaryStage) throws Exception {
        ScatterChart<Number, Number> scatterChart = getScatterPlot();

        Scene scene = new Scene(scatterChart, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    private ScatterChart<Number, Number> getScatterPlot() {
        NumberAxis xAxis = getAxis(0);
        NumberAxis yAxis = getAxis(1);

        xAxis.setLabel("x");
        yAxis.setLabel("y");
        
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Word Embeddings");
        for (Word w : words) {

            XYChart.Data<Number, Number> data = new XYChart.Data<Number, Number>(w.getPcaCoord(0), w.getPcaCoord(1));

            XYChart.Data<Number, Number> dataLabel = new XYChart.Data<Number, Number>(w.getPcaCoord(0), w.getPcaCoord(1) + 0.1);
            dataLabel.setNode(new Text(w.getWord()));

            series.getData().addAll(Arrays.asList(data, dataLabel));
        }
        scatterChart.getData().add(series);

        return scatterChart;
    }

    private NumberAxis getAxis(int i) {
        int min = (int) Math.floor(words.stream().map(w -> w.getPcaCoord(i)).min(Comparator.naturalOrder()).get());
        int max = (int) Math.ceil(words.stream().map(w -> w.getPcaCoord(i)).max(Comparator.naturalOrder()).get());

        return new NumberAxis(min, max, 1);
    }
    
}

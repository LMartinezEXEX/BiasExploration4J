package com.wordexplorer4j.WordExploration.Visualization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.wordexplorer4j.WordExploration.Word;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class WordExplorerVisualizer extends Application implements Visualizer{
    public static List<Word> wordsToPlot;

    private List<Word> words;

    public WordExplorerVisualizer() {
        this.words = new ArrayList<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {}

    public WordExplorerVisualizer(List<Word> words) {
        this.words = words;
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
        NumberAxis xAxis = getAxis(0);
        NumberAxis yAxis = getAxis(1);

        xAxis.setTickLabelsVisible(false);
        yAxis.setTickLabelsVisible(false);
        
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Word Embeddings");
        for (Word w : words) {

            XYChart.Data<Number, Number> data = new XYChart.Data<Number, Number>(w.getPcaCoord(0), w.getPcaCoord(1));
            data.setNode(new LabeledNode(w.getWord()));

            series.getData().add(data);
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

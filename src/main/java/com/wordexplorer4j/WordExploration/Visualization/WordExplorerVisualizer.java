package com.wordexplorer4j.WordExploration.Visualization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.wordexplorer4j.WordExploration.Word;

import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class WordExplorerVisualizer extends Visualizer{

    private List<Word> words;

    public WordExplorerVisualizer() {
        this.words = new ArrayList<>();
    }

    public WordExplorerVisualizer(List<Word> words) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        }

        this.words = Collections.unmodifiableList(words);
    }
    
    @Override
    public void plot(Stage primaryStage) throws Exception {
        if (Objects.isNull(primaryStage)) {
            throw new IllegalArgumentException("Stage to put chart into can not be null");
        }

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
        scatterChart.setLegendVisible(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (Word w : words) {

            XYChart.Data<Number, Number> data = new XYChart.Data<Number, Number>(w.getPca(0), w.getPca(1));
            data.setNode(new LabeledNode(w.getWord()));

            series.getData().add(data);
        }
        scatterChart.getData().add(series);
        return scatterChart;
    }

    private NumberAxis getAxis(int i) {
        if (words.size() == 0) {
            return new NumberAxis(-1, 1, 0.1);
        }

        int min = (int) Math.floor(words.stream().map(w -> w.getPca(i)).min(Comparator.naturalOrder()).get());
        int max = (int) Math.ceil(words.stream().map(w -> w.getPca(i)).max(Comparator.naturalOrder()).get());

        return new NumberAxis(min, max, 1);
    }
}

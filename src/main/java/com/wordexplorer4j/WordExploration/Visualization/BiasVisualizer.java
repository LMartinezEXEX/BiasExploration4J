package com.wordexplorer4j.WordExploration.Visualization;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class BiasVisualizer extends Application implements Visualizer{

    private String[] words;

    public BiasVisualizer() {
    }

    public BiasVisualizer(String[] words) {
        this.words = words;
    }

    public String[] getWords() {
        return words;
    }

    abstract public void plot(Stage primaryStage) throws Exception;

    @Override
    public void start(Stage primaryStage) throws Exception {}
}

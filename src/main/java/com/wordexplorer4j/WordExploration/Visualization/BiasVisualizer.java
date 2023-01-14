package com.wordexplorer4j.WordExploration.Visualization;

import java.util.Objects;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class BiasVisualizer extends Application implements Visualizer{

    private String[] words;

    public BiasVisualizer() {
    }

    public BiasVisualizer(String[] words) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        }

        this.words = words.clone();
    }

    public String[] getWords() {
        return words.clone();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {}
}

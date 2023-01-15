package com.wordexplorer4j.WordExploration.Visualization;

import java.util.List;
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

    protected static <T> List<T> nonNull(List<T> nonNull) {
        if (Objects.isNull(nonNull)) {
            throw new IllegalArgumentException("List of words can not be null");
        }

        return nonNull;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {}

    public String[] getWords() {
        return words.clone();
    }
}

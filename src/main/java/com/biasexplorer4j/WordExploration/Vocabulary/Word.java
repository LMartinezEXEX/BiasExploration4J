package com.biasexplorer4j.WordExploration.Vocabulary;

import java.util.Objects;

public class Word {
    
    private String word;
    private double[] projections;

    public Word(String word, double[] projections) {
        this.word = word;
        this.projections = projections;
    }

    public String getWord() {
        return word;
    }

    public double[] getProjections() {
        if (Objects.isNull(projections)) {
            return null;
        }
        return projections.clone();
    }
}

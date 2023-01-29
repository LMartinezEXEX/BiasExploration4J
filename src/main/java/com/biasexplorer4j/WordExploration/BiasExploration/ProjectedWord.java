package com.biasexplorer4j.WordExploration.BiasExploration;

import com.biasexplorer4j.WordExploration.WordToPlot;

public class ProjectedWord implements WordToPlot, Comparable<ProjectedWord> {

    private String word;
    private double[] projection;

    public ProjectedWord(String word, double[] projection) {
        this.word = word;
        this.projection = projection;
    }

    public String getWord() {
        return word;
    }

    public double[] getProjection() {
        return projection;
    }

    @Override
    public int compareTo(ProjectedWord o) {
        return Double.compare(projection[0], o.getProjection()[0]);
    }

    @Override
    public String getToken() {
        return this.getWord();
    }

    @Override
    public double[] getProjectionToPlot() {
        return this.projection;
    }
}

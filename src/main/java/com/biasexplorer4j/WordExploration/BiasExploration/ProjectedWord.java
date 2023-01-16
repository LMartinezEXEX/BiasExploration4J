package com.biasexplorer4j.WordExploration.BiasExploration;

public class ProjectedWord implements Comparable<ProjectedWord>{
    private String word;
    private double projection;

    public ProjectedWord(String word, double projection) {
        this.word = word;
        this.projection = projection;
    }

    @Override
    public int compareTo(ProjectedWord o) {
        return Double.compare(projection, o.getProjection());
    }

    public String getWord() {
        return word;
    }

    public double getProjection() {
        return projection;
    }
}

package com.biasexplorer4j.WordExploration.BiasExploration;

public class ProjectedWord implements Comparable<ProjectedWord> {

    private String word;
    private double projection;

    public ProjectedWord(String word, double projection) {
        this.word = word;
        this.projection = projection;
    }

    public String getWord() {
        return word;
    }

    public double getProjection() {
        return projection;
    }

    @Override
    public int compareTo(ProjectedWord o) {
        return Double.compare(projection, o.getProjection());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((word == null) ? 0 : word.hashCode());
        long temp;
        temp = Double.doubleToLongBits(projection);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProjectedWord other = (ProjectedWord) obj;
        if (word == null) {
            if (other.word != null)
                return false;
        } else if (!word.equals(other.word))
            return false;
        if (Double.doubleToLongBits(projection) != Double.doubleToLongBits(other.projection))
            return false;
        return true;
    }
}

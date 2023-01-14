package com.wordexplorer4j.PhraseBiasExploration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PseudoLikelihood {
    private List<Double> scores;

    public PseudoLikelihood(List<Double> scores) {
        if (Objects.isNull(scores)) {
            throw new IllegalArgumentException("Scores list can not be null");
        }
        
        this.scores = Collections.unmodifiableList(scores);
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        if (Objects.isNull(keys) || Objects.isNull(values) ) 
            throw new IllegalArgumentException();

        Iterator<K> keyIter = keys.iterator();
        Iterator<V> valIter = values.iterator();
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(_i -> keyIter.next(), _i -> valIter.next()));
    }

    public List<Double> getScores() {
        return new ArrayList<>(scores);
    }

    public List<Double> getPLLScores() {
        List<Double> proportions = getProportions();

        return proportions;
    }

    private List<Double> getProportions() {
        Double min = getMinScoreValue();

        return scores.stream().map(s -> min/s).collect(Collectors.toList());
    }

    private double getMinScoreValue() {
        return scores.stream().min(Comparator.naturalOrder()).get();
    }
}

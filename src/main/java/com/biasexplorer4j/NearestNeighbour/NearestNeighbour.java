package com.biasexplorer4j.NearestNeighbour;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

public class NearestNeighbour {
    private Map<String, Integer> wordsEmbeddingMapping = new HashMap<>();
    private Map<Integer, String> idToWordMapping = new HashMap<>();
    private INDArray embeddings;

    public NearestNeighbour(Map<String, INDArray> labeledPoints) {
        if (Objects.isNull(labeledPoints)) {
            throw new IllegalArgumentException("Labeled Points can not be null");
        }

        int dim = getEmbeddingDimension(new ArrayList<>(labeledPoints.values()));
        this.embeddings = Nd4j.zeros(labeledPoints.size(), dim);

        int idxCount = 0;
        for(Map.Entry<String, INDArray> e : labeledPoints.entrySet()) {
            this.embeddings.putRow(idxCount, e.getValue());
            wordsEmbeddingMapping.put(e.getKey(), idxCount);
            idToWordMapping.put(idxCount, e.getKey());
            idxCount++;
        }
    }

    public Map<String, List<String>> getKNearestNeighbour(List<String> words, int k) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list to calculate neighbours can not be null");
        } else if (k < 0) {
            throw new IllegalArgumentException("Number of neighbours to retrive from words, k, should be greater or equal than zero");
        }

        words = filterNonPresentLabeles(words);

        INDArray distanceMatrix = Nd4j.zeros(words.size(), this.embeddings.size(0));

        // Fill in the distances matrix using Cosine Similarity
        for(int wordIdx = 0; wordIdx < words.size(); wordIdx++) {
            for (int embIdx = 0; embIdx < this.embeddings.size(0); embIdx++) {
                int wordToCalculateIdx = this.wordsEmbeddingMapping.get(words.get(wordIdx));
                INDArray wordToCalculateEmb = this.embeddings.getRow(wordToCalculateIdx);

                INDArray wordToCompareEmb   = this.embeddings.getRow(embIdx); 
                double distance = Transforms.cosineSim(wordToCalculateEmb, wordToCompareEmb);
                distanceMatrix.put(wordIdx, embIdx, distance);
            }
        }

        Map<String, List<String>> wordsNeighbours = new HashMap<>();
        for(int row = 0; row < distanceMatrix.size(0); row++) {
            String word = words.get(row);
            wordsNeighbours.put(word, new ArrayList<>());
            Collection<Integer> indexes = this.getKMinIndexes(distanceMatrix.getRow(row, true), k, row);

            for (int index : indexes) {
                String neighbour = this.idToWordMapping.get(index);
                wordsNeighbours.get(word).add(neighbour);
            }
        }

        return wordsNeighbours;
    }

    private List<String> filterNonPresentLabeles(List<String> labels) {
        return labels.stream()
                    .filter(l -> this.wordsEmbeddingMapping.containsKey(l))
                    .collect(Collectors.toList());
    }

    private Collection<Integer> getKMinIndexes(INDArray features, int k, int currIdx) {
        if (features.size(1) <= k) {
            throw new IllegalArgumentException("Can't retrieve more neighbours than words used to initialize this object");
        }
        INDArray[] sorted = Nd4j.sortWithIndices(features.dup(), 1, false);
        INDArray sorted_idx = sorted[0];

        ArrayList<Integer> indexes = new ArrayList<>(k);
        // The first index is always the same word, so skip the 0-index word.
        for (int i = 1; i < k + 1; i++) {
            indexes.add(sorted_idx.getInt(i));
        }

        return indexes;
    }

    private int getEmbeddingDimension(List<INDArray> arrays) {
        return (int) arrays.get(0).size(1);
    }
}
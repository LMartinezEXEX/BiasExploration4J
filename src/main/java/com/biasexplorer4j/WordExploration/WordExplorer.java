package com.biasexplorer4j.WordExploration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.nd4j.linalg.factory.Nd4j;

import com.biasexplorer4j.NearestNeighbour.NearestNeighbour;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PLOT_TYPE;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PlotManager;
import com.biasexplorer4j.WordExploration.Vocabulary.Vocabulary;
import com.biasexplorer4j.WordExploration.Vocabulary.EmbeddedWord;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dimensionalityreduction.PCA;

public class WordExplorer {
    
    private Vocabulary vocabulary;
    private NearestNeighbour nearestNeighbour;

    public WordExplorer(Vocabulary vocabulary) {
        if (Objects.isNull(vocabulary)) {
            throw new IllegalArgumentException("Vocabulary can not be null");
        }

        this.vocabulary = vocabulary;
    }

    public void calculateWordsPca(boolean normalize) {
        INDArray pca_matrix = getPCAMatrix();
        
        NormalizerStandardize normalizer = new NormalizerStandardize();
        if (normalize) {
            INDArray matrix = this.getWordsEmbeddingMatrix();

            DataSet dataSet = new DataSet(matrix, Nd4j.ones(vocabulary.size(), vocabulary.getEmbeddingDimension()));
            normalizer.fit(dataSet);
        }

        for (EmbeddedWord w : vocabulary) {
            w.setPca(w.getEmbedding().mmul(pca_matrix));
            if (normalize) {
                w.normalizeEmbedding(normalizer);
            }
        }
    }

    private INDArray getPCAMatrix() {
        INDArray matrix = this.getWordsEmbeddingMatrix();

        INDArray factors = PCA.pca_factor(matrix, 2, true);
        return factors;
    }

    private INDArray getWordsEmbeddingMatrix() {
        List<INDArray> ndArrays = vocabulary.stream()
                                            .map(EmbeddedWord::getEmbedding)
                                            .collect(Collectors.toList());

        int rows = vocabulary.size();
        int columns = vocabulary.getEmbeddingDimension();

        INDArray matrix = Nd4j.create(ndArrays, new int[] {rows, columns});
        return matrix;
    }

    public Map<WordList, List<String>> getNeighbours(List<WordList> wordLists, int quantity) {
        if (Objects.isNull(wordLists) || wordLists.size() == 0) {
            throw new IllegalArgumentException("Must provide at least one word list to plot");
        }

        Map<WordList, List<String>> map = new HashMap<>(wordLists.size());
        for (WordList wl : wordLists) {
            Map<String, List<String>> outputMap = this.getNeighboursFromList(wl.getWordList(), quantity);
            List<String> neighbour_tokens = outputMap.values().stream()
                                                              .flatMap(List::stream)
                                                              .collect(Collectors.toList());
            map.put(wl, neighbour_tokens);
        }

        return map;
    }

    private Map<String, List<String>> getNeighboursFromList(List<String> words, int quantity) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        }

        if (Objects.isNull(this.nearestNeighbour)) 
            this.nearestNeighbour = new NearestNeighbour(this.getNeighbourMapping());
        
        List<String> wordsInVocab = vocabulary.get(words).stream()
                                                         .map(EmbeddedWord::getWord)
                                                         .collect(Collectors.toList());

        return this.nearestNeighbour.getKNearestNeighbour(wordsInVocab, quantity);
    }

    private Map<String, INDArray> getNeighbourMapping() {
        return vocabulary.stream()
                         .collect(Collectors.toMap(EmbeddedWord::getWord, EmbeddedWord::getEmbedding));
    }

    public void plot(List<WordList> wordLists) {
        if (Objects.isNull(wordLists) || wordLists.size() == 0) {
            throw new IllegalArgumentException("Must provide at least one non-null word list to plot");
        }

        String title = "Word Embedding in 2D";
        String xAxisLabel = "";
        String yAxisLabel = "";

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("title", title);
        arguments.put("xAxisLabel", xAxisLabel);
        arguments.put("yAxisLabel", yAxisLabel);
        arguments.put("labelPoints", true);

        PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments, wordLists);
    }

    public void plot(List<WordList> wordLists, int quantity) {
        if (Objects.isNull(wordLists) || wordLists.size() == 0) {
            throw new IllegalArgumentException("Must provide at least one word list to plot");
        }

        Map<WordList, List<String>> neighboursMap = this.getNeighbours(wordLists, quantity);
        for (WordList wl : wordLists) {
            List<String> neighbouList = neighboursMap.get(wl);
            vocabulary.add(wl, neighbouList);
        }

        this.plot(wordLists);
    }

    public Vocabulary getVocabulary() {
        return this.vocabulary;
    }
}

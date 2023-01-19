package com.biasexplorer4j.WordExploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.nd4j.linalg.factory.Nd4j;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.NearestNeighbour.NearestNeighbour;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PLOT_TYPE;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PlotManager;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dimensionalityreduction.PCA;

public class WordExplorer {
    
    private Vocabulary vocabulary;
    private NearestNeighbour nearestNeighbour;

    public WordExplorer(Map<String, double[]> embeddings) {
        if (Objects.isNull(embeddings)) {
            throw new IllegalArgumentException("Embeddings map can not be null");
        }

        this.vocabulary = new Vocabulary(embeddings);
    }

    public WordExplorer(DataLoader data) {
        if (Objects.isNull(data)) {
            throw new IllegalArgumentException("Data loader can not be null");
        } else if (data.getEmbeddings().size() == 0) {
            throw new IllegalArgumentException("Data loader has no embedding mapping. May result from omitting a call to loadDataset()");
        }

        this.vocabulary = new Vocabulary(data);
    }

    public void calculateWordsPca(boolean normalize) {
        INDArray pca_matrix = getPCAMatrix();
        
        NormalizerStandardize normalizer = new NormalizerStandardize();
        if (normalize) {
            INDArray matrix = this.getWordsEmbeddingMatrix();

            DataSet dataSet = new DataSet(matrix, Nd4j.ones(vocabulary.size(), vocabulary.getEmbeddingDimension()));
            normalizer.fit(dataSet);
        }

        for (Word w : vocabulary) {
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
                                            .map(Word::getEmbedding)
                                            .collect(Collectors.toList());

        int rows = vocabulary.size();
        int columns = vocabulary.getEmbeddingDimension();

        INDArray matrix = Nd4j.create(ndArrays, new int[] {rows, columns});
        return matrix;
    }

    public Map<String, List<String>> getNeighbours(List<String> words, int quantity) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        }

        if (Objects.isNull(this.nearestNeighbour)) 
            this.nearestNeighbour = new NearestNeighbour(this.getNeighbourMapping());
        
        List<String> wordsInVocab = vocabulary.get(words).stream()
                                                         .map(Word::getWord)
                                                         .collect(Collectors.toList());

        return this.nearestNeighbour.getKNearestNeighbour(wordsInVocab, quantity);
    }

    private Map<String, INDArray> getNeighbourMapping() {
        return vocabulary.stream()
                         .collect(Collectors.toMap(Word::getWord, Word::getEmbedding));
    }

    public void plot(List<String> words) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        }
        List<Word> wordsInVocab = vocabulary.get(words);
        String[] strInVocab = wordsInVocab.stream().map(Word::getWord).toArray(String[]::new);
        double[][] projections = new double[2][wordsInVocab.size()];
        for (int i = 0; i < wordsInVocab.size(); ++i) {
            projections[0][i] = wordsInVocab.get(i).getPca(0);
            projections[1][i] = wordsInVocab.get(i).getPca(1);
        }
        String title = "Word Embedding in 2D";
        String xAxisLabel = "";
        String yAxisLabel = "";

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("words", strInVocab);
        arguments.put("projections", projections);
        arguments.put("title", title);
        arguments.put("xAxisLabel", xAxisLabel);
        arguments.put("yAxisLabel", yAxisLabel);
        arguments.put("labelPoints", true);

        PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments);
    }

    public void plot(List<String> words, int numberOfNeighbours) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        } else if (numberOfNeighbours < 0) {
            throw new IllegalArgumentException("Number of neighbours to retrive from words, should be greater or equal than zero");
        }

        List<Word> wordsInVocab = vocabulary.get(words);

        List<String> neighbourWords = new ArrayList<>(wordsInVocab.size() * numberOfNeighbours);
        Map<String, List<String>> map = getNeighbours(wordsInVocab.stream()
                                                                  .map(Word::getWord)
                                                                  .collect(Collectors.toList()), 
                                                      numberOfNeighbours);
        for (List<String> listOfNeighbours : map.values()) {
            for (String n : listOfNeighbours) {
                neighbourWords.add(n);
            }
        }

        neighbourWords.addAll(words);
        plot(neighbourWords);
    }

    public Vocabulary getVocabulary() {
        return this.vocabulary;
    }
}

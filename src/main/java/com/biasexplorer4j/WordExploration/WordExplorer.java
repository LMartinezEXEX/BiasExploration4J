package com.biasexplorer4j.WordExploration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.nd4j.linalg.factory.Nd4j;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.NearestNeighbour.NearestNeighbour;
import com.biasexplorer4j.WordExploration.Visualization.WordExplorerVisualizer;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dimensionalityreduction.PCA;

import javafx.application.Platform;
import javafx.stage.Stage;

public class WordExplorer {

    private Map<String, Word> wordsMap = new HashMap<>();
    private int embeddingDimension;
    private NearestNeighbour nearestNeighbour;

    public WordExplorer(Map<String, double[]> embeddings) {
        if (Objects.isNull(embeddings)) {
            throw new IllegalArgumentException("Embeddings map can not be null");
        }

        for (double[] d : embeddings.values()) {
            this.embeddingDimension = d.length;
            break;
        }

        for(Map.Entry<String, double[]> entry : embeddings.entrySet()) {
            INDArray ndEmbedding = Nd4j.create(entry.getValue(), new int[] {1, this.getEmbeddingDimension()});
            wordsMap.put(entry.getKey(), new Word(entry.getKey(), ndEmbedding));
        }

        wordsMap = Collections.unmodifiableMap(wordsMap);
    }

    public WordExplorer(DataLoader data) {
        if (Objects.isNull(data)) {
            throw new IllegalArgumentException("Data loader can not be null");
        } else if (data.getEmbeddings().size() == 0) {
            throw new IllegalArgumentException("Data loader has no embedding mapping. May result from omitting a call to loadDataset()");
        }

        this.embeddingDimension = data.getEmbeddingDimension();

        for(Map.Entry<String, double[]> entry : data.getEmbeddings().entrySet()) {
            INDArray ndEmbedding = Nd4j.create(entry.getValue(), new int[] {1, this.getEmbeddingDimension()});
            wordsMap.put(entry.getKey(), new Word(entry.getKey(), ndEmbedding));
        }

        wordsMap = Collections.unmodifiableMap(wordsMap);
    }

    public void calculateWordsPca(boolean normalize) {
        INDArray pca_matrix = getPCAMatrix();
        
        NormalizerStandardize normalizer = new NormalizerStandardize();
        if (normalize) {
            INDArray matrix = this.getWordsEmbeddingMatrix();

            DataSet dataSet = new DataSet(matrix, Nd4j.ones(this.getWordsMap().size(), this.getEmbeddingDimension()));
            normalizer.fit(dataSet);
        }

        for (Word w : this.getWordsMap().values()) {
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
        List<INDArray> ndArrays = this.getWordsMap().values().stream()
                                                            .map(Word::getEmbedding)
                                                            .collect(Collectors.toList());

        int rows = this.getWordsMap().size();
        int columns = this.getEmbeddingDimension();

        INDArray matrix = Nd4j.create(ndArrays, new int[] {rows, columns});
        return matrix;
    }

    public Map<String, List<String>> getNeighbours(List<String> words, int quantity) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        }

        if (Objects.isNull(this.nearestNeighbour)) 
            this.nearestNeighbour = new NearestNeighbour(this.getNeighbourMapping());
        
        List<String> wordsInVocab = getWordsInVocab(words).stream()
                                                        .map(Word::getWord)
                                                        .collect(Collectors.toList());

        return this.nearestNeighbour.getKNearestNeighbour(wordsInVocab, quantity);
    }

    private Map<String, INDArray> getNeighbourMapping() {
        return this.getWordsMap()
                    .values()
                    .stream()
                    .collect(Collectors.toMap(Word::getWord, Word::getEmbedding));
    }

    public void plot(List<String> words) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        }

        Platform.runLater(() -> {
            try {
                new WordExplorerVisualizer(getWordsInVocab(words)).plot(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void plot(List<String> words, int numberOfNeighbours) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list can not be null");
        } else if (numberOfNeighbours < 0) {
            throw new IllegalArgumentException("Number of neighbours to retrive from words, should be greater or equal than zero");
        }

        List<Word> wordsInVocab = getWordsInVocab(words);

        List<String> neighbourWords = new ArrayList<>(wordsInVocab.size() * numberOfNeighbours);
        Map<String, List<String>> map = getNeighbours(wordsInVocab.stream().map(Word::getWord).collect(Collectors.toList()), numberOfNeighbours);
        for (List<String> listOfNeighbours : map.values()) {
            for (String n : listOfNeighbours) {
                neighbourWords.add(n);
            }
        }

        neighbourWords.addAll(words);
        plot(neighbourWords);
    }

    private List<Word> getWordsInVocab(List<String> words) {
        List<Word> wordsInVocab = new ArrayList<>(words.size());
        for (String w : words) {
            if (this.getWordsMap().containsKey(w)) {
                wordsInVocab.add(this.getWordsMap().get(w));
            } else {
                System.out.println("[WARN]  WordExplorer : Word { " + w + " } is not in vocabulary.");
            }
        }
        return wordsInVocab;
    }

    public Map<String, Word> getWordsMap() {
        return this.wordsMap;
    }

    public int getEmbeddingDimension() {
        return this.embeddingDimension;
    }
}

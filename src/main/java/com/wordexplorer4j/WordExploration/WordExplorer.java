package com.wordexplorer4j.WordExploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dimensionalityreduction.PCA;

import com.wordexplorer4j.DataLoader.DataLoader;
import com.wordexplorer4j.NearestNeighbour.NearestNeighbour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class WordExplorer {

    private ArrayList<Word> words = new ArrayList<>();
    private int embeddingDimension;
    private NearestNeighbour nearestNeighbour = null;

    public WordExplorer(HashMap<String, double[]> embeddings) {
        for (double[] d : embeddings.values()) {
            this.embeddingDimension = d.length;
            break;
        }

        for(Map.Entry<String, double[]> entry : embeddings.entrySet()) {
            INDArray ndEmbedding = Nd4j.create(entry.getValue(), new int[] {1, this.getEmbeddingDimension()});
            words.add(new Word(entry.getKey(), ndEmbedding));
        }

        initPlotter();
    }

    public WordExplorer(DataLoader data) {
        this.embeddingDimension = data.getEmbeddingDimension();

        for(Map.Entry<String, double[]> entry : data.getEmbeddings().entrySet()) {
            INDArray ndEmbedding = Nd4j.create(entry.getValue(), new int[] {1, this.getEmbeddingDimension()});
            words.add(new Word(entry.getKey(), ndEmbedding));
        }

        initPlotter();
    }

    public void calculateWordsPca(boolean normalize) {
        INDArray pca_matrix = getPCAMatrix();
        
        NormalizerStandardize normalizer = new NormalizerStandardize();
        if (normalize) {
            INDArray matrix = this.getWordsEmbeddingMatrix();

            DataSet dataSet = new DataSet(matrix, Nd4j.ones(words.size(), this.getEmbeddingDimension()));
            normalizer.fit(dataSet);
        }

        for (Word w : this.getWords()) {
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

    public INDArray getWordsEmbeddingMatrix() {
        List<INDArray> ndArrays = new ArrayList<>();
        for (Word w : this.getWords()) {
            ndArrays.add(w.getEmbedding());
        }

        int rows = words.size();
        int columns = this.getEmbeddingDimension();

        INDArray matrix = Nd4j.create(ndArrays, new int[] {rows, columns});
        return matrix;
    }

    public Map<String, List<String>> getNeighbours(List<String> words, int quantity) {
        if (Objects.isNull(this.nearestNeighbour)) 
            this.nearestNeighbour = new NearestNeighbour(this.getNeighbourMapping());
        
        return this.nearestNeighbour.getKNearestNeighbour(words, quantity);
    }

    private Map<String, INDArray> getNeighbourMapping() {
        Map<String, INDArray> map = new HashMap<>(); 
        for(Word w : this.words)
            map.put(w.getWord(), w.getEmbedding());
        return map;
    }

    public void plot(List<String> words) {
        Platform.runLater(() -> {
            try {
                new Visualizer(getAvailableWordsToPlot(words)).plot(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void plot(List<String> words, int numberOfNeighbours) {
        List<Word> wordsInVocab = getAvailableWordsToPlot(words);

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

    private List<Word> getAvailableWordsToPlot(List<String> words) {
        return this.words.stream()
                    .filter(w -> words.contains(w.getWord()))
                    .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    private void initPlotter() {
        Platform.setImplicitExit(false);
        final JFXPanel fxPanel = new JFXPanel();
        new Thread(() -> Application.launch(Visualizer.class)).start();
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public int getEmbeddingDimension() {
        return embeddingDimension;
    }
}

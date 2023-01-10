package com.wordexplorer4j.WordExploration.BiasExploration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.wordexplorer4j.WordExploration.Word;
import com.wordexplorer4j.WordExploration.WordExplorer;
import com.wordexplorer4j.WordExploration.Visualization.BiasVisualizer;

import javafx.application.Platform;
import javafx.stage.Stage;

public class BiasExplorer {
    private Map<String, Word> words = new HashMap<>();
    private int embeddingDimension;

    public BiasExplorer(WordExplorer wordExplorer) {
        for (Word w : wordExplorer.getWords()) {
            words.put(w.getWord(), w);
        }
        this.embeddingDimension = wordExplorer.getEmbeddingDimension();
    }

    public double[] plot2SpaceBias(List<String> words, List<String> kernel_1, List<String> kernel_2) {
        List<Word> wordsInVocab = getWordsInVocab(words);
        List<Word> kernel_1_InVocab = getWordsInVocab(kernel_1);
        List<Word> kernel_2_InVocab = getWordsInVocab(kernel_2);

        INDArray direction = getDirection(kernel_1_InVocab, kernel_2_InVocab);
        double[] projections = getProjections(wordsInVocab, direction);

        List<ProjectedWord> projectedWords = new ArrayList<>(wordsInVocab.size());
        for (int i = 0; i < wordsInVocab.size(); ++i) {
            projectedWords.add(new ProjectedWord(wordsInVocab.get(i).getWord(), projections[i]));
        }

        Collections.sort(projectedWords);
        plot(projectedWords, 
            kernel_1_InVocab.stream()
                            .map(Word::getWord)
                            .collect(Collectors.toList()),
            kernel_2_InVocab.stream()
                            .map(Word::getWord)
                            .collect(Collectors.toList())
            );

        return projections;
    }

    public double[][] plot4SpaceBias(List<String> words, List<String> kernel_1, List<String> kernel_2, List<String> kernel_3, List<String> kernel_4) {
        double[] projections_x = plot2SpaceBias(words, kernel_1, kernel_2);
        double[] projections_y = plot2SpaceBias(words, kernel_3, kernel_4);

        return new double[][] { projections_x, projections_y };
    }

    // All words must be present in the words map!
    private INDArray getDirection(List<Word> list_1, List<Word> list_2) {
        INDArray group_1 = getNormedSumEmbeddOf(list_1);
        INDArray group_2 = getNormedSumEmbeddOf(list_2);

        INDArray diff = group_1.sub(group_2);

        return Transforms.unitVec(diff).castTo(DataType.DOUBLE);
    }

    private INDArray getNormedSumEmbeddOf(List<Word> list) {
        INDArray group_1 = Nd4j.zeros(1, embeddingDimension);
        for (Word word : list) {
            group_1.addi(word.getEmbedding());
        }

        return Transforms.unitVec(group_1);
    }

    private double[] getProjections(List<Word> words, INDArray direction) {
        double[] projections = new double[words.size()];
        for (int i = 0; i < projections.length; ++i) {
            projections[i] = Transforms.cosineSim(words.get(i).getEmbedding(), direction);
        }

        return projections;
    }

    private void plot(List<ProjectedWord> projectedWords, List<String> kernelLeft, List<String> kernelRight) {
        Platform.runLater(() -> {
            try {
                new BiasVisualizer(projectedWords, kernelLeft, kernelRight).plot(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<Word> getWordsInVocab(List<String> words) {
        List<Word> wordsInVocab = new ArrayList<>(words.size());
        for (String word : words) {
            if (this.words.containsKey(word)) {
                wordsInVocab.add(this.words.get(word));
            }
        }

        return wordsInVocab;
    } 
}

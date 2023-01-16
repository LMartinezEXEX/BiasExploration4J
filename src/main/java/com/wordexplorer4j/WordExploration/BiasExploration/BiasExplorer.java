package com.wordexplorer4j.WordExploration.BiasExploration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.wordexplorer4j.WordExploration.Word;
import com.wordexplorer4j.WordExploration.WordExplorer;
import com.wordexplorer4j.WordExploration.Visualization.BiasVisualizer_2Spaces;
import com.wordexplorer4j.WordExploration.Visualization.BiasVisualizer_4Spaces;

import javafx.application.Platform;
import javafx.stage.Stage;

public class BiasExplorer {
    private Map<String, Word> words;
    private int embeddingDimension;

    public BiasExplorer(WordExplorer wordExplorer) {
        if (Objects.isNull(wordExplorer)) {
            throw new IllegalArgumentException("Word explorer object can not be null");
        }

        this.words = wordExplorer.getWordsMap();
        this.embeddingDimension = wordExplorer.getEmbeddingDimension();
    }

    public double[] plot2SpaceBias(List<String> words, List<String> kernel_1, List<String> kernel_2) {
        if (Objects.isNull(words) || Objects.isNull(kernel_1) || Objects.isNull(kernel_2)) {
            throw new IllegalArgumentException("No list of words can be null");
        }

        List<Word> wordsInVocab = getWordsInVocab(words);
        double[] projections = calculateBias(wordsInVocab, kernel_1, kernel_2);

        List<ProjectedWord> projectedWords = new ArrayList<>(wordsInVocab.size());
        for (int i = 0; i < wordsInVocab.size(); ++i) {
            projectedWords.add(new ProjectedWord(wordsInVocab.get(i).getWord(), projections[i]));
        }
        Collections.sort(projectedWords);

        plot(projectedWords, 
            getStringsInVocab(kernel_1),
            getStringsInVocab(kernel_2)
            );

        return projections;
    }

    public double[][] plot4SpaceBias(List<String> words, List<String> kernel_1, List<String> kernel_2, List<String> kernel_3, List<String> kernel_4) {
        if (Objects.isNull(words) || Objects.isNull(kernel_1) || Objects.isNull(kernel_2) ||
            Objects.isNull(kernel_3) || Objects.isNull(kernel_4)) {
            throw new IllegalArgumentException("No list of words can be null");
        }

        List<Word> wordsInVocab = getWordsInVocab(words);
        double[] projections_x = calculateBias(wordsInVocab, kernel_1, kernel_2);
        double[] projections_y = calculateBias(wordsInVocab, kernel_3, kernel_4);

        double[][] projections = new double[][] { projections_x, projections_y };

        List<String> kernel_1_InVocab = getStringsInVocab(kernel_1);
        List<String> kernel_2_InVocab = getStringsInVocab(kernel_2);
        List<String> kernel_3_InVocab = getStringsInVocab(kernel_3);
        List<String> kernel_4_InVocab = getStringsInVocab(kernel_4);

        words = wordsInVocab.stream().map(Word::getWord).collect(Collectors.toList());
        plot(words, 
            projections, 
            kernel_1_InVocab, 
            kernel_2_InVocab, 
            kernel_3_InVocab, 
            kernel_4_InVocab
            );

        return projections;
    }

    private double[] calculateBias(List<Word> words, List<String> kernel_1, List<String> kernel_2) {
        List<Word> kernel_1_InVocab = getWordsInVocab(kernel_1);
        List<Word> kernel_2_InVocab = getWordsInVocab(kernel_2);

        if (kernel_1_InVocab.isEmpty()) {
            throw new IllegalArgumentException("Definition of kernel 1 is empty (After removing O.O.V. words)");
        } else if (kernel_2_InVocab.isEmpty()) {
            throw new IllegalArgumentException("Definition of kernel 2 is empty (After removing O.O.V. words)");
        }

        if (kernel_1_InVocab.size() == kernel_2_InVocab.size() && kernel_1_InVocab.containsAll(kernel_2_InVocab)) {
            String sameWords = String.join(", ", kernel_1_InVocab.stream().map(Word::getWord).collect(Collectors.toList()));
            throw new IllegalArgumentException("Kernels can not be defined by the same words: " + sameWords);
        }

        INDArray direction = getDirection(kernel_1_InVocab, kernel_2_InVocab);
        return getProjections(words, direction);
    }

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
                new BiasVisualizer_2Spaces(projectedWords, kernelLeft, kernelRight).plot(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void plot(List<String> words, double[][] projections, List<String> kernelLeft, List<String> kernelRight, List<String> kernelUp, List<String> kernelDown) {
        Platform.runLater(() -> {
            try {
                new BiasVisualizer_4Spaces(words, projections, kernelLeft, kernelRight, kernelUp, kernelDown).plot(new Stage());
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
            } else {
                System.out.println("[WARN]  BiasExplorer : Word { " + word + " } is not in vocabulary.");
            }
        }

        return wordsInVocab;
    }

    private List<String> getStringsInVocab(List<String> words) {
        List<String> wordsInVocab = new ArrayList<>(words.size());
        for (String word : words) {
            if (this.words.containsKey(word)) {
                wordsInVocab.add(word);
            } else {
                System.out.println("[WARN]  BiasExplorer : Word { " + word + " } is not in vocabulary.");
            }
        }

        return wordsInVocab;
    }
}

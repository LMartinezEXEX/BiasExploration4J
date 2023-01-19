package com.biasexplorer4j.WordExploration.BiasExploration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.biasexplorer4j.WordExploration.Vocabulary;
import com.biasexplorer4j.WordExploration.Word;
import com.biasexplorer4j.WordExploration.WordExplorer;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PLOT_TYPE;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PlotManager;

public class BiasExplorer {

    private Vocabulary vocabulary;

    public BiasExplorer(WordExplorer wordExplorer) {
        if (Objects.isNull(wordExplorer)) {
            throw new IllegalArgumentException("Word explorer object can not be null");
        }

        this.vocabulary = wordExplorer.getVocabulary();
    }

    public double[] plot2SpaceBias(List<String> words, List<String> kernel_1, List<String> kernel_2) {
        if (Objects.isNull(words) || Objects.isNull(kernel_1) || Objects.isNull(kernel_2)) {
            throw new IllegalArgumentException("No list of words can be null");
        }

        List<Word> wordsInVocab = vocabulary.get(words);
        double[] projections = calculateBias(wordsInVocab, kernel_1, kernel_2);

        List<ProjectedWord> projectedWords = new ArrayList<>(wordsInVocab.size());
        for (int i = 0; i < wordsInVocab.size(); ++i) {
            projectedWords.add(new ProjectedWord(wordsInVocab.get(i).getWord(), projections[i]));
        }
        Collections.sort(projectedWords);

        plot(projectedWords, 
            vocabulary.filterInVocab(kernel_1),
            vocabulary.filterInVocab(kernel_2)
            );

        return projections;
    }

    public double[][] plot4SpaceBias(List<String> words, List<String> kernel_1, List<String> kernel_2, List<String> kernel_3, List<String> kernel_4) {
        if (Objects.isNull(words) || Objects.isNull(kernel_1) || Objects.isNull(kernel_2) ||
            Objects.isNull(kernel_3) || Objects.isNull(kernel_4)) {
            throw new IllegalArgumentException("No list of words can be null");
        }

        List<Word> wordsInVocab = vocabulary.get(words);
        double[] projections_x = calculateBias(wordsInVocab, kernel_1, kernel_2);
        double[] projections_y = calculateBias(wordsInVocab, kernel_3, kernel_4);

        double[][] projections = new double[][] { projections_x, projections_y };

        List<String> kernel_1_InVocab = vocabulary.filterInVocab(kernel_1);
        List<String> kernel_2_InVocab = vocabulary.filterInVocab(kernel_2);
        List<String> kernel_3_InVocab = vocabulary.filterInVocab(kernel_3);
        List<String> kernel_4_InVocab = vocabulary.filterInVocab(kernel_4);

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
        List<Word> kernel_1_InVocab = vocabulary.get(kernel_1);
        List<Word> kernel_2_InVocab = vocabulary.get(kernel_2);

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
        INDArray group_1 = Nd4j.zeros(1, vocabulary.getEmbeddingDimension());
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
        String[] words = projectedWords.stream().map(ProjectedWord::getWord).toArray(String[]::new);
        double[] projections = projectedWords.stream().mapToDouble(ProjectedWord::getProjection).toArray();
        String title = "2 Spaces word projection";
        String xAxisLabel = "";
        String yAxisLabel = "← " + String.join(", ", kernelLeft) + "      " + String.join(", ", kernelRight) + " →";

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("words", words);
        arguments.put("projections", projections);
        arguments.put("title", title);
        arguments.put("xAxisLabel", xAxisLabel);
        arguments.put("yAxisLabel", yAxisLabel);

        PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments);
    }

    private void plot(List<String> words, double[][] projections, List<String> kernelLeft, List<String> kernelRight, List<String> kernelUp, List<String> kernelDown) {
        String[] wordsArray = words.toArray(String[]::new);
        double[] xAxisLimits = new double[] {-1, 1};
        double[] yAxisLimits = new double[] {-1, 1};
        String title = "4 Spaces word projection";
        String xAxisLabel = "← " + String.join(", ", kernelRight) + "      " + String.join(", ", kernelLeft) + " →";
        String yAxisLabel = "← " + String.join(", ", kernelDown) + "      " + String.join(", ", kernelUp) + " →";
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("words", wordsArray);
        arguments.put("projections", projections);
        arguments.put("xAxisLimits", xAxisLimits);
        arguments.put("yAxisLimits", yAxisLimits);
        arguments.put("title", title);
        arguments.put("xAxisLabel", xAxisLabel);
        arguments.put("yAxisLabel", yAxisLabel);
        arguments.put("labelPoints", true);
        arguments.put("drawOriginAxis", true);

        PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments);
    }
}

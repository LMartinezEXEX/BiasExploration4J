package com.biasexplorer4j.WordExploration.BiasExploration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.biasexplorer4j.WordExploration.Visualization.Plots.PLOT_TYPE;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PlotManager;
import com.biasexplorer4j.WordExploration.Vocabulary.Vocabulary;
import com.biasexplorer4j.WordExploration.Vocabulary.Word;
import com.biasexplorer4j.WordExploration.Vocabulary.EmbeddedWord;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

public class BiasExplorer {

    private Vocabulary vocabulary;

    public BiasExplorer(Vocabulary vocabulary) {
        if (Objects.isNull(vocabulary)) {
            throw new IllegalArgumentException("Vocabulary can not be null");
        }
        
        this.vocabulary = vocabulary;
    }

    public double[] plot2SpaceBias(List<String> words, WordList kernel_1, WordList kernel_2) {
        if (Objects.isNull(words) || Objects.isNull(kernel_1) || Objects.isNull(kernel_2)) {
            throw new IllegalArgumentException("No list of words can be null");
        }

        List<EmbeddedWord> wordsInVocab = vocabulary.get(words);
        double[] projections = calculateBias(wordsInVocab, kernel_1, kernel_2);
        
        Word[] wordsForList = new Word[wordsInVocab.size()];
        for (int i = 0; i < wordsInVocab.size(); ++i) {
            wordsForList[i] = new Word(wordsInVocab.get(i).getWord(), new double[] { projections[i] });
        }

        WordList wordList = vocabulary.getWordList("Words of interest", wordsForList);
        plot(wordList, 
            kernel_2,
            kernel_1
            );

        return projections;
    }

    public double[][] plot4SpaceBias(List<String> words, WordList kernel_1, WordList kernel_2, WordList kernel_3, WordList kernel_4) {
        if (Objects.isNull(words) || Objects.isNull(kernel_1) || Objects.isNull(kernel_2) ||
            Objects.isNull(kernel_3) || Objects.isNull(kernel_4)) {
            throw new IllegalArgumentException("No list of words can be null");
        }

        List<EmbeddedWord> wordsInVocab = vocabulary.get(words);
        double[] projections_x = calculateBias(wordsInVocab, kernel_1, kernel_2);
        double[] projections_y = calculateBias(wordsInVocab, kernel_3, kernel_4);

        Word[] wordsForList = new Word[wordsInVocab.size()];
        for (int i = 0; i < wordsInVocab.size(); ++i) {
            String word = wordsInVocab.get(i).getWord();
            double[] projection = new double[] { projections_x[i], projections_y[i]};
            wordsForList[i] = new Word(word, projection);
        }

        WordList wordList = vocabulary.getWordList("Words of interest", wordsForList);
        plot(wordList,
            kernel_1, 
            kernel_2, 
            kernel_3, 
            kernel_4
            );

        return new double[][] { projections_x, projections_y };
    }

    private double[] calculateBias(List<EmbeddedWord> words, WordList kernel_1, WordList kernel_2) {
        if (kernel_1.getWords().equals(kernel_2.getWords())) {
            throw new IllegalArgumentException("Kernels can not be defined by the same words");
        }

        INDArray direction = getDirection(kernel_1.getWords(), kernel_2.getWords());
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
            EmbeddedWord embeddedWord = vocabulary.get(word.getWord());
            group_1.addi(embeddedWord.getEmbedding());
        }

        return Transforms.unitVec(group_1);
    }

    private double[] getProjections(List<EmbeddedWord> words, INDArray direction) {
        double[] projections = new double[words.size()];
        for (int i = 0; i < projections.length; ++i) {
            projections[i] = Transforms.cosineSim(words.get(i).getEmbedding(), direction);
        }

        return projections;
    }

    private void plot(WordList projectedWords, WordList kernelLeft, WordList kernelRight) {
        String title = "2 Spaces word projection";
        String xAxisLabel = "";
        String yAxisLabel = "??? " + String.join(", ", kernelLeft.getWordList()) + "      " + String.join(", ", kernelRight.getWordList()) + " ???";

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("title", title);
        arguments.put("xAxisLabel", xAxisLabel);
        arguments.put("yAxisLabel", yAxisLabel);

        PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments, Arrays.asList(projectedWords));
    }

    private void plot(WordList projectedWords, WordList kernelLeft, WordList kernelRight, WordList kernelUp, WordList kernelDown) {
        double[] xAxisLimits = new double[] {-1, 1};
        double[] yAxisLimits = new double[] {-1, 1};
        String title = "4 Spaces word projection";
        String xAxisLabel = setAxisLabel(kernelRight, kernelLeft);
        String yAxisLabel = setAxisLabel(kernelDown, kernelUp);
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("xAxisLimits", xAxisLimits);
        arguments.put("yAxisLimits", yAxisLimits);
        arguments.put("title", title);
        arguments.put("xAxisLabel", xAxisLabel);
        arguments.put("yAxisLabel", yAxisLabel);
        arguments.put("labelPoints", true);
        arguments.put("drawOriginAxis", true);

        PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments, Arrays.asList(projectedWords));
    }

    private  String setAxisLabel(WordList kernel_1, WordList kernel_2) {
        String kernel_1_title = kernel_1.getTitle().strip();
        String kernel_2_title = kernel_2.getTitle().strip();

        String raw_words_kernel_1 = String.join(", ", kernel_1.getWordList());
        String raw_words_kernel_2 = String.join(", ", kernel_2.getWordList());

        String axisLabel;
        if (raw_words_kernel_1.length() + raw_words_kernel_2.length() >= 20) {
            axisLabel = "??? " + kernel_1_title + "      " + kernel_2_title + " ???";
        }
        else {
            axisLabel = "??? " + raw_words_kernel_1 + "      " + raw_words_kernel_2 + " ???";
        }

        return axisLabel;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }
}

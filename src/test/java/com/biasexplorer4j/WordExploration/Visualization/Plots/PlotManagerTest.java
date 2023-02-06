package com.biasexplorer4j.WordExploration.Visualization.Plots;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.WordExploration.BiasExploration.BiasExplorer;
import com.biasexplorer4j.WordExploration.Vocabulary.Vocabulary;
import com.biasexplorer4j.WordExploration.Vocabulary.Word;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

public class PlotManagerTest {

    @AfterEach
    public void clean() {
        PlotManager.getInstance().cleanUp();
    }
    
    @Test
    public void successfulInstantiation() {
        assertDoesNotThrow(() -> PlotManager.getInstance());
    }

    @Test
    public void generateBarPlotSuccessfully() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        BiasExplorer be = new BiasExplorer(vocabulary);

        Word[] words = new Word[] { new Word("hombre", new double[] {0.45}), 
                                    new Word("mujer", new double[] {-0.45})
                                  };

        WordList wordList = be.getVocabulary().getWordList("Test", words);

        Map<String, Object> arguments = new HashMap<>(0);

        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments, Arrays.asList(wordList)));
    }

    @Test
    public void failBarPlotWhenNullReferencedWordList() {
        List<WordList> wordLists = null;
        Map<String, Object> arguments = new HashMap<>(0);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments, wordLists),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Must provide one word list to plot"));
    }

    @Test
    public void failBarPlotWhenListOfWordListIsEmpty() {
        List<WordList> wordLists = Arrays.asList();
        Map<String, Object> arguments = new HashMap<>(0);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments, wordLists),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Must provide one word list to plot"));
    }

    @Test
    public void failScatterPlotWhenNullReferencedWordList() {
        List<WordList> wordLists = null;

        Map<String, Object> arguments = new HashMap<>(0);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments, wordLists),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Must provide at least one word list to plot"));
    }

    @Test
    public void failScatterPlotWhenWordListIsEmpty() {
        List<WordList> wordLists = Arrays.asList();

        Map<String, Object> arguments = new HashMap<>(0);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments, wordLists),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Must provide at least one word list to plot"));
    }

    @Test
    public void cleaningDoesNotThrow() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        BiasExplorer be = new BiasExplorer(vocabulary);

        Word[] scatterWords = new Word[] { new Word("hombre", new double[] {0.45, 0.86}), 
                                           new Word("mujer", new double[] {-0.45, 0.12})
                                         };

        WordList scatterWordList = be.getVocabulary().getWordList("Test", scatterWords);

        Map<String, Object> scatterArguments = new HashMap<>(0);

        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, scatterArguments, Arrays.asList(scatterWordList)));
        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, scatterArguments, Arrays.asList(scatterWordList)));
        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, scatterArguments, Arrays.asList(scatterWordList)));

        Word[] barWords = new Word[] { new Word("hombre", new double[] {0.45}), 
                                       new Word("mujer", new double[] {-0.45})
                                     };

        WordList barWordList = be.getVocabulary().getWordList("Test", barWords);

        Map<String, Object> barArguments = new HashMap<>(0);

        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, barArguments, Arrays.asList(barWordList)));
        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, barArguments, Arrays.asList(barWordList)));

        assertDoesNotThrow(() -> PlotManager.getInstance().cleanUp());
    }
}

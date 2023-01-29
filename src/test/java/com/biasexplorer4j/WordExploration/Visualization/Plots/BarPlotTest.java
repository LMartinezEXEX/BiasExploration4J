package com.biasexplorer4j.WordExploration.Visualization.Plots;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.WordExploration.WordExplorer;
import com.biasexplorer4j.WordExploration.BiasExploration.BiasExplorer;
import com.biasexplorer4j.WordExploration.BiasExploration.ProjectedWord;
import com.biasexplorer4j.WordExploration.Vocabulary.Vocabulary;
import com.biasexplorer4j.WordExploration.Vocabulary.Word;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

public class BarPlotTest {
    
    @Test
    public void successfulInstantiationWithProjectedWords() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        BiasExplorer be = new BiasExplorer(vocabulary);

        ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", new double[] {0.45}), 
                                                      new ProjectedWord("mujer", new double[] {-0.45})
                                                    };

        WordList<ProjectedWord> wordList = be.getVocabulary().getWordList("Test", words);
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
        assertDoesNotThrow(() -> new BarPlot<ProjectedWord>(wordList, title, xAxisLabel, yAxisLabel));
    }

    @Test
    public void successfulInstantiationWithWords() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);

        WordList<Word> wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");

        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
        assertDoesNotThrow(() -> new BarPlot<Word>(wordList, title, xAxisLabel, yAxisLabel));
    }

    @Test
    public void instantiateWithWordsWithoutProjectionsCalculated() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);

        WordList<Word> wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");

        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
        assertThrows(NullPointerException.class, 
                        () -> new BarPlot<Word>(wordList, title, xAxisLabel, yAxisLabel),
                        "Expected NullPointerException but not thrown");
    }

    @Test
    public void instantiateWithNullReferencedWordList() {
        WordList<Word> wordList = null;
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BarPlot<Word>(wordList, title, xAxisLabel, yAxisLabel),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Word list to plot can not be null"));
    }

    @Test
    public void instantiateWithNullReferencedProjectionList() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        BiasExplorer be = new BiasExplorer(vocabulary);

        ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", null), 
                                                      new ProjectedWord("mujer", null)
                                                    };

        WordList<ProjectedWord> wordList = be.getVocabulary().getWordList("Test", words);

        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BarPlot<ProjectedWord>(wordList, title, xAxisLabel, yAxisLabel),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Projection for word: { hombre } is null"));
    }

    @Test
    public void instantiateEmptyProjectionListInWordList() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        BiasExplorer be = new BiasExplorer(vocabulary);

        ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", new double[] {})
                                                    };

        WordList<ProjectedWord> wordList = be.getVocabulary().getWordList("Test", words);

        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BarPlot<ProjectedWord>(wordList, title, xAxisLabel, yAxisLabel),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("For bar plot all words must have at least 1 projection value. Word { hombre } has: 0"));
    }

    @Test
    public void instantiateWithNullReferencedTitleAndAxisLabels() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        BiasExplorer be = new BiasExplorer(vocabulary);

        ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", new double[] {0.45}), 
                                                      new ProjectedWord("mujer", new double[] {-0.45})
                                                    };

        WordList<ProjectedWord> wordList = be.getVocabulary().getWordList("Test", words);
        String title = null;
        String xAxisLabel = null;
        String yAxisLabel = null;

        assertDoesNotThrow(() -> new BarPlot<ProjectedWord>(wordList, title, xAxisLabel, yAxisLabel));
    }

    @Test
    public void plotShouldBeNotVisibleAfterInstantiation() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        BiasExplorer be = new BiasExplorer(vocabulary);

        ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", new double[] {0.45}), 
                                                      new ProjectedWord("mujer", new double[] {-0.45})
                                                    };

        WordList<ProjectedWord> wordList = be.getVocabulary().getWordList("Test", words);
        String title = null;
        String xAxisLabel = null;
        String yAxisLabel = null;
        BarPlot<ProjectedWord> plot = new BarPlot<>(wordList, title, xAxisLabel, yAxisLabel);
        
        assertFalse(plot.isVisible());
    }
}

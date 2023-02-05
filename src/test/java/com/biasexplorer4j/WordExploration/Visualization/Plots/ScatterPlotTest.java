package com.biasexplorer4j.WordExploration.Visualization.Plots;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.axis.ValueAxis;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.WordExploration.WordExplorer;
import com.biasexplorer4j.WordExploration.Vocabulary.Vocabulary;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

public class ScatterPlotTest {

    @AfterEach
    public void clean() {
        PlotManager.getInstance().cleanUp();
    }

    @Test
    public void successfulInstantiation() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);

        WordList wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");

        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        assertDoesNotThrow(() -> new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false));
    }

    @Test
    public void instantiateWithMultipleWordLists() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);

        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);

        WordList wordList_1 = we.getVocabulary().getWordList("Test", "hombre", "mujer");

        WordList wordList_2 = we.getVocabulary().getWordList("Test 2", "rey", "reina");

        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
                                           
        assertDoesNotThrow(() -> new ScatterPlot(Arrays.asList(wordList_1, wordList_2), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false));
    }
    
    @Test
    public void instantiateWithNullReferencedWordList() {
        List<WordList> wordLists = null;
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(wordLists, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("List of WordLists to plot can not be null"));
    }

    @Test
    public void instantiateWithNullReferencedWordListInList() {
        WordList wordList = null;
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("WordList can not be null"));
    }

    @Test
    public void instantiateWithNullReferencedProjectionWordWordList() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);

        WordList wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");

        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        assertThrows(NullPointerException.class, 
                        () -> new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                        "Expected NullPointerException but not thrown");
    }

    @Test
    public void instantiateWithNullReferencedTitleAndAxisLabels() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);

        WordList wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = null;
        String xAxisLabel = null;
        String yAxisLabel = null;

        assertDoesNotThrow(() -> new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false));
    }

    @Test
    public void instantiationWithAxisWhereUpperBoundIsSmallerThanLowerBound() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);

        WordList wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");
        double[] xAxisLimits = new double[] {2.0, 4.0};

        double[] yAxisLimits = new double[] {2.0, -2.0};

        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Range(double, double): require lower (" +
                                                yAxisLimits[0] +
                                                ") <= upper (" +
                                                yAxisLimits[1] +
                                                ")."));
    }

    @Test
    public void useAxisWhenGiven() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);
        WordList wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");

        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};

        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        ScatterPlot plot = new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false);
        ValueAxis xAxis = plot.getChart().getXYPlot().getDomainAxis();
        ValueAxis yAxis = plot.getChart().getXYPlot().getRangeAxis();

        assertEquals(xAxisLimits[0], xAxis.getLowerBound());
        assertEquals(xAxisLimits[1], xAxis.getUpperBound());
        assertEquals(yAxisLimits[0], yAxis.getLowerBound());
        assertEquals(yAxisLimits[1], yAxis.getUpperBound());
    }

    @Test
    public void labelXYPointsWhenNeeded() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);
        WordList wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        boolean labelXYPoints = true;

        assertDoesNotThrow(() -> new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, labelXYPoints, false));
    }

    @Test
    public void drawOriginAxisWhenNeeded() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);
        WordExplorer we = new WordExplorer(vocabulary);
        we.calculateWordsPca(false);
        WordList wordList = we.getVocabulary().getWordList("Test", "hombre", "mujer");
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
        boolean labelXYPoints = true;

        boolean drawOriginAxis = true;

        assertDoesNotThrow(() -> new ScatterPlot(Arrays.asList(wordList), title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, labelXYPoints, drawOriginAxis));
    }
}

package com.biasexplorer4j.WordExploration.BiasExplorer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.WordExploration.BiasExploration.BiasExplorer;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PlotManager;
import com.biasexplorer4j.WordExploration.Vocabulary.Vocabulary;
import com.biasexplorer4j.WordExploration.Vocabulary.Word;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

public class BiasExplorerTest {
    
    @Test
    public void instantiateWithNullReferencedVocabulary() {
        Vocabulary vocabulary = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasExplorer(vocabulary),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Vocabulary can not be null"));
    }

    @Test 
    public void succesfulInstatiation() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);

        assertDoesNotThrow(() -> new BiasExplorer(vocabulary));
    }

    @Nested
    public class WithBeforeInitialization {
        private BiasExplorer biasExplorer= null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
            Vocabulary vocabulary = new Vocabulary(data);

            this.biasExplorer = new BiasExplorer(vocabulary);
        }

        @AfterEach
        public void clean() {
            PlotManager.getInstance().cleanUp();
        }

        @Test
        public void plot2SpacesWithNullLists() {
            List<String> words = null;
            WordList<Word> kernel_1 = null;
            WordList<Word> kernel_2 = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("No list of words can be null"));
        }

        @Test
        public void plot2Spaces() {
            List<String> words = Arrays.asList("hombre");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("femenino", "mujer");
            double[] projections = this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2);

            System.out.println(projections[0]);
            assertEquals(1, projections.length);
            assertEquals(0.46838, projections[0], 0.0001);
        }

        @Test
        public void plot2SpacesWithOOVWordsInWordsToPlot() {
            List<String> words = Arrays.asList("hombre", "papaya21");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("femenino", "mujer");
            double[] projections = this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2);

            assertEquals(1, projections.length);
            assertEquals(0.46838, projections[0], 0.0001);
        }

        @Test
        public void plot2SpacesWithOOVWordsInKernel() {
            List<String> words = Arrays.asList("hombre");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre", "papaya21");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("femenino", "mujer", "carcaj2d");
            double[] projections = this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2);

            assertEquals(1, projections.length);
            assertEquals(0.46838, projections[0], 0.0001);
        }

        @Test
        public void plot2SpacesWithSameKernelDefinition() {
            List<String> words = Arrays.asList("hombre");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre", "papaya21");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre", "papaya21");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Kernels can not be defined by the same words"));
        }

        @Test
        public void plot4SpacesWithNullLists() {
            List<String> words = null;
            WordList<Word> kernel_1 = null;
            WordList<Word> kernel_2 = null;
            WordList<Word> kernel_3 = null;
            WordList<Word> kernel_4 = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("No list of words can be null"));
        }

        @Test
        public void plot4Spaces() {
            List<String> words = Arrays.asList("hombre");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("femenino", "mujer");
            WordList<Word> kernel_3 = this.biasExplorer.getVocabulary().getWordList("rey", "rey");
            WordList<Word> kernel_4 = this.biasExplorer.getVocabulary().getWordList("reina", "reina");
            double[][] projections = this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4);

            assertEquals(2, projections.length);
            assertEquals(1, projections[0].length);
            assertEquals(1, projections[1].length);
            assertEquals(0.46831, projections[0][0], 0.0001);
            assertEquals(0.20717, projections[1][0], 0.0001);
        }

        @Test
        public void plot4SpacesWithOOVWordsInWordsToPlot() {
            List<String> words = Arrays.asList("hombre", "papaya21");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("femenino", "mujer");
            WordList<Word> kernel_3 = this.biasExplorer.getVocabulary().getWordList("rey", "rey");
            WordList<Word> kernel_4 = this.biasExplorer.getVocabulary().getWordList("reina", "reina");
            double[][] projections = this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4);

            assertEquals(2, projections.length);
            assertEquals(1, projections[0].length);
            assertEquals(1, projections[1].length);
            assertEquals(0.46831, projections[0][0], 0.0001);
            assertEquals(0.20717, projections[1][0], 0.0001);
        }

        @Test
        public void plot4SpacesWithOOVWordsInKernel() {
            List<String> words = Arrays.asList("hombre");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre", "papaya21");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("femenino", "mujer", "papaya21");
            WordList<Word> kernel_3 = this.biasExplorer.getVocabulary().getWordList("rey", "rey", "papaya21");
            WordList<Word> kernel_4 = this.biasExplorer.getVocabulary().getWordList("reina", "reina", "papaya21");
            double[][] projections = this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4);

            assertEquals(2, projections.length);
            assertEquals(1, projections[0].length);
            assertEquals(1, projections[1].length);
            assertEquals(0.46831, projections[0][0], 0.0001);
            assertEquals(0.20717, projections[1][0], 0.0001);
        }

        @Test
        public void plot4SpacesWithSameKernelDefinition() {
            List<String> words = Arrays.asList("hombre");
            WordList<Word> kernel_1 = this.biasExplorer.getVocabulary().getWordList("masculino", "hombre");
            WordList<Word> kernel_2 = this.biasExplorer.getVocabulary().getWordList("femenino", "mujer");
            WordList<Word> kernel_3 = this.biasExplorer.getVocabulary().getWordList("viejo_1", "viejo");
            WordList<Word> kernel_4 = this.biasExplorer.getVocabulary().getWordList("viejo_2", "viejo");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Kernels can not be defined by the same words"));
        }
    }
}

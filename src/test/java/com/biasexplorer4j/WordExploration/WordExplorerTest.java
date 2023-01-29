package com.biasexplorer4j.WordExploration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PlotManager;
import com.biasexplorer4j.WordExploration.Vocabulary.Vocabulary;
import com.biasexplorer4j.WordExploration.Vocabulary.Word;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

public class WordExplorerTest {
    
    @Test
    public void instantiateWithNullReferenceVocabulary() {
        Vocabulary vocabulary = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new WordExplorer(vocabulary),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Vocabulary can not be null"));
    }

    @Test 
    public void succesfulInstatiation() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);

        assertDoesNotThrow(() -> new WordExplorer(vocabulary));
    }

    @Nested
    class WithBeforeInitialization {

        private WordExplorer wordExplorer= null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
            Vocabulary vocabulary = new Vocabulary(data);

            this.wordExplorer = new WordExplorer(vocabulary);
        }

        @AfterEach
        public void clean() {
            PlotManager.getInstance().cleanUp();
        }

        @Test
        public void correctDimensionSetting() {
            assertEquals(300, this.wordExplorer.getVocabulary().getEmbeddingDimension());
        }

        @Test
        public void noThrowsWithPCACalculationWithoutNormalization() {
            assertDoesNotThrow(() -> this.wordExplorer.calculateWordsPca(false));
        }

        @Test
        public void noThrowsWithPCACalculationWithNormalization() {
            assertDoesNotThrow(() -> this.wordExplorer.calculateWordsPca(true));
        }

        @Test
        public void noThrowsWithPCACalculationMultipleTimes() {
            assertDoesNotThrow(() -> this.wordExplorer.calculateWordsPca(true));
            assertDoesNotThrow(() -> this.wordExplorer.calculateWordsPca(true));
            assertDoesNotThrow(() -> this.wordExplorer.calculateWordsPca(false));
        }

        @Test
        public void correctPCACalculationWithoutNormalization() {
            this.wordExplorer.calculateWordsPca(false);
            Word word = wordExplorer.getVocabulary().get("mujer");
            
            assertEquals(-0.01724, word.getPca(0), 0.0001);
            assertEquals(0.63126, word.getPca(1), 0.0001);
        }

        @Test
        public void getNeighboursWithNullReferencedWordList() {
            List<WordList<Word>> lists = null;
            int k = 3;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.getNeighbours(lists, k),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Must provide at least one word list to plot"));
        }

        @Test
        public void getNeighboursOfSingleWordList() {
            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));
            int k = 3;
            Map<WordList<Word>, List<String>> n_map = this.wordExplorer.getNeighbours(lists, k);

            assertEquals(1, n_map.size());
            assertEquals(3, n_map.get(lists.get(0)).size());
            assertIterableEquals(Arrays.asList("mujer", "viejo", "rey"), n_map.get(lists.get(0)));
        }
        
        @Test
        public void getNeighboursOfSingleWordListWithOOVWords() {
            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre", "papaya21"));
            int k = 1;
            Map<WordList<Word>, List<String>> n_map = this.wordExplorer.getNeighbours(lists, k);

            assertEquals(1, n_map.size());
            assertEquals(1, n_map.get(lists.get(0)).size());
            assertIterableEquals(Arrays.asList("mujer"), n_map.get(lists.get(0)));
        }

        @Test
        public void getNeighboursOfMultipleWordList() {
            WordList<Word> list_1 = this.wordExplorer.getVocabulary().getWordList("masculino", "hombre");
            WordList<Word> list_2 = this.wordExplorer.getVocabulary().getWordList("femenino", "mujer");
            WordList<Word> list_3 = this.wordExplorer.getVocabulary().getWordList("adulto", "viejo", "mayor", "maduro");
            
            List<WordList<Word>> lists = Arrays.asList(list_1, list_2, list_3);
            int k = 3;
            Map<WordList<Word>, List<String>> n_map = this.wordExplorer.getNeighbours(lists, k);

            assertEquals(3, n_map.size());
            assertEquals(3, n_map.get(lists.get(0)).size());
            assertIterableEquals(Arrays.asList("mujer", "viejo", "rey"), n_map.get(lists.get(0)));

            assertEquals(3, n_map.get(lists.get(1)).size());
            assertIterableEquals(Arrays.asList("hombre", "reina", "viejo"), n_map.get(lists.get(1)));

            assertEquals(3, n_map.get(lists.get(2)).size());
            assertIterableEquals(Arrays.asList("hombre", "rey", "reina"), n_map.get(lists.get(2)));
        }
        
        @Test
        public void getZeroNeighboursFromList() {
            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));
            int k = 0;
            Map<WordList<Word>, List<String>> n_map = this.wordExplorer.getNeighbours(lists, k);

            assertEquals(1, n_map.size());
            assertEquals(0, n_map.get(lists.get(0)).size());
        }

        @Test
        public void getNeighboursWithNegativeQuantity() {
            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));
            int k = -2;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.getNeighbours(lists, k),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Number of neighbours to retrive from words, k, should be greater or equal than zero"));
        }

        @Test
        public void whenSerachingForOneNeighbourIsNotTheSameWord() {
            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));
            int k = 1;
            Map<WordList<Word>, List<String>> n_map = this.wordExplorer.getNeighbours(lists, k);

            assertEquals(1, n_map.size());
            assertEquals(1, n_map.get(lists.get(0)).size());
            assertFalse(n_map.get(lists.get(0)).get(0).equals("hombre"));
        }

        @Test
        public void plotNullReferencedWordList() {
            List<WordList<Word>> lists = null;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.plot(lists),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Must provide at least one non-null word list to plot"));
        }

        @Test
        public void plotWithNegativeNumberOfNeighbours() {
            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));
            int numberOfNeighbours = -2;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.plot(lists, numberOfNeighbours),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Number of neighbours to retrive from words, k, should be greater or equal than zero"));
        }

        @Test
        public void plotWithoutNeighbours() {
            this.wordExplorer.calculateWordsPca(false);

            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));

            assertDoesNotThrow(() -> this.wordExplorer.plot(lists));
        }

        @Test
        public void plotWithWordListWithNeighboursAndVerifyChangeInWordList() {
            this.wordExplorer.calculateWordsPca(false);

            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));
            int numberOfNeighbours = 2;

            assertDoesNotThrow(() -> this.wordExplorer.plot(lists, numberOfNeighbours));
            assertIterableEquals(Arrays.asList("hombre", "mujer", "viejo"), lists.get(0).getWordList());
        }

        @Test
        public void plotWithZeroNeighbours() {
            this.wordExplorer.calculateWordsPca(false);

            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre"));
            int numberOfNeighbours = 0;

            assertDoesNotThrow(() -> this.wordExplorer.plot(lists, numberOfNeighbours));
            assertIterableEquals(Arrays.asList("hombre"), lists.get(0).getWordList());
        }

        @Test
        public void plotWithOOVWords() {
            this.wordExplorer.calculateWordsPca(false);

            List<WordList<Word>> lists = Arrays.asList(this.wordExplorer.getVocabulary()
                                                                        .getWordList("masculino", "hombre", "papaya21"));
            int numberOfNeighbours = 2;

            assertDoesNotThrow(() -> this.wordExplorer.plot(lists, numberOfNeighbours));
            assertIterableEquals(Arrays.asList("hombre", "mujer", "viejo"), lists.get(0).getWordList());
        }
    }
}

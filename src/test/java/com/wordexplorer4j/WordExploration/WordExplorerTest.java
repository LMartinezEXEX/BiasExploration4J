package com.wordexplorer4j.WordExploration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.DoubleStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nd4j.linalg.exception.ND4JIllegalStateException;

import com.wordexplorer4j.SetupExtension;
import com.wordexplorer4j.DataLoader.DataLoader;
import com.wordexplorer4j.DataLoader.VecLoader;

@Order(10)
@ExtendWith(SetupExtension.class)
public class WordExplorerTest {
    
    @Test
    public void instantiateWithNullReferenceMap() {
        Map<String, double[]> map = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new WordExplorer(map),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Embeddings map can not be null"));
    }

    @Test
    public void instantiateWithNullReferenceDataLoader() {
        DataLoader data = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new WordExplorer(data),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Data loader can not be null"));
    }

    @Test
    public void instantiateWithNonLoadedDataLoader() {
        DataLoader data = new VecLoader();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new WordExplorer(data),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Data loader has no embedding mapping. May result from omitting a call to loadDataset()"));
    }

    @Test
    public void instantiateWithIrregularEmbeddingSizes() {
        int normalEmbeddingSize = 30;
        int irregularEmbeddingSize = 10;

        Map<String, double[]> map = new HashMap<>(10);
        for (int i = 0; i < 9; ++i) {
            String token = "token_" + i;
            double[] embedding = DoubleStream.generate(() -> new Random().nextDouble()).limit(normalEmbeddingSize).toArray();
            map.put(token, embedding);
        }
        map.put("Irregular embedding", DoubleStream.generate(() -> new Random().nextDouble()).limit(irregularEmbeddingSize).toArray());

        ND4JIllegalStateException thrown = assertThrows(ND4JIllegalStateException.class, 
                                                            () -> new WordExplorer(map),
                                                            "Expectedt ND4JIllegalStateException but not thrown");

        assertTrue(thrown.getMessage().equals("Shape of the new array [1, " + normalEmbeddingSize + "] doesn't match data length: " + irregularEmbeddingSize + " - prod(shape) must equal the number of values provided"));
    }

    @Nested
    class WithBeforeInitialization {

        private WordExplorer wordExplorer= null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader();
            data.loadDataset(Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.vec"));

            this.wordExplorer = new WordExplorer(data);
        }

        @Test
        public void assureWordsMapIsInmutable() {
            Map<String, Word> wordsMap = this.wordExplorer.getWordsMap();
            assertThrows(UnsupportedOperationException.class, 
                        () -> wordsMap.put("Should Fail", null),
                        "Expectedt UnsupportedOperationException but not thrown");
        }

        @Test
        public void correctDimensionSetting() {
            assertEquals(300, this.wordExplorer.getEmbeddingDimension());
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
            this.wordExplorer.calculateWordsPca(true);
            Word word = wordExplorer.getWordsMap().get("mujer");
            
            assertEquals(0.3253998453426682, word.getPca(0));
            assertEquals(0.47227102629952966, word.getPca(1));
        }

        @Test
        public void getNeighboursOfNullList() {
            List<String> words = null; 
            int k = 3;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.getNeighbours(words, k),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list can not be null"));
        }

        @Test
        public void getNeighboursOfEmptyList() {
            List<String> words = Arrays.asList();
            int k = 3;
            Map<String, List<String>> n_map = this.wordExplorer.getNeighbours(words, k);

            assertEquals(0, n_map.size());
        }

        @Test
        public void getZeroNeighboursFromList() {
            List<String> words = Arrays.asList("hombre", "mujer");
            int k = 0;
            Map<String, List<String>> n_map = this.wordExplorer.getNeighbours(words, k);

            assertEquals(2, n_map.size());
            assertEquals(0, n_map.get("hombre").size());
            assertEquals(0, n_map.get("mujer").size());
        }

        @Test
        public void getNeighboursWithNegativeQuantity() {
            List<String> words = Arrays.asList("hombre", "mujer");
            int k = -2;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.getNeighbours(words, k),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Number of neighbours to retrive from words, k, should be greater or equal than zero"));
        }

        @Test
        public void getNeighboursWithOOVWords() {
            List<String> words = Arrays.asList("hombre", "mujer", "papaya21");
            int k = 1;
            Map<String, List<String>> n_map = this.wordExplorer.getNeighbours(words, k);

            assertEquals(2, n_map.size());
            assertEquals(1, n_map.get("hombre").size());
            assertEquals(1, n_map.get("mujer").size());
            assertFalse(n_map.containsKey("papaya21"));
        }

        @Test
        public void whenSerachingForOneNeighbourIsNotTheSameWord() {
            List<String> words = Arrays.asList("hombre");
            int k = 1;
            Map<String, List<String>> n_map = this.wordExplorer.getNeighbours(words, k);

            assertEquals(1, n_map.size());
            assertEquals(1, n_map.get("hombre").size());
            assertFalse(n_map.get("hombre").get(0).equals("hombre"));
        }

        @Test
        public void plotNullList() {
            List<String> words = null;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.plot(words),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list can not be null"));
        }

        @Test
        public void plotWithNegativeNumberOfNeighbours() {
            List<String> words = Arrays.asList("hombre");
            int numberOfNeighbours = -2;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.wordExplorer.plot(words, numberOfNeighbours),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Number of neighbours to retrive from words, should be greater or equal than zero"));
        }

        @Test
        public void plotWithoutNeighbours() {
            List<String> words = Arrays.asList("hombre");
            this.wordExplorer.calculateWordsPca(false);

            assertDoesNotThrow(() -> this.wordExplorer.plot(words));
        }

        @Test
        public void plotWithNeighbours() {
            List<String> words = Arrays.asList("hombre");
            int numberOfNeighbours = 2;
            this.wordExplorer.calculateWordsPca(false);

            assertDoesNotThrow(() -> this.wordExplorer.plot(words, numberOfNeighbours));
        }

        @Test
        public void plotWithZeroNeighbours() {
            List<String> words = Arrays.asList("hombre");
            int numberOfNeighbours = 0;
            this.wordExplorer.calculateWordsPca(false);

            assertDoesNotThrow(() -> this.wordExplorer.plot(words, numberOfNeighbours));
        }

        @Test
        public void plotWithOOVWords() {
            List<String> words = Arrays.asList("hombre", "papaya21");
            int numberOfNeighbours = 2;
            this.wordExplorer.calculateWordsPca(false);

            assertDoesNotThrow(() -> this.wordExplorer.plot(words, numberOfNeighbours));
        }

        @Test
        public void plotOnlyOOVWords() {
            List<String> words = Arrays.asList("papaya21", "carcaj2d");
            int numberOfNeighbours = 2;
            this.wordExplorer.calculateWordsPca(false);
            
            assertDoesNotThrow(() -> this.wordExplorer.plot(words, numberOfNeighbours));
        }
    }
}

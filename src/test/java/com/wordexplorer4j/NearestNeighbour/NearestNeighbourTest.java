package com.wordexplorer4j.NearestNeighbour;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.wordexplorer4j.DataLoader.DataLoader;
import com.wordexplorer4j.DataLoader.VecLoader;

public class NearestNeighbourTest {
    
    @Test
    public void testNullInstantiation() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                        () -> new NearestNeighbour(null),
                                                        "Expectedt IllegalArgumentException but not thrown");
                          
        assertTrue(thrown.getMessage().equals("Labeled Points can not be null"));
    }

    @Test
    public void testWithCorrectDataInstatiation() {
        Map<String, INDArray> map = new HashMap<>(10);
        for (int i = 0; i < 10; ++i) {
            String token = "Token_" + i;
            INDArray randomEmbedding = Nd4j.rand(1, 300);

            map.put(token, randomEmbedding);
        }

        assertDoesNotThrow(() -> new NearestNeighbour(map));
    }

    @Test
    public void testWithIncorrectDataInstatiation() {
        Map<String, INDArray> map = new HashMap<>(10);
        for (int i = 0; i < 9; ++i) {
            String token = "Token_" + i;
            INDArray randomEmbedding = Nd4j.rand(1, 300);

            map.put(token, randomEmbedding);
        }
        map.put("Different size embedding", Nd4j.rand(1, 10));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                        () -> new NearestNeighbour(map),
                                                        "Expectedt IllegalArgumentException but not thrown");
                          
        assertTrue(thrown.getMessage().equals("Length of both arrays must be equal"));
    }

    @Nested
    class WithBeforeInitialization {
        private NearestNeighbour nearestNeighbour = null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader();
            data.loadDataset(Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.vec"));
            Map<String, double[]> embeddingMap = data.getEmbeddings();

            Map<String, INDArray> labeledPoints = new HashMap<>(embeddingMap.size());
            for (Map.Entry<String, double[]> e : embeddingMap.entrySet()) {
                INDArray embedding = Nd4j.create(e.getValue(), new int[] {1, data.getEmbeddingDimension()});
                labeledPoints.put(e.getKey(), embedding);
            }

            this.nearestNeighbour = new NearestNeighbour(labeledPoints);
        }

        @Test
        public void serachNeighboursWithNullList() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> nearestNeighbour.getKNearestNeighbour(null, 3),
                                                            "Expectedt IllegalArgumentException but not thrown");
                            
            assertTrue(thrown.getMessage().equals("Word list to calculate neighbours can not be null"));
        }

        @Test
        public void serachNeighboursWithEmptyList() {
            List<String> words = Arrays.asList();
            Map<String, List<String>> n_map = nearestNeighbour.getKNearestNeighbour(words, 2);

            assertEquals(0, n_map.size());
        }

        @Test
        public void serachZeroNeighbours() {
            List<String> words = Arrays.asList("mujer", "hombre");
            Map<String, List<String>> n_map = nearestNeighbour.getKNearestNeighbour(words, 0);

            assertEquals(2, n_map.size());
            assertEquals(0, n_map.get("mujer").size());
            assertEquals(0, n_map.get("hombre").size());
        }

        @Test
        public void serachNeighboursWithNegativeQuantity() {
            List<String> words = Arrays.asList("mujer", "hombre");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> nearestNeighbour.getKNearestNeighbour(words, -1),
                                                            "Expectedt IllegalArgumentException but not thrown");
                            
            assertTrue(thrown.getMessage().equals("Number of neighbours to retrive from words, k, should be greater or equal than zero"));
        }

        @Test
        public void searchNeighboursWithOOVWords() {
            List<String> words = Arrays.asList("papaya21");
            Map<String, List<String>> n_map = nearestNeighbour.getKNearestNeighbour(words, 2);

            assertEquals(0, n_map.size());
        }

        @Test
        public void searchForMoreNeighboursThanLabeledPoints() {
            List<String> words = Arrays.asList("mujer");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> nearestNeighbour.getKNearestNeighbour(words, 100),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Can't retrieve more neighbours than words used to initialize this object"));
        }

        @Test
        public void correctNeighboursSearch() {
            List<String> words = Arrays.asList("mujer", "hombre");
            Map<String, List<String>> n_map = nearestNeighbour.getKNearestNeighbour(words, 2);

            assertEquals(2, n_map.size());
            assertIterableEquals(Arrays.asList("hombre", "reina"), n_map.get("mujer"));
            assertIterableEquals(Arrays.asList("mujer", "viejo"), n_map.get("hombre"));
        }
    }
}

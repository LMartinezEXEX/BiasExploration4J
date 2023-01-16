package com.wordexplorer4j.WordExploration.Visualization;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.wordexplorer4j.DataLoader.DataLoader;
import com.wordexplorer4j.DataLoader.VecLoader;
import com.wordexplorer4j.WordExploration.Word;
import com.wordexplorer4j.WordExploration.WordExplorer;

import javafx.stage.Stage;

@Order(9)
public class BiasVisualizer_4SpacesTest {
    
    @Test
    public void initializeWithNullListOfWords() {
        List<String> words = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, null, null, null, null, null),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("List of words can not be null"));
    }

    @Test
    public void initializeWithNullListOfProjections() {
        List<String> words = Arrays.asList("hombre", "mujer", "rey");
        double[][] projections = null;
        List<String> kernel_1 = Arrays.asList("hombre");
        List<String> kernel_2 = Arrays.asList("mujer");
        List<String> kernel_3 = Arrays.asList("viejo");
        List<String> kernel_4 = Arrays.asList("joven");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Projections array can not be null"));
    }

    @Test
    public void initializeWithOnlyOneProjection() {
        List<String> words = Arrays.asList("hombre", "mujer", "rey");
        double[][] projections = new double[][] { new double[3] };
        List<String> kernel_1 = Arrays.asList("hombre");
        List<String> kernel_2 = Arrays.asList("mujer");
        List<String> kernel_3 = Arrays.asList("viejo");
        List<String> kernel_4 = Arrays.asList("joven");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Projections must have just and only 2 elemens (for x and y)"));
    }

    @Test
    public void initializeWithMoreThanTwoProjection() {
        List<String> words = Arrays.asList("hombre", "mujer", "rey");
        double[][] projections = new double[][] { new double[3], new double[3], new double[3], new double[3] };
        List<String> kernel_1 = Arrays.asList("hombre");
        List<String> kernel_2 = Arrays.asList("mujer");
        List<String> kernel_3 = Arrays.asList("viejo");
        List<String> kernel_4 = Arrays.asList("joven");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Projections must have just and only 2 elemens (for x and y)"));
    }

    @Test
    public void initializeWithIreggularXProjectionSize() {
        List<String> words = Arrays.asList("hombre", "mujer", "rey");
        double[][] projections = new double[][] { new double[4], new double[3]};
        List<String> kernel_1 = Arrays.asList("hombre");
        List<String> kernel_2 = Arrays.asList("mujer");
        List<String> kernel_3 = Arrays.asList("viejo");
        List<String> kernel_4 = Arrays.asList("joven");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("X projections size (4) differs from amount of words (3)"));
    }

    @Test
    public void initializeWithIreggularYProjectionSize() {
        List<String> words = Arrays.asList("hombre", "mujer", "rey");
        double[][] projections = new double[][] { new double[3], new double[2]};
        List<String> kernel_1 = Arrays.asList("hombre");
        List<String> kernel_2 = Arrays.asList("mujer");
        List<String> kernel_3 = Arrays.asList("viejo");
        List<String> kernel_4 = Arrays.asList("joven");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Y projections size (2) differs from amount of words (3)"));
    }

    @Test
    public void initializeWithNullReferencedKernelDefinition() {
        List<String> words = Arrays.asList("hombre", "mujer", "rey");
        double[][] projections = new double[][] { new double[3], new double[3]};
        List<String> kernel_1 = Arrays.asList("hombre");
        List<String> kernel_2 = Arrays.asList("mujer");
        List<String> kernel_3 = null;
        List<String> kernel_4 = Arrays.asList("joven");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Kernels list definition can not be null"));
    }

    @Test
    public void initializeWithEmptyKernelDefinition() {
        List<String> words = Arrays.asList("hombre", "mujer", "rey");
        double[][] projections = new double[][] { new double[3], new double[3]};
        List<String> kernel_1 = Arrays.asList("hombre");
        List<String> kernel_2 = Arrays.asList("mujer");
        List<String> kernel_3 = Arrays.asList();
        List<String> kernel_4 = Arrays.asList("joven");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Kernels list should not be empty"));
    }

    @Nested
    public class WithBeforeInitialization {
        
        private BiasVisualizer_4Spaces visualizer = null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader();
            data.loadDataset(Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.vec"));
            WordExplorer wordExplorer = new WordExplorer(data);

            int entrySize = wordExplorer.getWordsMap().size();
            List<String> words = new ArrayList<>(entrySize);
            double[][] projections = new double[][] {new double[entrySize], new double[entrySize]};

            int idx = 0;
            for (Map.Entry<String, Word> e : wordExplorer.getWordsMap().entrySet()) {
                words.add(e.getKey());

                double mockXProjection = e.getValue().getEmbedding().getDouble(1, 0);
                double mockYProjection = e.getValue().getEmbedding().getDouble(1, 1);
                projections[0][idx] = mockXProjection;
                projections[1][idx] = mockYProjection;
                ++idx;
            }

            List<String> kernel_1 = Arrays.asList("hombre", "chico", "el", "masculino");
            List<String> kernel_2 = Arrays.asList("mujer", "chica", "ella", "femenino");
            List<String> kernel_3 = Arrays.asList("maduro", "viejo", "adulto", "anciano");
            List<String> kernel_4 = Arrays.asList("inmaduro", "joven", "chico");

            this.visualizer = new BiasVisualizer_4Spaces(words, projections, kernel_1, kernel_2, kernel_3, kernel_4);
        }

        @Test
        public void plotWithNullReferencedStage() {
            Stage stage = null;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                                () -> this.visualizer.plot(stage),
                                                                "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Stage to put chart into can not be null"));
        }
    }
}

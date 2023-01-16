package com.wordexplorer4j.WordExploration.Visualization;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.wordexplorer4j.DataLoader.DataLoader;
import com.wordexplorer4j.DataLoader.VecLoader;
import com.wordexplorer4j.WordExploration.Word;
import com.wordexplorer4j.WordExploration.WordExplorer;
import com.wordexplorer4j.WordExploration.BiasExploration.ProjectedWord;

import javafx.stage.Stage;

public class BiasVisualizer_2SpacesTest {
    
    @Test
    public void initializeWithNullProjectedWordList() {
        List<ProjectedWord> projectedWords = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_2Spaces(projectedWords, null, null),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("List of words can not be null"));
    }

    @Test
    public void initializeWithNullDefinedKernel() {
        List<ProjectedWord> projectedWords = new ArrayList<>();
        List<String> kernel_1 = null;
        List<String> kernel_2 = null;

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_2Spaces(projectedWords, kernel_1, kernel_2),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Kernels list definition can not be null"));
    }

    @Test
    public void initializeWithEmptyDefinedKernel() {
        List<ProjectedWord> projectedWords = new ArrayList<>();
        List<String> kernel_1 = Arrays.asList();
        List<String> kernel_2 = Arrays.asList("hombre");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasVisualizer_2Spaces(projectedWords, kernel_1, kernel_2),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Kernels list should not be empty"));
    }

    @Test
    public void initializeProperly() {
        List<ProjectedWord> projectedWords = new ArrayList<>(3);
        for (int i = 0; i < 3; ++ i) {
            String token = "token_" + i;
            double projection = i;
            projectedWords.add(new ProjectedWord(token, projection));
        }
        List<String> kernel_1 = Arrays.asList("viejo");
        List<String> kernel_2 = Arrays.asList("hombre");

        assertDoesNotThrow(() -> new BiasVisualizer_2Spaces(projectedWords, kernel_1, kernel_2));
    }
    
    @Nested
    public class WithBeforeInitialization {
        
        private BiasVisualizer_2Spaces visualizer = null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader();
            data.loadDataset(Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.vec"));
            WordExplorer wordExplorer = new WordExplorer(data);

            List<ProjectedWord> projectedWords = new ArrayList<>(wordExplorer.getWordsMap().size());
            for (Map.Entry<String, Word> e : wordExplorer.getWordsMap().entrySet()) {
                String word = e.getKey();
                double mockProjection = e.getValue().getEmbedding().getDouble(1, 0);
                projectedWords.add(new ProjectedWord(word, mockProjection));
            }

            List<String> kernel_1 = Arrays.asList("hombre", "chico", "el", "masculino");
            List<String> kernel_2 = Arrays.asList("mujer", "chica", "ella", "femenino");

            this.visualizer = new BiasVisualizer_2Spaces(projectedWords, kernel_1, kernel_2);
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

package com.biasexplorer4j.WordExploration.Visualization;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.WordExploration.Word;
import com.biasexplorer4j.WordExploration.WordExplorer;

import javafx.stage.Stage;

@Order(7)
public class WordExplorerVisualizerTest {
    
    @Test
    public void initializeWithNullReferenceListOfWords() {
        List<Word> words = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new WordExplorerVisualizer(words),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Word list can not be null"));
    }

    @Test
    public void initializeWithEmptyListOfWords() {
        List<Word> words = Arrays.asList();
        assertDoesNotThrow(() -> new WordExplorerVisualizer(words));
    }


    @Test
    public void initializeWithListOfWords() {
        List<Word> words = new ArrayList<>(2);
        for (int i = 0; i < 2 ; ++i) {
            String token = "token_" + i;
            INDArray embedding = Nd4j.rand(1, 300);
            words.add(new Word(token, embedding));
        }
        assertDoesNotThrow(() -> new WordExplorerVisualizer(words));
    }

    @Test
    public void initializeWithNoArgsContructor() {
        assertDoesNotThrow(() -> new WordExplorerVisualizer());
    }

    @Nested
    public class WithBeforeInitialization{
        private WordExplorerVisualizer visualizer = null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader();
            data.loadDataset(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
            WordExplorer wordExplorer = new WordExplorer(data);

            this.visualizer = new WordExplorerVisualizer(new ArrayList<>(wordExplorer.getWordsMap().values()));
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

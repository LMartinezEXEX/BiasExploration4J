package com.wordexplorer4j.WordExploration.BiasExplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.wordexplorer4j.DataLoader.DataLoader;
import com.wordexplorer4j.DataLoader.VecLoader;
import com.wordexplorer4j.WordExploration.WordExplorer;
import com.wordexplorer4j.WordExploration.BiasExploration.BiasExplorer;

public class BiasExplorerTest {
    
    @Test
    public void instantiateWithNullWordExplorerReference() {
        WordExplorer wordExplorer = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BiasExplorer(wordExplorer),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Word explorer object can not be null"));
    }

    @Nested
    public class WithBeforeInitialization {
        private BiasExplorer biasExplorer= null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader();
            data.loadDataset(Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.vec"));

            this.biasExplorer = new BiasExplorer(new WordExplorer(data));
        }

        @Test
        public void plot2SpacesWithNullLists() {
            List<String> words = null;
            List<String> kernel_1 = null;
            List<String> kernel_2 = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("No list of words can be null"));
        }

        @Test
        public void plot2Spaces() {
            List<String> words = Arrays.asList("hombre");
            List<String> kernel_1 = Arrays.asList("viejo", "hombre");
            List<String> kernel_2 = Arrays.asList("mujer");
            double[] projections = this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2);

            assertEquals(1, projections.length);
            assertEquals(0.27344, projections[0], 0.0001);
        }

        @Test
        public void plot2SpacesWithOOVWordsInWordsToPlot() {
            List<String> words = Arrays.asList("hombre", "papaya21");
            List<String> kernel_1 = Arrays.asList("viejo", "hombre");
            List<String> kernel_2 = Arrays.asList("mujer");
            double[] projections = this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2);

            assertEquals(1, projections.length);
            assertEquals(0.27344, projections[0], 0.0001);
        }

        @Test
        public void plot2SpacesWithOOVWordsInKernel() {
            List<String> words = Arrays.asList("hombre");
            List<String> kernel_1 = Arrays.asList("viejo", "hombre", "papaya21");
            List<String> kernel_2 = Arrays.asList("mujer", "papaya21");
            double[] projections = this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2);

            assertEquals(1, projections.length);
            assertEquals(0.27344, projections[0], 0.0001);
        }

        @Test
        public void plot2SpacesWithOnlyOOVWordsInKernel() {
            List<String> words = Arrays.asList("hombre");
            List<String> kernel_1 = Arrays.asList("viejo", "hombre");
            List<String> kernel_2 = Arrays.asList("papaya21");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Definition of kernel 2 is empty (After removing O.O.V. words)"));
        }

        @Test
        public void plot2SpacesWithSameKernelDefinition() {
            List<String> words = Arrays.asList("hombre");
            List<String> kernel_1 = Arrays.asList("viejo", "hombre");
            List<String> kernel_2 = Arrays.asList("viejo", "hombre");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot2SpaceBias(words, kernel_1, kernel_2),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Kernels can not be defined by the same words: " + "viejo, hombre"));
        }

        @Test
        public void plot4SpacesWithNullLists() {
            List<String> words = null;
            List<String> kernel_1 = null;
            List<String> kernel_2 = null;
            List<String> kernel_3 = null;
            List<String> kernel_4 = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("No list of words can be null"));
        }

        @Test
        public void plot4Spaces() {
            List<String> words = Arrays.asList("hombre");
            List<String> kernel_1 = Arrays.asList("hombre");
            List<String> kernel_2 = Arrays.asList("mujer");
            List<String> kernel_3 = Arrays.asList("rey");
            List<String> kernel_4 = Arrays.asList("reina");
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
            List<String> kernel_1 = Arrays.asList("hombre");
            List<String> kernel_2 = Arrays.asList("mujer");
            List<String> kernel_3 = Arrays.asList("rey");
            List<String> kernel_4 = Arrays.asList("reina");
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
            List<String> kernel_1 = Arrays.asList("hombre", "papaya21");
            List<String> kernel_2 = Arrays.asList("mujer", "papaya21");
            List<String> kernel_3 = Arrays.asList("rey", "papaya21");
            List<String> kernel_4 = Arrays.asList("reina", "papaya21");
            double[][] projections = this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4);

            assertEquals(2, projections.length);
            assertEquals(1, projections[0].length);
            assertEquals(1, projections[1].length);
            assertEquals(0.46831, projections[0][0], 0.0001);
            assertEquals(0.20717, projections[1][0], 0.0001);
        }

        @Test
        public void plot4SpacesWithOnlyOOVWordsInKernel() {
            List<String> words = Arrays.asList("hombre");
            List<String> kernel_1 = Arrays.asList("hombre");
            List<String> kernel_2 = Arrays.asList("mujer");
            List<String> kernel_3 = Arrays.asList("rey");
            List<String> kernel_4 = Arrays.asList("papaya21");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Definition of kernel 2 is empty (After removing O.O.V. words)"));
        }

        @Test
        public void plot4SpacesWithSameKernelDefinition() {
            List<String> words = Arrays.asList("hombre");
            List<String> kernel_1 = Arrays.asList("hombre");
            List<String> kernel_2 = Arrays.asList("mujer");
            List<String> kernel_3 = Arrays.asList("viejo");
            List<String> kernel_4 = Arrays.asList("viejo");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.biasExplorer.plot4SpaceBias(words, kernel_1, kernel_2, kernel_3, kernel_4),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Kernels can not be defined by the same words: " + "viejo"));
        }
    }
}

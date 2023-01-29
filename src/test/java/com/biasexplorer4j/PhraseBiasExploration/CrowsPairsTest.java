package com.biasexplorer4j.PhraseBiasExploration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CrowsPairsTest {
    
    @Test
    public void initializeWithNullReferencedModelUrl() {
        String modelURL = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new CrowsPairs(modelURL),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Model URL string can not be null"));  
    }

    @Test
    public void initializeWithInvalidModelUrl() {
        String modelURL = "invalidModelURL";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new CrowsPairs(modelURL),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Model not found")); 
    }

    @Test
    public void initializeWithValidModelUrl() {
        String modelURL = "djl://ai.djl.huggingface.pytorch/bert-base-uncased";
        assertDoesNotThrow(() -> new CrowsPairs(modelURL));
    }

    @Test
    public void initializeWithNoModelUrl() {
        assertDoesNotThrow(() -> new CrowsPairs());
    }

    @Nested
    public class WithBeforeeeeInitialization {

        private CrowsPairs cp = null;

        @BeforeEach
        public void setup() {
            this.cp = new CrowsPairs();
        }

        @Test
        public void compareWithNullReferencedPhrases() {
            List<String> phrases = null;
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.cp.compare(phrases),
                                                            "Expected IllegalArgumentException but not thrown");
            
            assertTrue(thrown.getMessage().equals("List of phrases can not be null")); 
        }

        @Test
        public void compareWithBadFormatWholePhrase() {
            List<String> phrases = Arrays.asList("<Can not contain all the phrase surrounded>");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.cp.compare(phrases),
                                                            "Expected IllegalArgumentException but not thrown");
            
            assertTrue(thrown.getMessage().equals("Sentence { <Can not contain all the phrase surrounded> } is wrongly formatted")); 
        }

        @Test
        public void compareWithBadFormatEmbeddedSurroundedWords() {
            List<String> phrases = Arrays.asList("Can <not have <embedded>> surrounded words");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.cp.compare(phrases),
                                                            "Expected IllegalArgumentException but not thrown");

            
            assertTrue(thrown.getMessage().equals("Sentence { Can <not have <embedded>> surrounded words } is wrongly formatted")); 
        }

        @Test
        public void compareWithReverseBadFormatEmbeddedSurroundedWords() {
            List<String> phrases = Arrays.asList("Can >not have reversed >embedded<< surrounded words");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.cp.compare(phrases),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Sentence { Can >not have reversed >embedded<< surrounded words } is wrongly formatted")); 
        }

        @Test
        public void compareWithNonBalancedSurroundedWords() {
            List<String> phrases = Arrays.asList("Phrase must be <balanced> with its <surrounded words");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.cp.compare(phrases),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Sentence { Phrase must be <balanced> with its <surrounded words } is wrongly formatted")); 
        }

        @Test
        public void compareWithReverseNonBalancedSurroundedWords() {
            List<String> phrases = Arrays.asList("Phrase must be >balanced< with its >surrounded words");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.cp.compare(phrases),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Sentence { Phrase must be >balanced< with its >surrounded words } is wrongly formatted")); 
        }

        @Test
        public void compareWithNoSurroundedWords() {
            List<String> phrases = Arrays.asList("Phrase can not have no surrounded words");
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.cp.compare(phrases),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Sentence { Phrase can not have no surrounded words } is wrongly formatted")); 
        }

        @Test
        public void compareWithOnlyOnePhrase() {
            List<String> phrases = Arrays.asList("Correctly <formatted> phrase, <right>?");
            Map<String, Double> map = this.cp.compare(phrases);
            
            assertEquals(1, map.size());
            assertEquals(1.0, map.get("Correctly <formatted> phrase, <right>?"));
        }

        @Test
        public void compareWithOnlyMultiplePhrases() {
            List<String> phrases = Arrays.asList("Correctly <formatted> phrase", "Correctly <other formatted> phrase", "Correctly <third> phrase");
            Map<String, Double> map = this.cp.compare(phrases);
            
            assertEquals(3, map.size());
            assertEquals(1.0, map.get("Correctly <formatted> phrase"));
            assertEquals(1.08039, map.get("Correctly <other formatted> phrase"), 0.0001);
            assertEquals(1.02420, map.get("Correctly <third> phrase"), 0.0001);
        }
    }
}

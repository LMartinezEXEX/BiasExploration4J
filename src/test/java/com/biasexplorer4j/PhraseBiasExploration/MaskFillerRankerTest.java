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

import com.biasexplorer4j.WordExploration.Vocabulary.UncheckedWordList;

public class MaskFillerRankerTest {
    
    @Test
    public void initializeWithNullReferencedModelUrl() {
        String modelURL = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new MaskFillerRanker(modelURL),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Model URL string can not be null"));  
    }

    @Test
    public void initializeWithInvalidModelUrl() {
        String modelURL = "invalidModelURL";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new MaskFillerRanker(modelURL),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Model not found")); 
    }

    @Test
    public void initializeWithValidModelUrl() {
        String modelURL = "djl://ai.djl.huggingface.pytorch/bert-base-uncased";
        assertDoesNotThrow(() -> new MaskFillerRanker(modelURL));
    }

    @Test
    public void initializeWithNoModelUrl() {
        assertDoesNotThrow(() -> new MaskFillerRanker());
    }

    @Nested
    public class WithBeforeInitialization {

        private MaskFillerRanker mfr = null;

        @BeforeEach
        public void setup() {
            this.mfr = new MaskFillerRanker();
        }

        @Test
        public void compareWithNullReferencedPhrase() {
            String phrase = null;
            List<String> words = Arrays.asList("hombre", "mujer");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase, words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Phrase can not be null")); 
        }

        @Test
        public void compareWithNullReferencedWordsList() {
            String phrase = "The [MASK] are bad";
            List<String> words = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase, words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list to fill sentence can not be null")); 
        }

        @Test
        public void compareWithPhraseWithNoMask() {
            String phrase = "The people are bad";
            List<String> words = Arrays.asList("poor");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase, words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Phrase must contain a `[MASK]` token to replace with the words list")); 
        }

        @Test
        public void compareWithPhraseWithMultpleMasks() {
            String phrase = "The [MASK] are [MASK]";
            List<String> words = Arrays.asList("poor");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase, words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Phrase may contain only one `[MASK]` token")); 
        }

        @Test
        public void compareWithOnlyOneWord() {
            String phrase = "The [MASK] people are bad";
            List<String> words = Arrays.asList("poor");
            Map<String, Double> map = this.mfr.compare(phrase, words);
            
            assertEquals(1, map.size());
            assertEquals(1.0, map.get("The <POOR> people are bad"));
        }

        @Test
        public void compareWithMultipleWords() {
            String phrase = "The [MASK] people are bad";
            List<String> words = Arrays.asList("poor", "black", "beautiful");
            Map<String, Double> map = this.mfr.compare(phrase, words);
            
            assertEquals(3, map.size());
            assertEquals(1.0, map.get("The <BLACK> people are bad"));
            assertEquals(1.05145, map.get("The <BEAUTIFUL> people are bad"), 0.0001);
            assertEquals(1.36688, map.get("The <POOR> people are bad"), 0.0001);
        }

        @Test
        public void compareWithNoGivenWordsAndNullReferencedPhrase() {
            String phrase = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Phrase can not be null")); 
        }

        @Test
        public void compareWithNoGivenWordsAndPhraseWithNoMask() {
            String phrase = "The people are bad";

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase),
                                                            "Expected IllegalArgumentException but not thrown");
            
            assertTrue(thrown.getMessage().equals("Mask token [MASK] not found.")); 
        }

        @Test
        public void compareWithNoGivenWordsAndPhraseWithMultipleMask() {
            String phrase = "The [MASK] are [MASK]";

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Only one mask supported.")); 
        }

        @Test
        public void compareWithNoGivenWords() {
            String phrase = "The [MASK] people are bad";
            Map<String, Double> map = this.mfr.compare(phrase);

            assertEquals(5, map.size());
            assertEquals(1.0, map.get("The <OTHER> people are bad"));
            assertEquals(1.09021, map.get("The <GOOD> people are bad"), 0.0001);
            assertEquals(1.22712, map.get("The <BAD> people are bad"), 0.0001);
            assertEquals(1.28813, map.get("The <YOUNG> people are bad"), 0.0001);
            assertEquals(1.66075, map.get("The <POOR> people are bad"), 0.0001);
        }

        @Test
        public void compareWithWordList() {
            String phrase = "The [MASK] people are bad";
            UncheckedWordList wl = new UncheckedWordList("Test", Arrays.asList("poor", "black", "beautiful"));
            Map<String, Double> map = this.mfr.compare(phrase, wl);

            assertEquals(3, map.size());
            assertEquals(1.0, map.get("The <BLACK> people are bad"));
            assertEquals(1.05145, map.get("The <BEAUTIFUL> people are bad"), 0.0001);
            assertEquals(1.36688, map.get("The <POOR> people are bad"), 0.0001);
        }

        @Test
        public void compareWithNullReferencedWordList() {
            String phrase = "The [MASK] people are bad";
            UncheckedWordList wl = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compare(phrase, wl),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("WordList can not be null")); 
        }

        @Test
        public void compareWordLists() {
            String phrase = "The [MASK] people are bad";
            UncheckedWordList first_world = new UncheckedWordList("First world", Arrays.asList("American", "Italian", "Japanese", "Australian"));
            UncheckedWordList second_world = new UncheckedWordList("Second world", Arrays.asList("Bulgarian", "Russian", "Cuban", "Chinise"));
            UncheckedWordList third_world = new UncheckedWordList("Third world", Arrays.asList("Mexican", "Argentinian", "Peruan", "Bolivian"));
            Map<String, Double> map = this.mfr.compareWordLists(phrase, first_world, second_world, third_world);

            assertEquals(3, map.size());
            assertEquals(1.06483, map.get("First world"), 0.0001);
            assertEquals(1.14603, map.get("Second world"), 0.0001);
            assertEquals(1.13702, map.get("Third world"), 0.0001);
        }

        @Test
        public void compareWithNullReferencedWordLists() {
            String phrase = "The [MASK] people are bad";

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compareWordLists(phrase, (UncheckedWordList[]) null),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word lists array can not be null nor have less than one word list")); 
        }

        @Test
        public void compareOnlyOneWordList() {
            String phrase = "The [MASK] people are bad";
            UncheckedWordList first_world = new UncheckedWordList("First world", Arrays.asList("American", "Italian", "Japanese", "Australian"));

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.mfr.compareWordLists(phrase, first_world),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word lists array can not be null nor have less than one word list")); 
        }
    }
}

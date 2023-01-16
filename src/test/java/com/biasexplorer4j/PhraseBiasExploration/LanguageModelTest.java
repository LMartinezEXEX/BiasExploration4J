package com.biasexplorer4j.PhraseBiasExploration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(3)
public class LanguageModelTest {

    @Test
    public void initializeWithNullReferencedModelUrl() {
        String modelURL = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new LanguageModel(modelURL),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Model URL string can not be null"));   
    }

    @Test
    public void initializeWithInvalidModelUrl() {
        String modelURL = "invalidModelURL";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new LanguageModel(modelURL),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Model not found")); 
    }

    @Test
    public void initializeWithValidModelUrl() {
        String modelURL = "djl://ai.djl.huggingface.pytorch/bert-base-uncased";
        assertDoesNotThrow(() -> new LanguageModel(modelURL));
    }

    @Test
    public void initializeWithNoModelUrl() {
        assertDoesNotThrow(() -> new LanguageModel());
    }

    @Nested
    public class WithBeforeInitialization {

        private LanguageModel lm = null;

        @BeforeEach
        public void setup() {
            this.lm = new LanguageModel();
        }

        @Test
        public void getTop5WithNullPhrase() {
            String phrase = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.getTop5(phrase),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Phrase can not be null"));
            
        }

        @Test
        public void getTop5WithPhraseNotMasked() {
            String phrase = "The people are bad";

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.getTop5(phrase),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Mask token [MASK] not found."));
            
        }

        @Test
        public void getTop5WithPhraseWithMultipleMasks() {
            String phrase = "The [MASK] people are [MASK] bad";

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.getTop5(phrase),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Only one mask supported."));
        }

        @Test
        public void getTop5() {
            String phrase = "The [MASK] people are bad";
            List<String> words = this.lm.getTop5(phrase);
            
            assertEquals(5, words.size());
            assertIterableEquals(Arrays.asList("good", "bad", "other", "young", "poor"), words);
        }

        @Test
        public void processInputWithNullPhrase() {
            String phrase = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.processInput(phrase),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Phrase can not be null"));
            
        }

        @Test
        public void processInputWithWholeNoSurroundedWords() {
            String phrase = "The poor people are bad";

            List<String> expectedProcessedPhrases = Arrays.asList("[MASK] poor people are bad", 
                                                                    "The [MASK] people are bad",
                                                                    "The poor [MASK] are bad",
                                                                    "The poor people [MASK] bad",
                                                                    "The poor people are [MASK]");

            assertIterableEquals(expectedProcessedPhrases, this.lm.processInput(phrase));
        }

        @Test
        public void processInputWithWholeWord() {
            String phrase = "The <poor> people are bad";

            List<String> expectedProcessedPhrases = Arrays.asList("[MASK] poor people are bad",
                                                                    "The poor [MASK] are bad",
                                                                    "The poor people [MASK] bad",
                                                                    "The poor people are [MASK]");

            assertIterableEquals(expectedProcessedPhrases, this.lm.processInput(phrase));
        }

        @Test
        public void processInputWithSeparatedNoSurroundedWord() {
            String phrase = "The poor people are sozoso bad"; // sozoso = so ##zo ##so

            List<String> expectedProcessedPhrases = Arrays.asList("[MASK] poor people are sozoso bad",
                                                                    "The [MASK] people are sozoso bad",
                                                                    "The poor [MASK] are sozoso bad",
                                                                    "The poor people [MASK] sozoso bad",
                                                                    "The poor people are [MASK] bad",
                                                                    "The poor people are [MASK] bad",
                                                                    "The poor people are [MASK] bad",
                                                                    "The poor people are sozoso [MASK]");

            assertIterableEquals(expectedProcessedPhrases, this.lm.processInput(phrase));
        }

        @Test
        public void processInputWithSeparatedWord() {
            String phrase = "The poor people are <sozoso> bad"; // sozoso = so ##zo ##so

            List<String> expectedProcessedPhrases = Arrays.asList("[MASK] poor people are sozoso bad",
                                                                    "The [MASK] people are sozoso bad",
                                                                    "The poor [MASK] are sozoso bad",
                                                                    "The poor people [MASK] sozoso bad",
                                                                    "The poor people are sozoso [MASK]");

            assertIterableEquals(expectedProcessedPhrases, this.lm.processInput(phrase));
        }

        @Test
        public void validTokensFromNullPhrase() {
            String phrase = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.getValidTokens(phrase),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Phrase can not be null"));
        }

        @Test
        public void validTokensFromPhraseWithNoSorroundedWords() {
            String phrase = "The people are bad";

            assertIterableEquals(Arrays.asList("the", "people", "are", "bad"), this.lm.getValidTokens(phrase));
        }

        @Test
        public void validTokensFromPhraseWithSorroundedWord() {
            String phrase = "The <people> are bad";

            assertIterableEquals(Arrays.asList("the", "are", "bad"), this.lm.getValidTokens(phrase));
        }

        @Test
        public void validTokensFromPhraseWithMultipleSorroundedWord() {
            String phrase = "The <people> are <bad>";

            assertIterableEquals(Arrays.asList("the", "are"), this.lm.getValidTokens(phrase));
        }

        @Test
        public void rankWithNullReferencedProcessedPhrases() {
            List<String> processedPrases = null;
            List<String> validTokens = Arrays.asList("the", "people", "are", "bad");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.rank(processedPrases, validTokens),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Processed phrases list can not be null"));
        }

        @Test
        public void rankWithNullReferencedValidTokens() {
            List<String> processedPrases = this.lm.processInput("The people are bad");
            List<String> validTokens = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.rank(processedPrases, validTokens),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Valid tokens list can not be null"));
        }

        @Test
        public void rankWithDifferentSizeListOfProcessedPhrasesAndValidTokens() {
            List<String> processedPrases = this.lm.processInput("The people are bad");
            List<String> validTokens = Arrays.asList("the", "people", "are");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.rank(processedPrases, validTokens),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Different amount of processed phrases (4) and valid tokens (3) encounteres"));
        }

        @Test
        public void rankWithAPhraseWithoutMask() {
            List<String> processedPrases = Arrays.asList("[MASK] poor people are bad",
                                                        "The poor people are bad",
                                                        "The poor people [MASK] bad",
                                                        "The poor people are [MASK]");

            List<String> validTokens = Arrays.asList("the", "people", "are", "bad");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.rank(processedPrases, validTokens),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Mask token [MASK] not found."));
        }

        @Test
        public void rankWithAPhraseWithMultipleMask() {
            List<String> processedPrases = Arrays.asList("[MASK] poor [MASK] are bad",
                                                        "The poor [MASK] are bad",
                                                        "The poor people [MASK] bad",
                                                        "The poor people are [MASK]");

            List<String> validTokens = Arrays.asList("the", "people", "are", "bad");

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> this.lm.rank(processedPrases, validTokens),
                                                            "Expectedt IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Only one mask supported."));
        }

        @Test
        public void rankWithValidInputs() {
            String phrase = "The people are bad";
            List<String> processedPrases = this.lm.processInput(phrase);
            List<String> validTokens = this.lm.getValidTokens(phrase);

            assertDoesNotThrow(() -> this.lm.rank(processedPrases, validTokens));
        }

        @Test
        public void correctRankOutputWithValidInputs() {
            String phrase = "The people are bad";
            List<String> processedPrases = this.lm.processInput(phrase);
            List<String> validTokens = this.lm.getValidTokens(phrase);

            double rank = this.lm.rank(processedPrases, validTokens);
            assertEquals(-10.12182, rank, 0.0001);
        }
    }
}

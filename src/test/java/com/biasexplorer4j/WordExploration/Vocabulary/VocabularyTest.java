package com.biasexplorer4j.WordExploration.Vocabulary;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import org.junit.jupiter.api.Test;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.WordExploration.BiasExploration.ProjectedWord;

public class VocabularyTest {

    public static double[] generateRandomEmbedding() {
        return DoubleStream.generate(() -> new Random().nextDouble()).limit(300).toArray();
    }
    
    @Test
    public void successfulInstantiationWithDataLoader() {
        DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
        Vocabulary vocabulary = new Vocabulary(data);

        assertEquals(5, vocabulary.size());
        assertTrue(vocabulary.contains("mujer"));
        assertTrue(vocabulary.contains("hombre"));
        assertTrue(vocabulary.contains("rey"));
        assertTrue(vocabulary.contains("reina"));
        assertTrue(vocabulary.contains("viejo"));
    }

    @Test
    public void successfulInstantiationWithMap() {
        Map<String, double[]> embeddingsMap = new HashMap<>(5);
        embeddingsMap.put("mujer", generateRandomEmbedding());
        embeddingsMap.put("hombre", generateRandomEmbedding());
        embeddingsMap.put("rey", generateRandomEmbedding());
        embeddingsMap.put("reina", generateRandomEmbedding());
        embeddingsMap.put("viejo", generateRandomEmbedding());

        Vocabulary vocabulary = new Vocabulary(embeddingsMap);

        assertEquals(5, vocabulary.size());
        assertTrue(vocabulary.contains("mujer"));
        assertTrue(vocabulary.contains("hombre"));
        assertTrue(vocabulary.contains("rey"));
        assertTrue(vocabulary.contains("reina"));
        assertTrue(vocabulary.contains("viejo"));
    }

    @Test
    public void instantiateWithNullReferencedDataLoader() {
        DataLoader data = null;
        
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new Vocabulary(data),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Data loader can not be null"));
    }

    @Test
    public void instantiateWithEmptyDataLoader() {
        DataLoader data = new VecLoader();
        
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new Vocabulary(data),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Data loader has no embedding mapping. May result from omitting a call to loadDataset()"));
    }

    @Test
    public void instantiateWithNullReferencedMap() {
        Map<String, double[]> embeddingsMap = null;
        
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new Vocabulary(embeddingsMap),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Embeddings map can not be null"));
    }

    @Test
    public void instantiateWithEmptyMap() {
        Map<String, double[]> embeddingsMap = new HashMap<>();
        
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new Vocabulary(embeddingsMap),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Embeddings map has no element"));
    }

    @Nested
    public class WithBeforeInitialization {

        private Vocabulary vocabulary = null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
            this.vocabulary = new Vocabulary(data);
        }

        @Test
        public void filterOOVwords() {
            List<String> filtered = vocabulary.filterInVocab(Arrays.asList("papaya21", "hombre", "carcaj2d", "viejo"));

            assertIterableEquals(Arrays.asList("hombre", "viejo"), filtered);
        }

        @Test
        public void filterOnlyOOVwords() {
            List<String> filtered = vocabulary.filterInVocab(Arrays.asList("papaya21", "carcaj2d"));

            assertIterableEquals(Arrays.asList(), filtered);
        }

        @Test
        public void vocabularyWhenContainsKey() {
            assertTrue(vocabulary.contains("hombre"));
        }

        @Test
        public void vocabularyWhenDoesnotContainsKey() {
            assertFalse(vocabulary.contains("papaya21"));
        }

        @Test
        public void getSingleWordInVocabulary() {
            Word word = vocabulary.get("hombre");

            assertNotEquals(null, word);
            assertTrue(word.getWord().equals("hombre"));
        }

        @Test
        public void getSingleWordOOV() {
            Word word = vocabulary.get("papaya21");

            assertEquals(null, word);
        }

        @Test
        public void getWordsInVocabulary() {
            List<Word> words = vocabulary.get(Arrays.asList("hombre", "mujer"));

            assertNotEquals(null, words);
            assertEquals(2, words.size());
            assertTrue(words.get(0).getWord().equals("hombre"));
            assertTrue(words.get(1).getWord().equals("mujer"));
        }

        @Test
        public void getWordsWithSomeOOV() {
            List<Word> words = vocabulary.get(Arrays.asList("hombre", "papaya21"));

            assertNotEquals(null, words);
            assertEquals(1, words.size());
            assertTrue(words.get(0).getWord().equals("hombre"));
        }

        @Test
        public void getWordsWithAllOOV() {
            List<Word> words = vocabulary.get(Arrays.asList("carcaj2d", "papaya21"));

            assertNotEquals(null, words);
            assertEquals(0, words.size());
        }
    }

    @Nested
    public class WordListTest {

        private Vocabulary vocabulary = null;

        @BeforeEach
        public void setup() {
            DataLoader data = new VecLoader(Paths.get("src/test/java/com/biasexplorer4j/data/testEmbeddings.vec"));
            this.vocabulary = new Vocabulary(data);
        }

        @Test
        public void generateWordListFromListOfStringsInVocabulary() {
            WordList<Word> wordList = vocabulary.getWordList("Test", "hombre", "mujer");

            assertNotEquals(null, wordList);
            assertEquals(2, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer"), wordList.getWordList());
            assertTrue(wordList.getTitle().equals("Test"));
        }

        @Test
        public void generateWordListWithFirstStringNull() {
            WordList<Word> wordList = vocabulary.getWordList("Test", null, "hombre", "mujer");

            assertNotEquals(null, wordList);
            assertEquals(2, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer"), wordList.getWordList());
            assertTrue(wordList.getTitle().equals("Test"));
        }

        @Test
        public void generateWordListFromListOfStringsWithSomeOOV() {
            WordList<Word> wordList = vocabulary.getWordList("Test", "hombre", "carcaj2d", "mujer", "papaya21");

            assertNotEquals(null, wordList);
            assertEquals(2, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer"), wordList.getWordList());
            assertTrue(wordList.getTitle().equals("Test"));
        }

        @Test
        public void generateWordListFromOOVWordList() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> vocabulary.getWordList("Test", "carcaj2d", "papaya21"),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list must contain at least one word"));
        }

        @Test
        public void generateWordListWithNullReferencedTitle() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> vocabulary.getWordList(null, "hombre"),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list's title can not be null"));
        }

        @Test
        public void generateWordListFromProjectedWordList() {
            ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", generateRandomEmbedding()),
                                                          new ProjectedWord("mujer", generateRandomEmbedding())
                                                        };

            WordList<ProjectedWord> wordList = vocabulary.getWordList("Test", words);

            assertNotEquals(null, wordList);
            assertEquals(2, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer"), wordList.getWordList());
            assertTrue(wordList.getTitle().equals("Test"));                                                      
        }

        @Test
        public void generateWordListFromProjectedWordListWithSomeOOVWords() {
            ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", generateRandomEmbedding()),
                                                          new ProjectedWord("mujer", generateRandomEmbedding()),
                                                          new ProjectedWord("papaya21", generateRandomEmbedding())
                                                        };

            WordList<ProjectedWord> wordList = vocabulary.getWordList("Test", words);

            assertNotEquals(null, wordList);
            assertEquals(2, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer"), wordList.getWordList());
            assertTrue(wordList.getTitle().equals("Test"));                                                      
        }

        @Test
        public void generateWordListFromProjectedWordListWithAllOOVWords() {
            ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("papaya21", generateRandomEmbedding()),
                                                          new ProjectedWord("carcaj2d", generateRandomEmbedding())
                                                        };

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> vocabulary.getWordList("Test", words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list must contain at least one word"));                                                         
        }

        @Test
        public void generateWordListFromNullReferencedProjectedWordList() {
            ProjectedWord[] words = null;

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> vocabulary.getWordList("Test", words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Projected words list can not be null"));                                                    
        }

        @Test
        public void generateWordListFromEmptyProjectedWordList() {
            ProjectedWord[] words = new ProjectedWord[0];

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> vocabulary.getWordList("Test", words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list must contain at least one word"));                                                    
        }

        @Test
        public void generateWordListFromProjectedWordListWithNullReferencedTitle() {
            ProjectedWord[] words = new ProjectedWord[] { new ProjectedWord("hombre", generateRandomEmbedding()),
                                                          new ProjectedWord("mujer", generateRandomEmbedding())
                                                        };

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> vocabulary.getWordList(null, words),
                                                            "Expected IllegalArgumentException but not thrown");

            assertTrue(thrown.getMessage().equals("Word list's title can not be null"));                                                    
        }

        @Test
        public void addInVocabularyWordToWordList() {
            String inVocabulary = "rey";
            WordList<Word> wordList = vocabulary.getWordList("Test", "hombre", "mujer", "reina");

            assertDoesNotThrow(() -> vocabulary.add(wordList, inVocabulary));

            assertEquals(4, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer", "reina", "rey"), wordList.getWordList());
        }

        @Test
        public void addOOVWordToWordList() {
            String inVocabulary = "papaya21";
            WordList<Word> wordList = vocabulary.getWordList("Test", "hombre", "mujer", "reina");

            assertDoesNotThrow(() -> vocabulary.add(wordList, inVocabulary));

            assertEquals(3, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer", "reina"), wordList.getWordList());
        }

        @Test
        public void addInVocabularyMultipleWordsToWordList() {
            List<String> inVocabulary = Arrays.asList("rey", "viejo");
            WordList<Word> wordList = vocabulary.getWordList("Test", "hombre", "mujer", "reina");

            assertDoesNotThrow(() -> vocabulary.add(wordList, inVocabulary));

            assertEquals(5, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer", "reina", "rey", "viejo"), wordList.getWordList());
        }

        @Test
        public void addSomeOOVMultipleWordsToWordList() {
            List<String> inVocabulary = Arrays.asList("papaya21", "rey", "viejo");
            WordList<Word> wordList = vocabulary.getWordList("Test", "hombre", "mujer", "reina");

            assertDoesNotThrow(() -> vocabulary.add(wordList, inVocabulary));

            assertEquals(5, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer", "reina", "rey", "viejo"), wordList.getWordList());
        }

        @Test
        public void addAllMultipleWordsToWordList() {
            List<String> inVocabulary = Arrays.asList("papaya21", "carcaj2d");
            WordList<Word> wordList = vocabulary.getWordList("Test", "hombre", "mujer", "reina");

            assertDoesNotThrow(() -> vocabulary.add(wordList, inVocabulary));

            assertEquals(3, wordList.size());
            assertIterableEquals(Arrays.asList("hombre", "mujer", "reina"), wordList.getWordList());
        }
    }   
}

package com.biasexplorer4j.WordExploration.Vocabulary;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UncheckedWordListTest {
    
    @Test
    public void successfulInstatiation() {
        String title = "Test";
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");

        assertDoesNotThrow(() -> new UncheckedWordList(title, words));
    }

    @Test
    public void instatiationWithNullReferencedTitle() {
        String title = null;
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new UncheckedWordList(title, words),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Word list's title can not be null")); 
    }

    @Test
    public void instatiationWithNullReferencedListOfWords() {
        String title = "Test";
        List<String> words = null;

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new UncheckedWordList(title, words),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Word list must contain at least one word")); 
    }

    @Test
    public void instatiationWithEmptyListOfWords() {
        String title = "Test";
        List<String> words = Arrays.asList();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new UncheckedWordList(title, words),
                                                            "Expected IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Word list must contain at least one word")); 
    }

    @Test
    public void addWordToList() {
        String title = "Test";
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");

        UncheckedWordList wordList = new UncheckedWordList(title, words);
        String word = "rey";
        
        assertTrue(wordList.add(word));
        assertEquals(5, wordList.size());
    }

    @Test
    public void addNullReferencedWordToList() {
        String title = "Test";
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");

        UncheckedWordList wordList = new UncheckedWordList(title, words);
        String word = null;
        
        assertFalse(wordList.add(word));
        assertEquals(4, wordList.size());
    }

    @Test
    public void addListOfWordsToWordList() {
        String title = "Test";
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");

        UncheckedWordList wordList = new UncheckedWordList(title, words);
        List<String> new_words = Arrays.asList("rey", "muchacha");
        
        assertTrue(wordList.add(new_words));
        assertEquals(6, wordList.size());
    }

    @Test
    public void addNullReferencedListOfWordsToWordList() {
        String title = "Test";
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");

        UncheckedWordList wordList = new UncheckedWordList(title, words);
        List<String> new_words = null;
        
        assertFalse(wordList.add(new_words));
        assertEquals(4, wordList.size());
    }

    @Test
    public void addEmptyListOfWordsToWordList() {
        String title = "Test";
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");

        UncheckedWordList wordList = new UncheckedWordList(title, words);
        List<String> new_words = Arrays.asList();
        
        assertFalse(wordList.add(new_words));
        assertEquals(4, wordList.size());
    }

    @Test
    public void WordListElementsAreUnmodifiable() {
        String title = "Test";
        List<String> words = Arrays.asList("hombre", "mujer", "viejo", "reina");
        UncheckedWordList wordList = new UncheckedWordList(title, words);
        
        List<String> referencedList = wordList.getWordList();
        referencedList.add("should not affect original list");
        
        assertEquals(4, wordList.size());
    }
}

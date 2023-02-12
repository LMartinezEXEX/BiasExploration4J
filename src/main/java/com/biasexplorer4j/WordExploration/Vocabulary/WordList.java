package com.biasexplorer4j.WordExploration.Vocabulary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class WordList implements Iterable<Word> {

    private String title;
    private Set<Word> wordList;

    protected WordList(List<? extends Word> words, String title) {
        if (Objects.isNull(words) || words.size() == 0) {
            throw new IllegalArgumentException("Word list must contain at least one word");
        } else if (Objects.isNull(title)) {
            throw new IllegalArgumentException("Word list's title can not be null");
        }

        this.title = title;
        this.wordList = new HashSet<>(words);
    }

    protected boolean add(List<? extends Word> words) {
        return wordList.addAll(words);
    }

    protected <T extends Word> boolean add(T word) {
        return wordList.add(word);
    }

    public List<Word> getWords() {
        return new ArrayList<>(wordList);
    }

    public List<String> getWordList() {
        return wordList.stream()
                       .map(Word::getWord)
                       .collect(Collectors.toList());
    }

    public String getTitle() {
        return title;
    }

    public int size() {
        return wordList.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((wordList == null) ? 0 : wordList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordList other = (WordList) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (wordList == null) {
            if (other.wordList != null)
                return false;
        } else if (!wordList.equals(other.wordList))
            return false;
        return true;
    }

    @Override
    public Iterator<Word> iterator() {
        return this.wordList.iterator();
    }
}

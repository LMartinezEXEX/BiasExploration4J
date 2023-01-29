package com.biasexplorer4j.WordExploration.Vocabulary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.biasexplorer4j.WordExploration.WordToPlot;

public class WordList<T extends WordToPlot> implements Iterable<T> {

    private String title;
    private List<T> wordList;

    protected WordList(List<T> words, String title) {
        if (Objects.isNull(words) || words.size() == 0) {
            throw new IllegalArgumentException("Word list must contain at least one word");
        } else if (Objects.isNull(title)) {
            throw new IllegalArgumentException("Word list's title can not be null");
        }

        this.title = title;
        this.wordList = words;
    }

    protected boolean add(List<T> words) {
        return wordList.addAll(words);
    }

    protected boolean add(T word) {
        return wordList.add(word);
    }

    public List<T> getWords() {
        return new ArrayList<>(wordList);
    }

    public List<String> getWordList() {
        return wordList.stream()
                       .map(WordToPlot::getToken)
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
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordList<T> other = (WordList<T>) obj;
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
    public Iterator<T> iterator() {
        return this.wordList.iterator();
    }
}

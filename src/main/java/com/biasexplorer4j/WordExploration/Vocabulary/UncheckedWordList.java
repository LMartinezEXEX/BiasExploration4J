package com.biasexplorer4j.WordExploration.Vocabulary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UncheckedWordList implements Iterable<String>{

    private String title;
    private Set<String> wordList;

    public UncheckedWordList(String title, List<String> wordList) {
        if (Objects.isNull(wordList) || wordList.size() == 0) {
            throw new IllegalArgumentException("Word list must contain at least one word");
        } else if (Objects.isNull(title)) {
            throw new IllegalArgumentException("Word list's title can not be null");
        }

        this.title = title;
        this.wordList = new HashSet<>(wordList);
    } 

    public boolean add(String word) {
        if (Objects.isNull(word)) {
            return false;
        }
        return wordList.add(word);
    }

    public boolean add(List<String> words) {
        if (Objects.isNull(words)){
            return false;
        }
        return wordList.addAll(words);
    }

    public List<String> getWordList() {
        return new ArrayList<>(wordList);
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
        UncheckedWordList other = (UncheckedWordList) obj;
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
    public Iterator<String> iterator() {
        return wordList.iterator();
    }
    
}

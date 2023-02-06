package com.biasexplorer4j.WordExploration.Vocabulary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.biasexplorer4j.DataLoader.DataLoader;

public class Vocabulary implements Iterable<EmbeddedWord>{

    private Map<String, EmbeddedWord> vocabulary;
    private int embeddingDimension;

    public Vocabulary(DataLoader data) {
        if (Objects.isNull(data)) {
            throw new IllegalArgumentException("Data loader can not be null");
        } else if (data.getEmbeddings().size() == 0) {
            throw new IllegalArgumentException("Data loader has no embedding mapping. May result from omitting a call to loadDataset()");
        }

        this.embeddingDimension = data.getEmbeddingDimension();

        populateVocabulary(data.getEmbeddings());
    }

    public Vocabulary(Map<String, double[]> embeddingsMap) {
        if (Objects.isNull(embeddingsMap)) {
            throw new IllegalArgumentException("Embeddings map can not be null");
        }else if (embeddingsMap.size() == 0) {
            throw new IllegalArgumentException("Embeddings map has no element");
        }

        for (double[] d : embeddingsMap.values()){
            this.embeddingDimension = d.length;
            break;
        }

        populateVocabulary(embeddingsMap);
    }

    private void populateVocabulary(Map<String, double[]> embeddingsMap) {
        this.vocabulary = new HashMap<>(embeddingsMap.size());
        for (Map.Entry<String, double[]> e : embeddingsMap.entrySet()) {
            String token = e.getKey();
            INDArray embedding = Nd4j.create(e.getValue(), new int[] {1, this.embeddingDimension});
            EmbeddedWord word = new EmbeddedWord(token, embedding);

            this.vocabulary.put(token, word);
        }
    }

    public WordList getWordList(String title, String word, String... words) {
        EmbeddedWord token_1 = this.get(word);
        List<EmbeddedWord> tokens = this.get(Arrays.asList(words));

        if (Objects.nonNull(token_1)){
            tokens.add(0, token_1);  
        } 
        return new WordList(tokens, title);
    }

    public WordList getWordList(String title, Word... words) {
        List<Word> listOfWords = null;
        if (Objects.nonNull(words)) {
           listOfWords = filterWordsInVocab(Arrays.asList(words));
        }
        return new WordList(listOfWords, title);
    }

    public boolean add(WordList wordList, List<String> words) {
        List<EmbeddedWord> wordsInVocab = this.get(words);
        return wordList.add(wordsInVocab);
    }

    public boolean add(WordList wordList, String word) {
        EmbeddedWord wordInVocab = this.get(word);
        if (Objects.isNull(wordInVocab)) {
            return false;
        }
        return wordList.add(wordInVocab);
    }

    private List<Word> filterWordsInVocab(List<Word> words) {
        return words.stream()
                    .filter(t -> this.contains(t.getWord()))
                    .collect(Collectors.toList());
    }

    public List<String> filterInVocab(List<String> tokens) {
        if (Objects.isNull(tokens)) {
            return null;
        }

        return tokens.stream()
                     .filter(t -> this.contains(t))
                     .collect(Collectors.toList());
    }

    public boolean contains(String token) {
        return vocabulary.containsKey(token);
    }

    public EmbeddedWord get(String token) {
        return this.vocabulary.get(token);
    }

    public List<EmbeddedWord> get(List<String> tokens) {
        List<String> inVocab = filterInVocab(tokens);
        return inVocab.stream()
                      .map(t -> vocabulary.get(t))
                      .collect(Collectors.toList());
    }

    public Stream<EmbeddedWord> stream() {
        return vocabulary.values().stream();
    }

    public int size() {
        return vocabulary.size();
    }

    public int getEmbeddingDimension() {
        return embeddingDimension;
    }

    @Override
    public Iterator<EmbeddedWord> iterator() {
        return this.vocabulary.values().iterator();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((vocabulary == null) ? 0 : vocabulary.hashCode());
        result = prime * result + embeddingDimension;
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
        Vocabulary other = (Vocabulary) obj;
        if (vocabulary == null) {
            if (other.vocabulary != null)
                return false;
        } else if (!vocabulary.equals(other.vocabulary))
            return false;
        if (embeddingDimension != other.embeddingDimension)
            return false;
        return true;
    }
}

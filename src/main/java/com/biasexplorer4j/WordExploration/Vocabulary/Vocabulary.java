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
import com.biasexplorer4j.WordExploration.WordToPlot;
import com.biasexplorer4j.WordExploration.BiasExploration.ProjectedWord;

public class Vocabulary implements Iterable<Word>{

    private Map<String, Word> vocabulary;
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
            Word word = new Word(token, embedding);

            this.vocabulary.put(token, word);
        }
    }

    public WordList<Word> getWordList(String title, String word, String... words) {
        Word token_1 = this.get(word);
        List<Word> tokens = this.get(Arrays.asList(words));

        if (Objects.nonNull(token_1)){
            tokens.add(0, token_1);  
        } 
        return new WordList<Word>(tokens, title);
    }

    public WordList<ProjectedWord> getWordList(String title, ProjectedWord... words) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Projected words list can not be null");
        }

        List<ProjectedWord> wordsInVocab = Arrays.stream(words)
                                                 .filter(w -> this.contains(w.getWord()))
                                                 .collect(Collectors.toList());
        return new WordList<ProjectedWord>(wordsInVocab, title);
    }

    @SuppressWarnings("unchecked")
    public <T extends WordToPlot> boolean add(WordList<T> wordList, List<String> words) {
        List<T> wordsInVocab = (List<T>) this.get(words);
        return wordList.add(wordsInVocab);
    }

    @SuppressWarnings("unchecked")
    public <T extends WordToPlot> boolean add(WordList<T> wordList, String word) {
        T wordInVocab = (T) this.get(word);
        if (Objects.isNull(wordInVocab)) {
            return false;
        }
        
        return wordList.add(wordInVocab);
    }

    public List<String> filterInVocab(List<String> tokens) {
        return tokens.stream()
                     .filter(t -> this.contains(t))
                     .collect(Collectors.toList());
    }

    public boolean contains(String token) {
        return vocabulary.containsKey(token);
    }

    public Word get(String token) {
        return this.vocabulary.get(token);
    }

    public List<Word> get(List<String> tokens) {
        List<String> inVocab = filterInVocab(tokens);
        return inVocab.stream()
                      .map(t -> vocabulary.get(t))
                      .collect(Collectors.toList());
    }

    public Stream<Word> stream() {
        return vocabulary.values().stream();
    }

    public int size() {
        return vocabulary.size();
    }

    public int getEmbeddingDimension() {
        return embeddingDimension;
    }

    @Override
    public Iterator<Word> iterator() {
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

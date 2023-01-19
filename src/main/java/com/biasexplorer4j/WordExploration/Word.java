package com.biasexplorer4j.WordExploration;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;

public class Word {

    private String word;
    private INDArray embedding;
    private INDArray pca;

    public Word(String word, INDArray embedding) {
        this.word = word;
        this.embedding = embedding;
    }

    public double getPca(int coord) {
        return this.getPca().getDouble(1, coord);
    }

    public INDArray normalizeEmbedding(NormalizerStandardize normalizer) {
        normalizer.transform(this.getEmbedding());
        return this.getEmbedding();
    }

    public String getWord() {
        return word;
    }

    public INDArray getEmbedding() {
        return embedding;
    }

    public void setPca(INDArray pca) {
        this.pca = pca;
    }

    public INDArray getPca() {
        return pca;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((word == null) ? 0 : word.hashCode());
        result = prime * result + ((embedding == null) ? 0 : embedding.hashCode());
        result = prime * result + ((pca == null) ? 0 : pca.hashCode());
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
        Word other = (Word) obj;
        if (word == null) {
            if (other.word != null)
                return false;
        } else if (!word.equals(other.word))
            return false;
        if (embedding == null) {
            if (other.embedding != null)
                return false;
        } else if (!embedding.equals(other.embedding))
            return false;
        if (pca == null) {
            if (other.pca != null)
                return false;
        } else if (!pca.equals(other.pca))
            return false;
        return true;
    }
}

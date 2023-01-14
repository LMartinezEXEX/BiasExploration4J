package com.wordexplorer4j.WordExploration;

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

    public Double[] getEmbeddingInDouble() {
        Double[] emb = new Double[300];
        for (int j = 0; j < 300; j++) {
            emb[j] = this.embedding.getDouble(j);
        }
        return emb;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setEmbedding(INDArray embedding) {
        this.embedding = embedding;
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
}

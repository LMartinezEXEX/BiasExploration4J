package com.biasexplorer4j.WordExploration.Vocabulary;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;

import com.biasexplorer4j.WordExploration.Word;

public class EmbeddedWord extends Word {

    private INDArray embedding;
    private INDArray pca;

    protected EmbeddedWord(String word, INDArray embedding) {
        super(word, null);
        this.embedding = embedding;
    }

    public INDArray normalizeEmbedding(NormalizerStandardize normalizer) {
        normalizer.transform(this.getEmbedding());
        return this.getEmbedding();
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

    public double getPca(int coord) {
        return this.getPca().getDouble(1, coord);
    }

    @Override
    public double[] getProjections() {
        return new double[] { getPca(0), getPca(1) };
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        EmbeddedWord other = (EmbeddedWord) obj;
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

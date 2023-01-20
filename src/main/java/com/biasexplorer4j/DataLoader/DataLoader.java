package com.biasexplorer4j.DataLoader;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DataLoader {
    private Map<String, double[]> embeddings = new HashMap<>();
    private int embeddingDimension;

    public abstract void loadDataset(Path path);

    protected void reset() {
        this.embeddings = new HashMap<>();
        this.embeddingDimension = 0;
    }

    protected Map<String, double[]> getModifiableEmbeddings() {
        return this.embeddings;
    }

    protected void setEmbeddingDimension(int embeddingDimension) {
        this.embeddingDimension = embeddingDimension;
    }

    public Map<String, double[]> getEmbeddings() {
        return Collections.unmodifiableMap(this.embeddings);
    }

    public int getEmbeddingDimension() {
        return this.embeddingDimension;
    }
}

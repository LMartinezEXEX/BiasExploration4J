package com.biasexplorer4j.DataLoader;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DataLoader {
    private Map<String, double[]> embeddings;
    private int embeddingDimension;

    public abstract void loadDataset(Path path);

    protected void init() {
        this.embeddings = new HashMap<>();
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

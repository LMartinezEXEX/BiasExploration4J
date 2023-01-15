package com.wordexplorer4j.DataLoader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class DataLoader {
    private Map<String, double[]> embeddings = new HashMap<>();

    public abstract void loadDataset(Path path);

    public Map<String, double[]> getEmbeddings() {
        return this.embeddings;
    }

    public void setEmbeddings(HashMap<String, double[]> embeddings) {
        this.embeddings = embeddings;
    }

    public int getEmbeddingDimension() {
        for (double[] d : embeddings.values()) {
            return d.length;
        }

        return 0;
    }
}

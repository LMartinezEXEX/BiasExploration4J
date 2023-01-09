package com.wordexplorer4j.DataLoader;

import java.nio.file.Path;
import java.util.HashMap;

public abstract class DataLoader {
    private HashMap<String, double[]> embeddings = new HashMap<>();

    public abstract void loadDataset(Path path);

    public HashMap<String, double[]> getEmbeddings() {
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

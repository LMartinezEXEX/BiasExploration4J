package com.biasexplorer4j.DataLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.IntStream;

public class VecLoader extends DataLoader{
    private int embeddingDim;

    @Override
    public void loadDataset(Path path) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("Path to .vec extended file can not be null");
        }
        if (!path.toString().toLowerCase().endsWith(".vec")) {
            throw new IllegalArgumentException("Only .vec extended files accepted");
        }

        super.embeddings = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            this.setEmbeddingDim(Integer.parseInt(line.split(" ")[1]));

            while ((line = reader.readLine()) != null) {
                process_line(line);
            }

        } catch (IOException ioe) {
            throw new IllegalArgumentException("File { " + path.toAbsolutePath() +" } not found");
        }
    }

    private void process_line(String readLine) {
        String[] components = readLine.split(" ");

        if (getEmbeddingDim() == 0)
            setEmbeddingDim(components.length - 1);
        else {
            if (components.length - 1 != getEmbeddingDim()) 
                throw new IllegalArgumentException("Diferent embeddings sizes encountered!");
        }

        String word = components[0];
        double[] embedding = parseToEmbedding(components);
        super.getEmbeddings().put(word, embedding);
    }

    private double[] parseToEmbedding(String[] coords) {
        return IntStream.range(1, coords.length)
                        .mapToDouble(i -> Double.parseDouble(coords[i]))
                        .toArray();
    }

    private void setEmbeddingDim(int embeddingDim) {
        this.embeddingDim = embeddingDim;
    }

    public int getEmbeddingDim() {
        return embeddingDim;
    }
    
}

package com.biasexplorer4j.DataLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.IntStream;

public class VecLoader extends DataLoader{

    public VecLoader() {}

    public VecLoader(Path path) {
        loadDataset(path);
    }

    @Override
    public void loadDataset(Path path) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("Path to .vec extended file can not be null");
        }
        if (!path.toString().toLowerCase().endsWith(".vec")) {
            throw new IllegalArgumentException("Only .vec extended files accepted");
        }

        super.reset();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            super.setEmbeddingDimension(Integer.parseInt(line.split(" ")[1]));

            while ((line = reader.readLine()) != null) {
                process_line(line);
            }

        } catch (IOException ioe) {
            throw new IllegalArgumentException("File { " + path.toAbsolutePath() +" } not found");
        }
    }

    private void process_line(String readLine) {
        String[] components = readLine.split(" ");

        if (components.length - 1 != super.getEmbeddingDimension()) 
            throw new IllegalArgumentException("Diferent embeddings sizes encountered!");

        String word = components[0];
        double[] embedding = parseToEmbedding(components);
        super.getModifiableEmbeddings().put(word, embedding);
    }

    private double[] parseToEmbedding(String[] coords) {
        return IntStream.range(1, coords.length)
                        .mapToDouble(i -> Double.parseDouble(coords[i]))
                        .toArray();
    }
}

package com.wordexplorer4j.DataLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

public class VecLoader extends DataLoader{
    private int embeddingDim;

    @Override
    public void loadDataset(Path path) {

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            this.setEmbeddingDim(Integer.parseInt(line.split(" ")[1]));

            while ((line = reader.readLine()) != null) {
                process_line(line);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
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
        //float[] embedding = new float[getEmbeddingDim()];
        //for (int i = 1; i < coords.length; i++) {
        //    embedding[i-1] = Float.parseFloat(coords[i]);
        //}

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

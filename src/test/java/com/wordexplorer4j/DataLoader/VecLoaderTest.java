package com.wordexplorer4j.DataLoader;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VecLoaderTest {

    private VecLoader loader = null;

    @BeforeEach
    public void setup() {
        loader = new VecLoader();
    }
    
    @Test
    public void successfulVecFileLoading() {
        Path path = Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.vec");
        loader.loadDataset(path);
        
        assertEquals(300, loader.getEmbeddingDim());
        assertEquals(5, loader.getEmbeddings().size());
    }

    @Test
    public void shouldFailWithNullReferencedFile() {
        Path path = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                        () -> loader.loadDataset(path),
                                                        "Expectedt IllegalArgumentException but not thrown");
                          
        assertTrue(thrown.getMessage().equals("Path to .vec extended file can not be null"));
    }

    @Test
    public void shouldFailWithNonVecFile() {
        Path path = Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.csv");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                        () -> loader.loadDataset(path),
                                                        "Expectedt IllegalArgumentException but not thrown");
                          
        assertTrue(thrown.getMessage().equals("Only .vec extended files accepted"));
    }

    @Test
    public void shouldFailWithNonExistentFile() {
        Path path = Paths.get("dummyPath.vec");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                        () -> loader.loadDataset(path),
                                                        "Expectedt IllegalArgumentException but not thrown");
                          
        assertTrue(thrown.getMessage().equals("File { " + path.toAbsolutePath() +" } not found"));
    }

    @Test
    public void shouldFailWithBadEmbeddingFileFormat() {
        Path path = Paths.get("src/test/java/com/wordexplorer4j/data/testBadEmbeddings.vec");
        
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                        () -> loader.loadDataset(path),
                                                        "Expectedt IllegalArgumentException but not thrown");
        assertTrue(thrown.getMessage().equals("Diferent embeddings sizes encountered!"));
    }

    @Test
    public void loadAllWordsAndNothingMore() {
        Path path = Paths.get("src/test/java/com/wordexplorer4j/data/testEmbeddings.vec");
        loader.loadDataset(path);
        Set<String> tokens = loader.getEmbeddings().keySet();

        assertTrue(tokens.containsAll(Arrays.asList("hombre", "rey", "reina", "mujer", "viejo")));

        assertFalse(tokens.containsAll(Arrays.asList("lagarto", "chico")));
    }

    @Test
    public void checkEmbeddingsLoading() {
        Path path = Paths.get("src/test/java/com/wordexplorer4j/data/testShorterEmbeddings.vec");
        loader.loadDataset(path);
        String[] mujer_raw_emb = "-0.0600897372 -0.1596184522 -0.0225087684 -0.1410036683 0.0355858058".split(" ");
        double[] mujer_emb = Arrays.stream(mujer_raw_emb).mapToDouble(i -> Double.parseDouble(i)).toArray();

        assertArrayEquals(mujer_emb, loader.getEmbeddings().get("mujer"));
    }
}

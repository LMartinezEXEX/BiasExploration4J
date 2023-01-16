package com.wordexplorer4j.PhraseBiasExploration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(4)
public class PseudoLikelihoodTest {
    
    @Test
    public void intitializeWithNullreferencedList() {
        List<Double> scores = null;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new PseudoLikelihood(scores),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Scores list can not be null"));  
    }

    @Test
    public void calculatePLLScoreWithOnlyOneValue() {
        List<Double> scores = Arrays.asList(2.0342);
        List<Double> PLLScores = new PseudoLikelihood(scores).getPLLScores();

        assertEquals(1, PLLScores.size());
        assertEquals(1.0, PLLScores.get(0));
    }

    @Test
    public void calculatePLLScoreWithMultipleDifferentValues() {
        List<Double> scores = Arrays.asList(2.0, 1.0, -1.0, -0.5);
        List<Double> PLLScores = new PseudoLikelihood(scores).getPLLScores();

        assertEquals(4, PLLScores.size());
        assertEquals(-0.5, PLLScores.get(0));
        assertEquals(-1.0, PLLScores.get(1));
        assertEquals(1.0, PLLScores.get(2));
        assertEquals(2.0, PLLScores.get(3));
    }

    @Test
    public void calculatePLLScoreWithMultipleEqualValues() {
        List<Double> scores = Arrays.asList(2.0, 2.0, 2.0, 2.0);
        List<Double> PLLScores = new PseudoLikelihood(scores).getPLLScores();

        assertEquals(4, PLLScores.size());
        assertEquals(1.0, PLLScores.get(0));
        assertEquals(1.0, PLLScores.get(1));
        assertEquals(1.0, PLLScores.get(2));
        assertEquals(1.0, PLLScores.get(3));
    }
}

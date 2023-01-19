package com.biasexplorer4j.WordExploration.Visualization.Plots;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BarPlotTest {
    
    @Test
    public void successfulInstantiation() {
        String[] words = new String[] {"mujer", "hombre"};
        double[] projections = new double[] {0.23, -0.43};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
        assertDoesNotThrow(() -> new BarPlot(words, projections, title, xAxisLabel, yAxisLabel));
    }

    @Test
    public void instantiateWithEmptyLists() {
        String[] words = new String[] {};
        double[] projections = new double[] {};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        assertDoesNotThrow(() -> new BarPlot(words, projections, title, xAxisLabel, yAxisLabel));
    }

    @Test
    public void instantiateWithNullReferencedWordList() {
        String[] words = null;
        double[] projections = new double[] {0.23, -0.43};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BarPlot(words, projections, title, xAxisLabel, yAxisLabel),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words to plot list can not be null"));
    }

    @Test
    public void instantiateWithNullReferencedProjectionList() {
        String[] words = new String[] {"mujer", "hombre"};
        double[] projections = null;
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BarPlot(words, projections, title, xAxisLabel, yAxisLabel),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words projection list can not be null"));
    }

    @Test
    public void instantiateWithIrregularWordAndProjectionsListSize() {
        String[] words = new String[] {"mujer", "hombre"};
        double[] projections = new double[] {0.23, -0.43, 2.42, 32.53};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new BarPlot(words, projections, title, xAxisLabel, yAxisLabel),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words to plot list size (" + 
                                                words.length + 
                                                ") and projections list size (" +
                                                projections.length +
                                                ") must be equal"));
    }

    @Test
    public void instantiateWithNullReferencedTitleAndAxisLabels() {
        String[] words = new String[] {"mujer", "hombre"};
        double[] projections = new double[] {0.23, -0.43};
        String title = null;
        String xAxisLabel = null;
        String yAxisLabel = null;

        assertDoesNotThrow(() -> new BarPlot(words, projections, title, xAxisLabel, yAxisLabel));
    }

    @Test
    public void plotShouldBeNotVisibleAfterInstantiation() {
        String[] words = new String[] {"mujer", "hombre"};
        double[] projections = new double[] {0.23, -0.43};
        String title = null;
        String xAxisLabel = null;
        String yAxisLabel = null;
        BarPlot plot = new BarPlot(words, projections, title, xAxisLabel, yAxisLabel);
        
        assertFalse(plot.isVisible());
    }
}

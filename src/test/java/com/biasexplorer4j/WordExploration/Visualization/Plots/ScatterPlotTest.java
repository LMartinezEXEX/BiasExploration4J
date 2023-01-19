package com.biasexplorer4j.WordExploration.Visualization.Plots;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jfree.chart.axis.ValueAxis;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ScatterPlotTest {

    @AfterEach
    public void clean() {
        PlotManager.getInstance().cleanUp();
    }

    @Test
    public void successfulInstantiation() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        assertDoesNotThrow(() -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false));
    }

    @Test
    public void instantiateWithEmptyLists() {
        String[] words = new String[] {};
        double[][] projections = new double[][] { new double[] {}, new double[] {} };
        double[] xAxisLimits = new double[] {};
        double[] yAxisLimits = new double[] {};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        assertDoesNotThrow(() -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false));
    }

    @Test
    public void instantiateWithNullReferencedWordList() {
        String[] words = null;
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words to plot list can not be null"));
    }

    @Test
    public void instantiateWithNullReferencedProjectionList() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = null;
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words projection list can not be null"));
    }

    @Test
    public void instantiateWithProjectionListMissing() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Projections list must contain two elements," + 
                                                "one array for X poryections and another for Y projections"));
    }

    @Test
    public void instantiateWithIrregularWordAndProjectionsListSize() {
        String[] words = new String[] {"mujer", "hombre", "casa", "zanahoria"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words to plot list size (" + 
                                                words.length + 
                                                ") and X projections list size (" +
                                                projections[0].length +
                                                ") and Y projection list size (" +
                                                projections[1].length +
                                                ") must be equal"));
    }

    @Test
    public void instantiateWithIrregularXProjectionAndYProjectionListSize() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03, 0.45, 0.13} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words to plot list size (" + 
                                                words.length + 
                                                ") and X projections list size (" +
                                                projections[0].length +
                                                ") and Y projection list size (" +
                                                projections[1].length +
                                                ") must be equal"));
    }

    @Test
    public void instantiateWithNullReferencedTitleAndAxisLabels() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = null;
        String xAxisLabel = null;
        String yAxisLabel = null;

        assertDoesNotThrow(() -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false));
    }

    @Test
    public void instantiationWithAxisWhereUpperBoundIsSmallerThanLowerBound() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};

        double[] yAxisLimits = new double[] {2.0, -2.0};

        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Range(double, double): require lower (" +
                                                yAxisLimits[0] +
                                                ") <= upper (" +
                                                yAxisLimits[1] +
                                                ")."));
    }

    @Test
    public void useAxisWhenGiven() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";

        ScatterPlot plot = new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, false, false);
        ValueAxis xAxis = plot.getChart().getXYPlot().getDomainAxis();
        ValueAxis yAxis = plot.getChart().getXYPlot().getRangeAxis();

        assertEquals(xAxisLimits[0], xAxis.getLowerBound());
        assertEquals(xAxisLimits[1], xAxis.getUpperBound());
        assertEquals(yAxisLimits[0], yAxis.getLowerBound());
        assertEquals(yAxisLimits[1], yAxis.getUpperBound());
    }

    @Test
    public void labelXYPointsWhenNeeded() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
        boolean labelXYPoints = true;

        assertDoesNotThrow(() -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, labelXYPoints, false));
    }

    @Test
    public void drawOriginAxisWhenNeeded() {
        String[] words = new String[] {"mujer", "hombre"};
        double[][] projections = new double[][] { new double[] {0.23, -0.43}, new double[] {-0.64, -0.03} };
        double[] xAxisLimits = new double[] {2.0, 4.0};
        double[] yAxisLimits = new double[] {-2.0, 2.0};
        String title = "Test";
        String xAxisLabel = "X label";
        String yAxisLabel = "Y label";
        boolean labelXYPoints = true;
        boolean drawOriginAxis = true;

        assertDoesNotThrow(() -> new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, labelXYPoints, drawOriginAxis));
    }
}

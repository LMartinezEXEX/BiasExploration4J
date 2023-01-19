package com.biasexplorer4j.WordExploration.Visualization.Plots;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JFrame;

public class PlotManager {
    
    private static PlotManager instance;
    private List<JFrame> plots = new ArrayList<>();

    private PlotManager() {}

    public static PlotManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new PlotManager();
        }

        return instance;
    }

    public boolean plot(PLOT_TYPE type, Map<String, ?> arguments) {
        switch (type) {
            case BAR:
                JFrame barFrame = barPlot(arguments);
                return plots.add(barFrame);
            
            case SCATTER:
                JFrame scatterFrame = scatterPlot(arguments);
                return plots.add(scatterFrame);

            default:
                return false;
        }
    }

    private JFrame barPlot(Map<String, ?> arguments) {
        String[] words       = getOrDefault(arguments, "words", null, String[].class);
        double[] projections = getOrDefault(arguments, "projections", null, double[].class);
        String title         = getOrDefault(arguments, "title", "", String.class);
        String xAxisLabel    = getOrDefault(arguments, "xAxisLabel", "", String.class);
        String yAxisLabel    = getOrDefault(arguments, "yAxisLabel", "", String.class);

        BarPlot plot = new BarPlot(words, projections, title, xAxisLabel, yAxisLabel);
        plot.setVisible(true);
        return plot;
    }

    private JFrame scatterPlot(Map<String, ?> arguments) {
        String[] words         = getOrDefault(arguments, "words", null, String[].class);
        double[][] projections = getOrDefault(arguments, "projections", null, double[][].class);
        double[] xAxisLimits   = getOrDefault(arguments, "xAxisLimits", null, double[].class);
        double[] yAxisLimits   = getOrDefault(arguments, "yAxisLimits", null, double[].class);
        String title           = getOrDefault(arguments, "title", "", String.class);
        String xAxisLabel      = getOrDefault(arguments, "xAxisLabel", "", String.class);
        String yAxisLabel      = getOrDefault(arguments, "yAxisLabel", "", String.class);
        boolean labelXYPoints  = getOrDefault(arguments, "labelPoints", false, Boolean.class);
        boolean drawOriginAxis = getOrDefault(arguments, "drawOriginAxis", false, Boolean.class);

        ScatterPlot plot = new ScatterPlot(words, projections, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, labelXYPoints, drawOriginAxis);
        plot.setVisible(true);
        return plot;
    }

    public void cleanUp() {
        for (JFrame frame : plots) {
            frame.dispose();
        }
    }

    private <T> T getOrDefault(Map<String, ?> map, String key, T defaultValue, Class<T> expectedClass) {
        if (Objects.isNull(map)) {
            throw new IllegalArgumentException("Arguments map can not be null");
        }
        
        Object value = map.get(key);
        return (expectedClass.isInstance(value)) ? expectedClass.cast(value) : defaultValue;
    }

}

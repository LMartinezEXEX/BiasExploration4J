package com.biasexplorer4j.WordExploration.Visualization.Plots;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JFrame;

import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

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

    public boolean plot(PLOT_TYPE type, Map<String, ?> arguments, List<WordList> wordLists) {
        switch (type) {
            case BAR:
                JFrame barFrame = barPlot(arguments, wordLists);
                return plots.add(barFrame);
            
            case SCATTER:
                JFrame scatterFrame = scatterPlot(arguments, wordLists);
                return plots.add(scatterFrame);

            default:
                return false;
        }
    }

    private JFrame barPlot(Map<String, ?> arguments, List<WordList> wordLists) {
        if (Objects.isNull(wordLists) || wordLists.size() == 0) {
            throw new IllegalArgumentException("Must provide one word list to plot");
        }

        String title         = getOrDefault(arguments, "title", "", String.class);
        String xAxisLabel    = getOrDefault(arguments, "xAxisLabel", "", String.class);
        String yAxisLabel    = getOrDefault(arguments, "yAxisLabel", "", String.class);

        BarPlot plot = new BarPlot(wordLists.get(0), title, xAxisLabel, yAxisLabel);
        plot.setVisible(true);
        return plot;
    }

    private JFrame scatterPlot(Map<String, ?> arguments, List<WordList> wordLists) {
        if (Objects.isNull(wordLists) || wordLists.size() == 0) {
            throw new IllegalArgumentException("Must provide at least one word list to plot");
        }

        double[] xAxisLimits   = getOrDefault(arguments, "xAxisLimits", null, double[].class);
        double[] yAxisLimits   = getOrDefault(arguments, "yAxisLimits", null, double[].class);
        String title           = getOrDefault(arguments, "title", "", String.class);
        String xAxisLabel      = getOrDefault(arguments, "xAxisLabel", "", String.class);
        String yAxisLabel      = getOrDefault(arguments, "yAxisLabel", "", String.class);
        boolean labelXYPoints  = getOrDefault(arguments, "labelPoints", false, Boolean.class);
        boolean drawOriginAxis = getOrDefault(arguments, "drawOriginAxis", false, Boolean.class);

        ScatterPlot plot = new ScatterPlot(wordLists, title, xAxisLimits, yAxisLimits, xAxisLabel, yAxisLabel, labelXYPoints, drawOriginAxis);
        plot.setVisible(true);
        return plot;
    }

    public void cleanUp() {
        for (JFrame frame : plots) {
            frame.dispose();
        }
        plots = new ArrayList<>();
    }

    private <T> T getOrDefault(Map<String, ?> map, String key, T defaultValue, Class<T> expectedClass) {
        if (Objects.isNull(map)) {
            throw new IllegalArgumentException("Arguments map can not be null");
        }
        
        Object value = map.get(key);
        return (expectedClass.isInstance(value)) ? expectedClass.cast(value) : defaultValue;
    }

}

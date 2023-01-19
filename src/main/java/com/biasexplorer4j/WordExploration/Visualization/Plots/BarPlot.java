package com.biasexplorer4j.WordExploration.Visualization.Plots;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.util.Objects;

public class BarPlot extends JFrame {

    private String[] words;
    private double[] projections;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private JFreeChart chart;
    
    protected BarPlot(String[] words, double[] projections, String title, String xAxisLabel, String yAxisLabel) {
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Words to plot list can not be null");
        }
        if (Objects.isNull(projections)) {
            throw new IllegalArgumentException("Words projection list can not be null");
        }

        this.words = words.clone();
        this.projections = projections.clone();
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        initFrame();
    }

    private void initFrame() {
        DefaultCategoryDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createBarChart(title, 
                                                    xAxisLabel, 
                                                    yAxisLabel, 
                                                    dataset, 
                                                    PlotOrientation.HORIZONTAL,
                                                    false,
                                                    false,
                                                    false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        add(chartPanel);
        pack();
        setLocationRelativeTo(null);

        this.chart = chart;
    }

    private DefaultCategoryDataset createDataset() {
        if (words.length != projections.length) {
            throw new IllegalArgumentException("Words to plot list size (" + 
                                                words.length + 
                                                ") and projections list size (" +
                                                projections.length +
                                                ") must be equal");
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < words.length; ++i) {
            double projection = projections[i];
            String colorDefiningKey = (projection < 0.0) ? "Neg" : "Pos";
            dataset.addValue(-projection, colorDefiningKey, words[i]);
        }
        return dataset;
    }

    public JFreeChart getChart() {
        try {
            return (JFreeChart) chart.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
    }
}

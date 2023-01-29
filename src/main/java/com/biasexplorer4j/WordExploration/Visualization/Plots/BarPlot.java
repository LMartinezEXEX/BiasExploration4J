package com.biasexplorer4j.WordExploration.Visualization.Plots;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.biasexplorer4j.WordExploration.WordToPlot;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

import java.awt.Color;
import java.util.Objects;

public class BarPlot<T extends WordToPlot>  extends JFrame {

    private WordList<T> wordList;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private JFreeChart chart;
    
    protected BarPlot(WordList<T> wordList, String title, String xAxisLabel, String yAxisLabel) {
        if (Objects.isNull(wordList)) {
            throw new IllegalArgumentException("Word list to plot can not be null");
        }

        this.wordList = wordList;
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
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.chart = chart;
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (T projectedWord : this.wordList) {
            double[] projections = projectedWord.getProjectionToPlot();
            String word = projectedWord.getToken();
            if (Objects.isNull(projections)) {
                throw new IllegalArgumentException("Projection for word: { " + word + " } is null");
            } else if (projections.length == 0) {
                throw new IllegalArgumentException("For bar plot all words must have at least 1 " +
                                                        "projection value. Word { " + 
                                                        word + 
                                                        " } has: 0");
            }
            String colorDefiningKey = (projections[0] < 0.0) ? "Neg" : "Pos";
            dataset.addValue(-projections[0], colorDefiningKey, word);
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

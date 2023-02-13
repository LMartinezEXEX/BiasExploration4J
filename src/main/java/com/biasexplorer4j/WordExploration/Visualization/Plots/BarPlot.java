package com.biasexplorer4j.WordExploration.Visualization.Plots;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.biasexplorer4j.WordExploration.Vocabulary.Word;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class BarPlot extends JFrame {

    private WordList wordList;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private static final double SPACE_TO_BORDER = 0.01;
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");
    private JFreeChart chart;
    
    protected BarPlot(WordList wordList, String title, String xAxisLabel, String yAxisLabel) {
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

        JFreeChart chart = getBarPlotChart(dataset);

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

        wordList.forEach(BarPlot::checkProjections);

        Word[] sortedWords = wordList.getWords().toArray(new Word[0]);
        Arrays.sort(sortedWords, new Comparator<Word>() {
            public int compare(Word w1, Word w2) {
                return Double.compare(w1.getProjections()[0], w2.getProjections()[0]);
            }
        });

        for (Word projectedWord : sortedWords) {
            double projection = projectedWord.getProjections()[0];
            String word = projectedWord.getWord();

            String colorDefiningKey = (projection < 0.0) ? "Neg" : "Pos";
            dataset.addValue(projection, colorDefiningKey, word);
        }
        return dataset;
    }

    private JFreeChart getBarPlotChart(CategoryDataset dataset) {
        CategoryAxis categoryAxis = new CategoryAxis(xAxisLabel);

        ValueAxis valueAxis = new NumberAxis(yAxisLabel);
        double absoluteMax = getAbsoluteMaxValue();
        valueAxis.setRange(-absoluteMax - SPACE_TO_BORDER, absoluteMax + SPACE_TO_BORDER);
        
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition position1 = new ItemLabelPosition(
                                        ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT);
        renderer.setDefaultPositiveItemLabelPosition(position1);
        ItemLabelPosition position2 = new ItemLabelPosition(
                                        ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT);
        renderer.setDefaultNegativeItemLabelPosition(position2);

        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        currentTheme.apply(chart);
        return chart;
    }

    private double getAbsoluteMaxValue() {
        return wordList.getWords().stream().mapToDouble(w -> w.getProjections()[0]).max().getAsDouble();
    }

    private static void checkProjections(Word word) {
        double[] projections = word.getProjections();
        String wordString = word.getWord();
        if (Objects.isNull(projections)) {
            throw new IllegalArgumentException("Projection for word: { " + wordString + " } is null");
        } else if (projections.length == 0) {
            throw new IllegalArgumentException("For bar plot all words must have at least 1 " +
                                                    "projection value. Word { " + 
                                                    wordString + 
                                                    " } has: 0");
        }
    }

    public JFreeChart getChart() {
        try {
            return (JFreeChart) chart.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
    }
}

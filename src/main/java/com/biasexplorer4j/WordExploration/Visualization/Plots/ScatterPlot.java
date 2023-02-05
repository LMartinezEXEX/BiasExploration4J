package com.biasexplorer4j.WordExploration.Visualization.Plots;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.biasexplorer4j.WordExploration.Word;
import com.biasexplorer4j.WordExploration.Vocabulary.WordList;

import java.awt.Color;
import java.awt.BasicStroke;
import java.util.List;
import java.util.Objects;

public class ScatterPlot extends JFrame {

    private List<WordList> wordLists;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private double[] xAxisLimits;
    private double[] yAxisLimits;
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");
    private JFreeChart chart;
    
    protected ScatterPlot(List<WordList> wordLists,String title, double[] xAxisLimits, double[] yAxisLimits, 
                            String xAxisLabel, String yAxisLabel, 
                            boolean labelXYPoints, boolean drawOriginAxis) {
        if (Objects.isNull(wordLists)) {
            throw new IllegalArgumentException("List of WordLists to plot can not be null");
        } else if (wordLists.stream().anyMatch(e -> Objects.isNull(e))) {
            throw new IllegalArgumentException("WordList can not be null");
        }
        this.wordLists = wordLists;
        this.xAxisLimits = xAxisLimits;
        this.yAxisLimits = yAxisLimits;
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        initFrame(labelXYPoints, drawOriginAxis);
    }

    private void initFrame(boolean labelXYPoints, boolean drawOriginAxis) {
        XYSeriesCollection dataset = createDataset();

        JFreeChart chart = getScatterChart(dataset);
                                             
        if (labelXYPoints) {
            putLabelsToXYPoints(chart.getXYPlot());
        }

        if (drawOriginAxis) {
            drawXOriginLines(chart.getXYPlot(), dataset);
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        add(chartPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.chart = chart;
    }

    private XYSeriesCollection createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (WordList wl : wordLists) {
            XYSeries series = new XYSeries(wl.getTitle());

            List<Word> words = wl.getWords();
            for (int i = 0; i < words.size(); ++i) {
                double[] projections = words.get(i).getProjections();
                String word = words.get(i).getWord();
                if (Objects.isNull(projections)) {
                    throw new IllegalArgumentException("Projection for word: { " + word + " } is null");
                }
                if (projections.length < 2) {
                    throw new IllegalArgumentException("For scatter plot all words must have at least 2 " +
                                                            "projection values. Word { " + 
                                                            word + 
                                                            " } has only: " + 
                                                            projections.length);
                }

                series.add(projections[0], projections[1]);
            }
            dataset.addSeries(series);
        }

        return dataset;
    }

    private JFreeChart getScatterChart(XYDataset dataset) {
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        if (Objects.nonNull(xAxisLimits) && xAxisLimits.length >= 2) {
            xAxis.setRange(xAxisLimits[0], xAxisLimits[1]);
        }
        if (Objects.nonNull(yAxisLimits) && yAxisLimits.length >= 2) {
            yAxis.setRange(yAxisLimits[0], yAxisLimits[1]);
        }

        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        currentTheme.apply(chart);
        return chart;
    }

    private void putLabelsToXYPoints(XYPlot plot) {
        for (WordList wl : wordLists) {
            List<Word> words = wl.getWords();
            for (int i = 0; i < words.size(); ++i) {
                String token = words.get(i).getWord();
                double[] projections = words.get(i).getProjections();
                XYTextAnnotation label = new XYTextAnnotation(token, projections[0], projections[1] + 0.02);
                plot.addAnnotation(label);
            }
        }   
    }

    private void drawXOriginLines(XYPlot plot, XYSeriesCollection dataset) {
        double minX = (Objects.isNull(xAxisLimits)) ? dataset.getDomainLowerBound(true) : xAxisLimits[0];
        double maxX = (Objects.isNull(xAxisLimits)) ? dataset.getDomainUpperBound(true) : xAxisLimits[1];
        double minY = (Objects.isNull(yAxisLimits)) ? dataset.getRangeLowerBound(true) : yAxisLimits[0];
        double maxY = (Objects.isNull(xAxisLimits)) ? dataset.getRangeUpperBound(true) : yAxisLimits[1];
        
        XYLineAnnotation XLine = new XYLineAnnotation(minX, 0, maxX, 0, new BasicStroke(1.0f), Color.BLACK);
        plot.addAnnotation(XLine);

        XYLineAnnotation YLine = new XYLineAnnotation(0, minY, 0, maxY, new BasicStroke(1.0f), Color.BLACK);
        plot.addAnnotation(YLine);
    }
    
    public JFreeChart getChart() {
        try {
            return (JFreeChart) chart.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
    }
}

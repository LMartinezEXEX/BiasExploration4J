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

import java.awt.Color;
import java.awt.BasicStroke;
import java.util.Objects;

public class ScatterPlot extends JFrame {

    private String[] words;
    private double[][] projections;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private double[] xAxisLimits;
    private double[] yAxisLimits;
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");
    private JFreeChart chart;
    
    protected ScatterPlot(String[] words, double[][] projections, String title, 
                            double[] xAxisLimits, double[] yAxisLimits, 
                            String xAxisLabel, String yAxisLabel, 
                            boolean labelXYPoints, boolean drawOriginAxis) {                       
        if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Words to plot list can not be null");
        }
        if (Objects.isNull(projections)) {
            throw new IllegalArgumentException("Words projection list can not be null");
        }

        this.words = words.clone();
        this.projections = projections.clone();
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

        this.chart = chart;
    }

    private XYSeriesCollection createDataset() {
        if (projections.length != 2) {
            throw new IllegalArgumentException("Projections list must contain two elements," + 
                                                "one array for X poryections and another for Y projections");
        }  else if (words.length != projections[0].length || projections[0].length != projections[1].length) {
            throw new IllegalArgumentException("Words to plot list size (" + 
                                                words.length + 
                                                ") and X projections list size (" +
                                                projections[0].length +
                                                ") and Y projection list size (" +
                                                projections[1].length +
                                                ") must be equal");
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries series = new XYSeries("");
        for (int i = 0; i < words.length; ++i) {
            series.add(projections[0][i], projections[1][i]);
        }
        
        dataset.addSeries(series);

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
        for (int i = 0; i < words.length; ++i) {
            XYTextAnnotation label = new XYTextAnnotation(words[i], projections[0][i], projections[1][i] + 0.02);
            plot.addAnnotation(label);
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

package com.biasexplorer4j.WordExploration.Visualization.Plots;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class PlotManagerTest {

    @AfterEach
    public void clean() {
        PlotManager.getInstance().cleanUp();
    }
    
    @Test
    public void successfulInstantiation() {
        assertDoesNotThrow(() -> PlotManager.getInstance());
    }

    @Test
    public void generateBarPlotSuccessfully() {
        String[] words = new String[] {"hombre", "mujer"};
        double[] projections = new double[] {-0.42, 0.54};

        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("words", words);
        arguments.put("projections", projections);

        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments));
    }

    @Test
    public void failBarPlotWhenWordListArgumentIsNotAStringArray() {
        float words = 45.2f;
        double[] projections = new double[] {-0.42, 0.54};

        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("words", words);
        arguments.put("projections", projections);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words to plot list can not be null"));
    }

    @Test
    public void failBarPlotWhenProjectionArrayNotPassed() {
        String[] words = new String[] {"hombre", "mujer"};

        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("words", words);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words projection list can not be null"));
    }

    @Test
    public void failScatterPlotWhenWordListIsNotAStringArray() {
        float words = 45.2f;
        double[][] projections = new double[][] { new double[] {-0.42, 0.54}, new double[] {-0.12, 0.22} };

        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("words", words);
        arguments.put("projections", projections);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words to plot list can not be null"));
    }

    @Test
    public void failScatterPlotWhenProjectionArrayIsTheTypeExpected() {
        String[] words = new String[] {"hombre", "mujer"};
        double[] projections = new double[] { -0.42, 0.54 };

        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("words", words);
        arguments.put("projections", projections);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
                                                            () -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments),
                                                            "Expectedt IllegalArgumentException but not thrown");

        assertTrue(thrown.getMessage().equals("Words projection list can not be null"));
    }

    @Test
    public void cleaningDoesNotThrow() {
        String[] words = new String[] {"hombre", "mujer"};
        double[][] scatterProjections = new double[][] { new double[] {-0.42, 0.54}, new double[] {-0.12, 0.22} };

        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("words", words);
        arguments.put("projections", scatterProjections);

        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments));
        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments));
        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.SCATTER, arguments));

        words = new String[] {"hombre", "mujer"};
        double[] barProjections = new double[] { -0.42, 0.54 };

        arguments.put("words", words);
        arguments.put("projections", barProjections);

        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments));
        assertDoesNotThrow(() -> PlotManager.getInstance().plot(PLOT_TYPE.BAR, arguments));

        assertDoesNotThrow(() -> PlotManager.getInstance().cleanUp());
    }
}

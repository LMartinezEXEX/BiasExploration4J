package com.biasexplorer4j.WordExploration.Visualization;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class Visualizer extends Application {


    public Visualizer() {}
    
    @SuppressWarnings("unused")
    public static void setup() {
        Platform.setImplicitExit(false);
        final JFXPanel fxPanel = new JFXPanel();
        new Thread(() -> Application.launch(Visualizer.class)).start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {}

    void plot(Stage primaryStage) throws Exception {}
}

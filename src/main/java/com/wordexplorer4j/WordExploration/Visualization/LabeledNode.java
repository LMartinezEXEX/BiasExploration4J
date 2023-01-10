package com.wordexplorer4j.WordExploration.Visualization;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class LabeledNode extends Region {
    
    public LabeledNode(String label) {
        Text text = new Text(label);
        Circle circle = new Circle(5, Color.RED);
        circle.relocate(0, 10);
        this.getChildren().addAll(circle, text);
    }
}

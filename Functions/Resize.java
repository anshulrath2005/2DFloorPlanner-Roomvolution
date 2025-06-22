package Functions;

import FloorPlan.*;
import Assets.DesignElement;

import java.awt.*;
import javax.swing.*;
import java.util.Hashtable;

public class Resize extends JSlider implements ManipulationFunction {
    private DrawingPanel drawingPanel;
    private Select selectFunction;

    public Resize(DrawingPanel drawingPanel, Select selectFunction) {
        super(JSlider.HORIZONTAL, 0, 1000, 100); // Assuming original size is at 100
        this.drawingPanel = drawingPanel;
        this.selectFunction = selectFunction;
        
        setMajorTickSpacing(100);
        setPaintTicks(true);
        setPaintLabels(true);

        // Customize the labels to show 1-10 instead of 10-1000
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("0"));
        labelTable.put(100, new JLabel("1"));
        labelTable.put(200, new JLabel("2"));
        labelTable.put(300, new JLabel("3"));
        labelTable.put(400, new JLabel("4"));
        labelTable.put(500, new JLabel("5"));
        labelTable.put(600, new JLabel("6"));
        labelTable.put(700, new JLabel("7"));
        labelTable.put(800, new JLabel("8"));
        labelTable.put(900, new JLabel("9"));
        labelTable.put(1000, new JLabel("10"));
        setLabelTable(labelTable);

        //Hide slider  initially
        setVisible(false);

        // Add a listener to handle resizing
        addChangeListener(e -> performFunction(new Point()));
    }

    @Override
    public void performFunction(Point point) {
        double scaleFactor = (double) getValue() / 100; // Scale factor from 0.0 to 10.0

        // Resize the selected design elements
        if (selectFunction.selectedElements != null) {
            for (DesignElement element : selectFunction.selectedElements) {
                element.resize(scaleFactor);
            }
        }

        //Update drawing panel and make sure it still recieves keyboard input (not this slider)
        drawingPanel.repaint();
        drawingPanel.requestFocusInWindow();
    }
}

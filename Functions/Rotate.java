package Functions;

import Assets.DesignElement;
import FloorPlan.*;
import java.awt.*;


public class Rotate implements ManipulationFunction {
    private DrawingPanel drawingPanel;
    private Select selectFunction;

    public Rotate(DrawingPanel drawingPanel, Select selectFunction) {
        this.drawingPanel = drawingPanel;
        this.selectFunction = selectFunction;
    }

    @Override
    public void performFunction(Point point) {

        // Rotate the selected design element
        if (selectFunction.selectedElements != null) {
            for (DesignElement element : selectFunction.selectedElements) {
                element.rotate(element.getRotationAngle() + 90);
            } 
        }

        //Update drawing panel and make sure it still recieves keyboard input (not this slider)
        drawingPanel.repaint();
        drawingPanel.requestFocusInWindow();
    }
}

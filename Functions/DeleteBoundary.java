package Functions;
import FloorPlan.DrawingPanel;
import Assets.BlankCanvas;
import Assets.DesignElement;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

public class DeleteBoundary implements ManipulationFunction {

    private DrawingPanel drawingPanel;
    private Select selectFunction;

    public DeleteBoundary(DrawingPanel drawingPanel, Select selectFunction) {
        this.drawingPanel = drawingPanel;
        this.selectFunction = selectFunction;
    }

    @Override
    public void performFunction(Point clickedPoint) {
        List <DesignElement> elements = drawingPanel.getDesignElements();
        Iterator<DesignElement> iterator = elements.iterator();

        //Iterate and remove elements safely
        while (iterator.hasNext()) {
            DesignElement element = iterator.next();
            if (element instanceof BlankCanvas) {
                iterator.remove();
            }
        }
        drawingPanel.repaint(); 
    }
}

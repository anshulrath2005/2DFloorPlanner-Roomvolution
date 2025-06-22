package Functions;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

import Assets.*;
import FloorPlan.*;

public class Select implements ManipulationFunction {
    private DrawingPanel drawingPanel;
    public List<DesignElement> selectedElements;
    public Point startPoint;
    public Point endPoint;

    public Select(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        this.selectedElements = new ArrayList<>();
    }

    @Override
    public void performFunction(Point draggedPoint) {
        List<DesignElement> elements = drawingPanel.getDesignElements();

        //The current selection rectangle
        Rectangle selectionRect = new Rectangle(startPoint);
        endPoint = draggedPoint;
        selectionRect.add(endPoint);

        //Check if the design elements intersect with the selction rectangle and select them if so
        for (DesignElement element : elements) {
            if (element.getBounds().intersects(selectionRect) && !(element instanceof BlankCanvas) && !(element instanceof Room)) {
                if (!selectedElements.contains(element)) {
                    selectedElements.add(element);
                    element.setSelected(true);
                }
            }
        }
    }

    //Draw a transparent blue rectangle as user drags mouse to show the selection area
    public void draw(Graphics2D g) {
        if (startPoint != null && endPoint != null) {
            g.setColor(new Color(0, 0, 255, 100)); 
            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y);
            int width = Math.abs(startPoint.x - endPoint.x);
            int height = Math.abs(startPoint.y - endPoint.y);
            g.fill(new Rectangle(x, y, width, height));
        }
    }

    //Clear all selected items
    public void clearSelection() {
        for (DesignElement element : selectedElements) {
            element.setSelected(false);
        }
        selectedElements.clear();
        drawingPanel.repaint();
    }
}

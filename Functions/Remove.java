package Functions;

import Assets.*;
import FloorPlan.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Remove implements ManipulationFunction {
    private DrawingPanel drawingPanel;
    private Select selectFunction;
    private SelectRoom selectRoom;

    public Remove(DrawingPanel drawingPanel, Select selectFunction, SelectRoom selectRoom) {
        this.drawingPanel = drawingPanel;
        this.selectFunction = selectFunction;
        this.selectRoom = selectRoom;
    }

    @Override
    public void performFunction(Point point) {
        // Remove all selected items from design elements
        if (selectFunction.selectedElements != null || selectRoom.selectedRooms != null) {
            List<DesignElement> elements = drawingPanel.getDesignElements();
            Iterator<DesignElement> iterator = elements.iterator();

            //Iterate and remove elements safely
            while (iterator.hasNext()) {
                DesignElement element = iterator.next();
                if (element.isSelected()) {
                    if (element.getRoom() != null){
                        element.getRoom().roomFixtures.remove(element);
                    }
                    iterator.remove();
                }
            }

            //Clear the removed items from selected items too
            selectFunction.clearSelection();
            selectRoom.clearSelection();
            BottomPanel.notifyCanvasButton(elements);
            TabbedPanel.updateLayout();
        }

        // Redraw the canvas to reflect the changes
        drawingPanel.repaint();
    }
}

package Functions;

import Assets.*;
import FloorPlan.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public class SelectRoom implements ManipulationFunction {
    private DrawingPanel drawingPanel;
    public List <Room> selectedRooms;
    public Point startPoint;
    public Point endPoint;

    public SelectRoom(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        selectedRooms = new ArrayList<>();
    }

    @Override
    public void performFunction(Point mousePoint) {
        List<DesignElement> elements = drawingPanel.getDesignElements();

        //Check if the design elements intersect with the selction rectangle and select them if so
        for (DesignElement element : elements) {
            if (element instanceof Room) {
                Area roomArea = new Area(element.getBounds());
                if (roomArea.contains(mousePoint)) {
                    selectedRooms.add((Room)element);
                    element.setSelected(true);
                    for (DesignElement ele : ((Room)element).roomFixtures) {
                        ele.setSelected(true);
                    }
                }
            }
        }
    }

    //Clear all selected items
    public void clearSelection() {
        for (Room room : selectedRooms) {
            room.setSelected(false);
            for (DesignElement ele : ((Room)room).roomFixtures) {
                ele.setSelected(false);
            }
        }
        selectedRooms.clear();
    }
}

package Functions;

import Assets.BlankCanvas;
import Assets.DesignElement;
import Assets.Room;
import FloorPlan.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

import javax.swing.JOptionPane;

public class MoveRoom implements ManipulationFunction {
    private DrawingPanel drawingPanel;
    private SelectRoom selectRoom;
    public Point startDragPoint;
    public Point initialPoint;
    public int dX, dY;

    public MoveRoom(DrawingPanel drawingPanel, SelectRoom selectRoom) {
        this.drawingPanel = drawingPanel;
        this.selectRoom = selectRoom;
    }

    @Override
    public void performFunction(Point draggedPoint) {
        if (draggedPoint == null) {
            return;
        }

        if (selectRoom.selectedRooms != null) {
            //Find change in mouse position

            BlankCanvas canvas = null;

            for (DesignElement ele : drawingPanel.getDesignElements()) {
                if (ele instanceof BlankCanvas) {
                    canvas = (BlankCanvas)ele;
                    break;
                }
            }
            
            //Shift all selecetd elements by the position change
            for (Room room : selectRoom.selectedRooms) {
                Point start = room.getStartPoint();
                room.setStartPoint(drawingPanel.getRoomPoint(draggedPoint, canvas, room));
                for (DesignElement ele : room.roomFixtures) {
                    int dx = room.getStartPoint().x - start.x;
                    int dy = room.getStartPoint().y - start.y;
                    Point startFix = ele.getStartPoint();
                    Point newStartFix = new Point(startFix.x + dx, startFix.y + dy);
                    ele.setStartPoint(newStartFix);
                }
            }
            
            //Update the starting point for next move
            startDragPoint = draggedPoint;
    
            // Redraw the canvas to reflect the changes
            drawingPanel.repaint();
        }
    }

    public void checkOverlap(Point releasedPoint) {
        calculateD(releasedPoint);
        boolean flag = false;
        if (!selectRoom.selectedRooms.isEmpty()) {
            for (Room room : selectRoom.selectedRooms) {
                if (isOverlapping(room) || !isWithinBounds(room)) {
                    JOptionPane.showMessageDialog(null, "Rooms cannot overlap or go outside the boundary!");
                    flag = true;
                    break;
                }
            }
        }

        if (flag) {
            for (Room room : selectRoom.selectedRooms) {
                Point start = room.getStartPoint();
                //selectedRoom.setPosition(originalX, originalY);
                room.setStartPoint(new Point(start.x - dX, start.y - dY));
                System.out.println(dX);
                System.out.println(dY);
                if (!room.roomFixtures.isEmpty()) {
                    for (DesignElement ele : room.roomFixtures) {
                        System.out.println("Fixtures moved!");
                        ele.setStartPoint(new Point(ele.getStartPoint().x - dX, ele.getStartPoint().y - dY));
                    }
                }
            }
        }
        
        drawingPanel.repaint();
    }
    public boolean isOverlapping(Room newRoom) {
        // Convert newRoom's polygon bounds to an Area
        Area newRoomArea = new Area(newRoom.getBounds());
        List <DesignElement> designElements = drawingPanel.getDesignElements();
        
        for (DesignElement ele : designElements) {
            if (ele instanceof Room) {
                if (ele != newRoom) {
                    // Convert existing room's polygon bounds to an Area
                    Area existingRoomArea = new Area(ele.getBounds());
    
                    // Check for intersection
                    existingRoomArea.intersect(newRoomArea);
                    if (!existingRoomArea.isEmpty()) {
                        return true; // Overlap detected
                    }
                }
            }
        }
        return false; // No overlap
    }
    public boolean isWithinBounds(Room newRoom) {
        Point newRoomPoint = newRoom.getStartPoint();
        int graphicHeight = newRoom.getHeight();
        int graphicWidth = newRoom.getWidth();
        int graphicX = newRoomPoint.x - graphicWidth/2;
        int graphicY = newRoomPoint.y - graphicHeight/2;
        int rectX, rectY, rectHeight, rectWidth;

        for (DesignElement ele : drawingPanel.getDesignElements()) {
            if (ele instanceof BlankCanvas) {
                rectX = ele.getStartPoint().x;
                rectY = ele.getStartPoint().y;
                rectHeight = ((BlankCanvas)ele).getFrameHeight();
                rectWidth = ((BlankCanvas)ele).getFrameWidth();
                return graphicX >= rectX &&
                       graphicY >= rectY &&
                       (graphicX + graphicWidth) <= (rectX + rectWidth) &&
                       (graphicY + graphicHeight) <= (rectY + rectHeight);
            } 
        }
        return false;
    }

    public void calculateD(Point releasedPoint) {
        if (initialPoint != null) {
            this.dX = releasedPoint.x - initialPoint.x;
            this.dY = releasedPoint.y - initialPoint.y;
        }
    }
}
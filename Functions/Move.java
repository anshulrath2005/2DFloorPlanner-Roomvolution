package Functions;

import Assets.BlankCanvas;
import Assets.DesignElement;
import Assets.Room;
import FloorPlan.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

import javax.swing.JOptionPane;
/**
 * Class representing a move function
 *
 * @author ChatGPT, Wahad Latif
 */
public class Move implements ManipulationFunction {
    private DrawingPanel drawingPanel;
    private Select selectFunction;
    public Point startDragPoint;
    public Point initialPoint;
    public int dX, dY;

    public Move(DrawingPanel drawingPanel, Select selectFunction) {
        this.drawingPanel = drawingPanel;
        this.selectFunction = selectFunction;
    }

    @Override
    public void performFunction(Point draggedPoint) {
        if (draggedPoint == null) {
            return;
        }
    
        if (selectFunction.selectedElements != null) {
            //Find change in mouse position
            int dx = draggedPoint.x - startDragPoint.x;
            int dy = draggedPoint.y - startDragPoint.y;
            
            //Shift all selecetd elements by the position change
            for (DesignElement element : selectFunction.selectedElements) {
                Point start = element.getStartPoint();
                if (element.isFixture()) {
                    Point snapPoint = getClosestPointOnRoomEdge(draggedPoint, element.getRoom(), element);
                    if (snapPoint != null) {
                        element.setStartPoint(snapPoint);
                    } else {
                        element.setStartPoint(start);
                    }
                } else {
                    Point newStart = new Point(start.x + dx, start.y + dy);
                    element.setStartPoint(newStart);
                }
            }
            
            //Update the starting point for next move
            startDragPoint = draggedPoint;
    
            // Redraw the canvas to reflect the changes
            drawingPanel.repaint();
        } 
    }

    public boolean isOverlapping(DesignElement designElement) {
        // Convert designElement's polygon bounds to an Area
        Area designElementArea = new Area(designElement.getBounds());
        List <DesignElement> designElements = drawingPanel.getDesignElements();
        
        for (DesignElement ele : designElements) {
            if (!(ele instanceof Room)) {
                if (ele != designElement) {
                    // Convert existing room's polygon bounds to an Area
                    Area existingRoomArea = new Area(ele.getBounds());
    
                    // Check for intersection
                    existingRoomArea.intersect(designElementArea);
                    if (!existingRoomArea.isEmpty()) {
                        return true; // Overlap detected
                    }
                }
            }
        }
        return false; // No overlap
    }

    public boolean isWithinBounds(DesignElement designElement) {
        Point designElementPoint = designElement.getStartPoint();
        int graphicHeight = designElement.getHeight();
        int graphicWidth = designElement.getWidth();
        int graphicX = designElementPoint.x - graphicWidth/2;
        int graphicY = designElementPoint.y - graphicHeight/2;
        int rectX, rectY, rectHeight, rectWidth;
        List <DesignElement> designElements = drawingPanel.getDesignElements();
    
        for (DesignElement ele : designElements) {
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

    public boolean isWithinRoom(DesignElement designElement) {
        Point designElementPoint = designElement.getStartPoint();
        int graphicHeight = designElement.getHeight();
        int graphicWidth = designElement.getWidth();
        int graphicX = designElementPoint.x - graphicWidth/2;
        int graphicY = designElementPoint.y - graphicHeight/2;

        if (designElement.getRoom() != null) {
            Room selectedRoom = designElement.getRoom();
            int roomX = selectedRoom.getStartPoint().x - selectedRoom.getWidth()/2;
            int roomY = selectedRoom.getStartPoint().y - selectedRoom.getHeight()/2;
            int roomWidth = selectedRoom.getWidth();
            int roomHeight = selectedRoom.getHeight();
            return graphicX >= roomX &&
                       graphicY >= roomY &&
                       (graphicX + graphicWidth) <= (roomX + roomWidth) &&
                       (graphicY + graphicHeight) <= (roomY + roomHeight);
        }
        return false;
    }

    private Point getClosestPointOnRoomEdge(Point e, DesignElement selectedRoom, DesignElement element) {
        int selectedRoomX = selectedRoom.getStartPoint().x;
        int selectedRoomY = selectedRoom.getStartPoint().y;
        int width = selectedRoom.getWidth()/2;
        int height = selectedRoom.getHeight()/2;
        if (selectedRoom instanceof BlankCanvas) {
            selectedRoomX = selectedRoomX + width;
            selectedRoomY = selectedRoomY + height;
        }
        int dx1 = e.x - selectedRoomX + width;
        int dx2 = selectedRoomX + width - e.x;
        int dy1 = e.y - selectedRoomY + height;
        int dy2 = selectedRoomY + height - e.y;
        int xPoint = 0;
        int yPoint = 0;
        if (dx1 >= 0 && dx2 >= 0 && dy1 >= 0 && dy2 >= 0) {
            if (dx1 <= dx2) {
                if (dy1 <= dy2) {
                    // Point A
                    if (dx1 <= dy1) {
                        xPoint = selectedRoomX - width + element.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY - height + element.getHeight()/2;
                        element.rotate(0);
                    }
                    
                } else {
                    // Point D
                    if (dx1 <= dy2) {
                        xPoint = selectedRoomX - width + element.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY + height - element.getHeight()/2;
                        element.rotate(180);
                    }
                }
            } else {
                if (dy1 <= dy2) {
                    // Point B
                    if (dx2 <= dy1) {
                        xPoint = selectedRoomX + width - element.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY - height + element.getHeight()/2;
                        element.rotate(0);
                    }
                } else {
                    // Point C
                    if (dx2 <= dy2) {
                        xPoint = selectedRoomX + width - element.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY + height - element.getHeight()/2;
                        element.rotate(180);
                    }
                }
            }
        }
    
        return new Point(xPoint, yPoint);
    }

    public void checkOverlap(Point releasedPoint) {
        calculateD(releasedPoint);
        boolean flag = false;
        if (!selectFunction.selectedElements.isEmpty()) {
            for (DesignElement ele : selectFunction.selectedElements) {
                if (isOverlappingWithElements(ele) || !isWithinRoom(ele)) {
                    JOptionPane.showMessageDialog(null, "Elements cannot overlap or go outside the boundary!");
                    flag = true;
                    break;
                }
            }
        }

        if (flag) {
            for (DesignElement ele : selectFunction.selectedElements) {
                ele.setStartPoint(new Point(ele.getStartPoint().x - dX, ele.getStartPoint().y - dY));
                ele.rotate(ele.getSavedRotationAngle());
            }
        }
        
        drawingPanel.repaint();
    }

    public boolean isOverlappingWithElements(DesignElement element) {
        Room selectedRoom = element.getRoom();
        for (DesignElement ele : selectedRoom.roomFixtures) {
            if (!(ele.equals(element))) {
                Area designElementArea = new Area(ele.getBounds());
                Area elementArea = new Area(element.getBounds());
                designElementArea.intersect(elementArea);
                    if (!designElementArea.isEmpty()) {
                        return true;
                    }
            }
        }
        return false;
    }

    public void calculateD(Point releasedPoint) {
        this.dX = releasedPoint.x - initialPoint.x;
        this.dY = releasedPoint.y - initialPoint.y;
    }
}

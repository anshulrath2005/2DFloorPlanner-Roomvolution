package Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class BlankCanvas implements DesignElement{
    private int frameWidth;
    private int frameHeight;

    private Point startPoint;
    private Point endPoint;
    private boolean isSelected = false;

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public void drawCanvas(Point draggedPoint) {
        Rectangle selectionRect = new Rectangle(startPoint);
        endPoint = draggedPoint;
        selectionRect.add(endPoint);
    }

    @Override
    public void draw(Graphics2D g) {

        // Save the current graphics transformation
        AffineTransform oldTransform = g.getTransform();

        // Translate and rotate the graphics context to draw the stove at the desired position and angle
        if (startPoint != null && endPoint != null) {
            g.setColor(Color.WHITE); 
            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y);
            frameWidth = Math.abs(startPoint.x - endPoint.x);
            frameHeight = Math.abs(startPoint.y - endPoint.y);
            
            // Fill the rectangle
            g.fill(new Rectangle(x, y, frameWidth, frameHeight));
        
            // Draw the grid lines
            int gridSize = 20; // 20x20 pixel grid squares
            g.setColor(Color.LIGHT_GRAY);
            
            // Vertical grid lines
            for (int x1 = x + gridSize; x1 < x + frameWidth; x1 += gridSize) {
                g.drawLine(x1, y, x1, y + frameHeight);
            }
        
            // Horizontal grid lines
            for (int y1 = y + gridSize; y1 < y + frameHeight; y1 += gridSize) {
                g.drawLine(x, y1, x + frameWidth, y1);
            }

            // Draw the border
            g.setStroke(new BasicStroke(2));
            g.setColor(Color.BLACK);
            g.drawRect(x, y, frameWidth, frameHeight);
        }
        
        // Restore the old graphics transformation
        g.setTransform(oldTransform);
    }

    @Override
    public Shape getBounds() {
        // Calculate the coordinates of the corners of the unrotated rectangle
        int x1 = startPoint.x;
        int y1 = startPoint.y;
        int x2 = startPoint.x + frameWidth;
        int y2 = startPoint.y;
        int x3 = startPoint.x + frameWidth;
        int y3 = startPoint.y + frameHeight;
        int x4 = startPoint.x;
        int y4 = startPoint.y + frameHeight;
    
        int[] xPoints = {x1, x2, x3, x4};
        int[] yPoints = {y1, y2, y3, y4};

        Polygon polygon = new Polygon(xPoints, yPoints, 4);

        return polygon;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void resize(double scale) {
        frameWidth = (int) (scale * frameWidth);
        frameHeight = (int) (scale * frameHeight);
    }

    @Override
    public void rotate(int angle) {

    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getWidth() {
        return frameWidth;
    }

    public int getHeight() {
        return frameHeight;
    }

    public int getRotationAngle() {
        return 0;
    }

    @Override
    public boolean isFixture() {
        return false;
    }

    private Room inRoom = null;

    @Override
    public Room getRoom() {
        return inRoom;
    }

    @Override
    public void setRoom(Room room) {
        inRoom = room;
    }

    public int savedRotationAngle;
    public void saveRotationAngle(int angle) {
        savedRotationAngle = angle;
    }
    public int getSavedRotationAngle() {
        return savedRotationAngle;
    }
}

package Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;


public class Table implements DesignElement {
    private static final int DEFAULT_TABLE_WIDTH = 100;
    private static final int DEFAULT_TABLE_HEIGHT = 60;
    private static final int DEFAULT_LEG_WIDTH = 5;

    private int tableWidth = DEFAULT_TABLE_WIDTH;
    private int tableHeight = DEFAULT_TABLE_HEIGHT;
    private int legWidth = DEFAULT_LEG_WIDTH;

    private Point startPoint;
    private boolean isSelected = false;
    private int rotationAngle = 0;

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    @Override
    public void draw(Graphics2D g) {
        if (isSelected) {
            g.setColor(Color.MAGENTA);
        } else {
            g.setColor(Color.DARK_GRAY);
        }
        g.setStroke(new BasicStroke(2));
        
        // Save the current graphics transformation
        AffineTransform oldTransform = g.getTransform();

        // Translate and rotate the graphics context to draw the stove at the desired position and angle
        g.translate(startPoint.x, startPoint.y);
        g.rotate(Math.toRadians(rotationAngle));

        // Draw desk top (circle)
        int diameter = Math.min(tableWidth, tableHeight);
        g.drawOval(-diameter/2, -diameter/2, diameter, diameter);

        // Draw desk legs (four cardinal points)
        int legSize = legWidth;
        g.drawRect(-diameter/2 + tableWidth / 3 - legWidth, -diameter/2, legSize, legSize); // North leg
        g.drawRect(-diameter/2 + tableWidth / 3 - legWidth, -diameter/2 + tableHeight - legSize, legSize, legSize); // South leg
        g.drawRect(-diameter/2, -diameter/2 + tableHeight / 2 - legWidth, legSize, legSize); // West leg
        g.drawRect(-diameter/2 + tableWidth / 2 + legWidth, -diameter/2 + tableHeight / 2 - legWidth, legSize, legSize); // East leg

        // Restore the old graphics transformation
        g.setTransform(oldTransform);
    }

    @Override
    public Shape getBounds() {
        // Calculate the diameter of the circle
        int diameter = Math.min(tableWidth, tableHeight);

        // Calculate the center of the desk
        int centerX = startPoint.x - diameter/2;
        int centerY = startPoint.y - diameter/2;
        
        // Create a circle shape
        return new java.awt.geom.Ellipse2D.Double(centerX, centerY, diameter, diameter);
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
        tableWidth = (int) (scale * DEFAULT_TABLE_WIDTH);
        tableHeight = (int) (scale * DEFAULT_TABLE_HEIGHT);
        legWidth = (int) (scale * DEFAULT_LEG_WIDTH);
    }

    @Override
    public void rotate(int angle) {
        rotationAngle = angle;
    }

    public int getWidth() {
        if (rotationAngle % 180 == 0) {
            return tableWidth;
        } else {
            return tableHeight;
        }
    }

    public int getHeight() {
        if (rotationAngle % 180 == 0) {
            return tableHeight;
        } else {
            return tableWidth;
        }
    }

    public int getRotationAngle() {
        return rotationAngle;
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


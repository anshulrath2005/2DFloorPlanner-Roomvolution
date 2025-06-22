package Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;


public class Chair implements DesignElement {
    private static final int DEFAULT_CHAIR_WIDTH = 30;
    private static final int DEFAULT_CHAIR_HEIGHT = 30;

    private int chairWidth = DEFAULT_CHAIR_WIDTH;
    private int chairHeight = DEFAULT_CHAIR_HEIGHT;

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
            g.setColor(Color.BLACK);
        }
        g.setStroke(new BasicStroke(2));

        // Save the current graphics transformation
        AffineTransform oldTransform = g.getTransform();

        // Translate and rotate the graphics context to draw the stove at the desired position and angle
        g.translate(startPoint.x, startPoint.y);
        g.rotate(Math.toRadians(rotationAngle));

        // Draw chair backrest
        int backrestWidth = chairWidth;
        int backrestHeight = chairHeight / 5;
        g.drawRect(-chairWidth/2, -chairHeight/2, backrestWidth, backrestHeight);

        // Draw chair seat
        int seatWidth = chairWidth;
        int seatHeight = (chairHeight / 5)*4;
        g.drawRect(-chairWidth/2, -chairHeight/2 + backrestHeight, seatWidth, seatHeight);
        

        // Restore the old graphics transformation
        g.setTransform(oldTransform);
    }

     @Override
    public Shape getBounds() {
        // Calculate the coordinates of the corners of the unrotated rectangle
        int x1 = -chairWidth / 2;
        int y1 = -chairHeight / 2;
        int x2 = chairWidth / 2;
        int y2 = -chairHeight / 2;
        int x3 = chairWidth / 2;
        int y3 = chairHeight / 2;
        int x4 = -chairWidth / 2;
        int y4 = chairHeight / 2;

        // Apply the rotation to each corner
        double cosTheta = Math.cos(Math.toRadians(rotationAngle));
        double sinTheta = Math.sin(Math.toRadians(rotationAngle));

        int[] xPoints = {(int) (x1 * cosTheta - y1 * sinTheta), (int) (x2 * cosTheta - y2 * sinTheta),
                (int) (x3 * cosTheta - y3 * sinTheta), (int) (x4 * cosTheta - y4 * sinTheta)};
        int[] yPoints = {(int) (x1 * sinTheta + y1 * cosTheta), (int) (x2 * sinTheta + y2 * cosTheta),
                (int) (x3 * sinTheta + y3 * cosTheta), (int) (x4 * sinTheta + y4 * cosTheta)};

        // Create a polygon from the rotated corners
        Polygon polygon = new Polygon(xPoints, yPoints, 4);

        // Translate the polygon to the start point
        polygon.translate(startPoint.x, startPoint.y);

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
        chairWidth = (int) (scale * DEFAULT_CHAIR_WIDTH);
        chairHeight = (int) (scale * DEFAULT_CHAIR_HEIGHT);
    }

    @Override
    public void rotate(int angle) {
        rotationAngle = angle;
    }

    public int getWidth() {
        if (rotationAngle % 180 == 0) {
            return chairWidth;
        } else {
            return chairHeight;
        }
    }

    public int getHeight() {
        if (rotationAngle % 180 == 0) {
            return chairHeight;
        } else {
            return chairWidth;
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

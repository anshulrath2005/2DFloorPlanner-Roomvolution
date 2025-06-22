package Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;


public class Stove implements DesignElement {
    private static final int DEFAULT_STOVE_WIDTH = 60;
    private static final int DEFAULT_STOVE_HEIGHT = 40;
    private int stoveWidth = DEFAULT_STOVE_WIDTH;
    private int stoveHeight = DEFAULT_STOVE_HEIGHT;
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

        // Draw stove body outline
        g.drawRect(-stoveWidth / 2, -stoveHeight / 2, stoveWidth, stoveHeight);

        // Draw stove burners outline
        int burnerSize = Math.min(stoveWidth, stoveHeight) / 4;
        int burnerX = -stoveWidth / 3;
        int burnerY = -stoveHeight / 3;
        for (int i = 0; i < 2; i++) {
            g.drawOval(burnerX, burnerY, burnerSize, burnerSize);
            g.drawOval(burnerX, burnerY + stoveHeight / 3, burnerSize, burnerSize);
            burnerX += stoveWidth / 2;
        }

        // Restore the old graphics transformation
        g.setTransform(oldTransform);
    }

    @Override
    public Shape getBounds() {
        // Calculate the coordinates of the corners of the unrotated rectangle
        int x1 = -stoveWidth / 2;
        int y1 = -stoveHeight / 2;
        int x2 = stoveWidth / 2;
        int y2 = -stoveHeight / 2;
        int x3 = stoveWidth / 2;
        int y3 = stoveHeight / 2;
        int x4 = -stoveWidth / 2;
        int y4 = stoveHeight / 2;

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
        stoveWidth = (int) (scale * DEFAULT_STOVE_WIDTH);
        stoveHeight = (int) (scale * DEFAULT_STOVE_HEIGHT);
    }

    @Override
    public void rotate(int angle) {
        rotationAngle = angle;
    }

    public int getWidth() {
        if (rotationAngle % 180 == 0) {
            return stoveWidth;
        } else {
            return stoveHeight;
        }
    }

    public int getHeight() {
        if (rotationAngle % 180 == 0) {
            return stoveHeight;
        } else {
            return stoveWidth;
        }
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    @Override
    public boolean isFixture() {
        return true;
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
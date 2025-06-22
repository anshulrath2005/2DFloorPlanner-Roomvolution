package Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;


public class Sofa implements DesignElement {
    private static final int DEFAULT_SOFA_WIDTH = 60;
    private static final int DEFAULT_SOFA_HEIGHT = 30;
    private int sofaWidth = DEFAULT_SOFA_WIDTH;
    private int sofaHeight = DEFAULT_SOFA_HEIGHT;
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

        // Calculate the center point of the sofa
        int centerX = startPoint.x;
        int centerY = startPoint.y + sofaHeight / 2;

        // Save the current graphics transformation
        AffineTransform oldTransform = g.getTransform();

        // Translate to the center point
        g.translate(centerX, centerY);

        // Rotate the graphics context
        g.rotate(Math.toRadians(rotationAngle));

        // Translate back to the original position
        g.translate(-centerX, -centerY);

        // Draw sofa base outline
        g.drawRect(startPoint.x - sofaWidth / 2, startPoint.y, sofaWidth, sofaHeight);

        // Draw sofa backrest outline
        g.drawRect(startPoint.x - sofaWidth / 2, startPoint.y - sofaHeight / 2, sofaWidth, sofaHeight / 2);

        // // Draw sofa arms outline
        g.drawRect(startPoint.x - sofaWidth / 2 - sofaWidth / 6, startPoint.y, sofaWidth / 6, sofaHeight);
        g.drawRect(startPoint.x + sofaWidth / 2, startPoint.y, sofaWidth / 6, sofaHeight);

        // // Draw sofa legs outline
        g.drawRect(startPoint.x - sofaWidth / 2 - sofaWidth / 6, startPoint.y + sofaHeight, sofaWidth / 6, sofaHeight / 4);
        g.drawRect(startPoint.x + sofaWidth / 2, startPoint.y + sofaHeight, sofaWidth / 6, sofaHeight / 4);

        // Restore the old graphics transformation
        g.setTransform(oldTransform);
    }

    @Override
    public Shape getBounds() {
        // Calculate the center of the sofa
        int centerX = startPoint.x;
        int centerY = startPoint.y + sofaHeight / 2;

        // Calculate the coordinates of the corners of the unrotated rectangle
        int x1 = -sofaWidth / 2 -sofaWidth / 6 ;
        int y1 = -sofaWidth / 2;
        int x2 = sofaWidth / 2 + sofaWidth / 6 ;
        int y2 = -sofaWidth / 2;
        int x3 = sofaWidth / 2 + sofaWidth / 6 ;
        int y3 = sofaHeight -sofaWidth / 8;
        int x4 = -sofaWidth / 2 - sofaWidth / 6;
        int y4 = sofaHeight -sofaWidth / 8;

        // Apply the rotation to each corner
        double cosTheta = Math.cos(Math.toRadians(rotationAngle));
        double sinTheta = Math.sin(Math.toRadians(rotationAngle));

        int[] xPoints = {(int) (x1 * cosTheta - y1 * sinTheta), (int) (x2 * cosTheta - y2 * sinTheta),
                (int) (x3 * cosTheta - y3 * sinTheta), (int) (x4 * cosTheta - y4 * sinTheta)};
        int[] yPoints = {(int) (x1 * sinTheta + y1 * cosTheta), (int) (x2 * sinTheta + y2 * cosTheta),
                (int) (x3 * sinTheta + y3 * cosTheta), (int) (x4 * sinTheta + y4 * cosTheta)};

        // Translate the rotated points to the center of the sofa
        for (int i = 0; i < 4; i++) {
            xPoints[i] += centerX;
            yPoints[i] += centerY;
        }

        // Create a polygon from the rotated corners
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
        sofaWidth = (int) (scale * DEFAULT_SOFA_WIDTH);
        sofaHeight = (int) (scale * DEFAULT_SOFA_HEIGHT);
    }

    @Override
    public void rotate(int angle) {
        rotationAngle = angle;
    }


    public int getWidth() {
        if (rotationAngle % 180 == 0) {
            return sofaWidth + sofaWidth/3;
        } else {
            return 2 * sofaHeight;
        }
    }

    public int getHeight() {
        if (rotationAngle % 180 == 0) {
            return 2 * sofaHeight;
        } else {
            return sofaWidth + sofaWidth/3;
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


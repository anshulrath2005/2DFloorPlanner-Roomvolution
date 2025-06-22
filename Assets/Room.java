package Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Room implements DesignElement {
    private int roomWidth;
    private int roomHeight;
    private RoomType roomType;

    private Point startPoint;
    private boolean isSelected = false;
    private int rotationAngle = 0;

    public ArrayList<DesignElement> roomFixtures;
    
    public Room(int width, int height, RoomType roomType) {
        this.roomWidth = width;
        this.roomHeight = height;
        this.roomType = roomType;
        roomFixtures = new ArrayList<>();
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    @Override
    public void draw(Graphics2D g) {
        switch(roomType) {
            case Bathroom:
                g.setColor(new Color(0, 0, 255, 100));
                break;
            case Bedroom:
                g.setColor(new Color(0, 255, 0, 100));
                break;
            case DrawingRoom:
                g.setColor(new Color(255, 165, 0, 100));
                break;
            case Kitchen:
                g.setColor(new Color(255, 0, 0, 100));
                break;
            default:
                break;
        }
        if (isSelected) {
            g.setColor(new Color(255, 0, 255, 100));
        } 
        g.setStroke(new BasicStroke(2));

        // Save the current graphics transformation
        AffineTransform oldTransform = g.getTransform();

        // Translate and rotate the graphics context to draw the stove at the desired position and angle
        g.translate(startPoint.x, startPoint.y);
        g.rotate(Math.toRadians(rotationAngle));

        // Draw counter
        g.fillRect(-roomWidth/2, -roomHeight/2, roomWidth, roomHeight);
        g.setColor(Color.black);
        g.drawRect(-roomWidth/2, -roomHeight/2, roomWidth, roomHeight);

        // Restore the old graphics transformation
        g.setTransform(oldTransform);
    }

    @Override
    public Shape getBounds() {
        // Calculate the coordinates of the corners of the unrotated rectangle
        int x1 = -roomWidth / 2;
        int y1 = -roomHeight / 2;
        int x2 = roomWidth / 2;
        int y2 = -roomHeight / 2;
        int x3 = roomWidth / 2;
        int y3 = roomHeight / 2;
        int x4 = -roomWidth / 2;
        int y4 = roomHeight / 2;

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
        roomWidth = (int) (scale * roomWidth);
        roomHeight = (int) (scale * roomHeight);
    }

    @Override
    public void rotate(int angle) {
        rotationAngle = angle;
    }

    public int getWidth() {
        return roomWidth;
    }

    public int getHeight() {
        return roomHeight;
    }

    public int getRotationAngle() {
        return 0;
    }

    @Override
    public boolean isFixture() {
        return false;
    }

    @Override
    public Room getRoom() {
        return this;
    }

    @Override
    public void setRoom(Room room) {}

    public RoomType getRoomType() {
        return roomType;
    }

    public int savedRotationAngle;
    public void saveRotationAngle(int angle) {
        savedRotationAngle = angle;
    }
    public int getSavedRotationAngle() {
        return savedRotationAngle;
    }
}

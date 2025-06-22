package Functions;

import Assets.BlankCanvas;
import Assets.DesignElement;
import Assets.Room;
import Assets.RoomType;
import FloorPlan.DrawingPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import javax.swing.*;

public class AddRoom implements ManipulationFunction {

    private DrawingPanel drawingPanel;
    private SelectRoom selectRoom;
    private RoomType roomType;
    private String roomDirection;
    private String roomAlignment;
    private Room newRoom;
    private Room selectedRoom;

    public AddRoom(DrawingPanel drawingPanel, SelectRoom selectRoom) {
        this.drawingPanel = drawingPanel;
        this.selectRoom = selectRoom;
    }

    @Override
    public void performFunction(Point clickedPoint) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));
        String[] options = {"Choose a Room Type", "Bedroom", "Bathroom", "Kitchen", "DrawingRoom"};

        JComboBox<String> comboBox = new JComboBox<>(options);

        JCheckBox dir = new JCheckBox();
        dir.setText("Set Manually");

        JPanel widthPanel = new JPanel();
        JLabel widthLabel = new JLabel("Width: ");
        JTextField roomw = new JTextField();
        widthLabel.setPreferredSize(new Dimension(60,30));
        roomw.setPreferredSize(new Dimension(100,30));
        widthPanel.add(widthLabel);
        widthPanel.add(roomw);

        JPanel heightPanel = new JPanel();
        JLabel heightLabel = new JLabel("Height: ");
        JTextField rooml = new JTextField();
        heightLabel.setPreferredSize(new Dimension(60,30));
        rooml.setPreferredSize(new Dimension(100,30));
        heightPanel.add(heightLabel);
        heightPanel.add(rooml);

        JButton setDim = new JButton("Set Dimensions");
        setDim.setPreferredSize(new Dimension(100, 30));

        panel.add(comboBox);
        panel.add(dir);
        panel.add(widthPanel);
        panel.add(heightPanel);
        panel.add(setDim);
        //Choose roomType
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == comboBox) {
                    roomType = RoomType.valueOf((String)comboBox.getSelectedItem());
                }
            }
        });
        //create room with the given width and length
        setDim.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == setDim) {
                    int width = Integer.parseInt((String)roomw.getText());
                    int height = Integer.parseInt((String)rooml.getText());
                    System.out.println(width);
                    System.out.println(height);
                    newRoom = new Room(width, height, roomType);
                    System.out.println("Room Created!");
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(
                null, 
                panel,
                "Select a Room Type", 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(2, 1));
        String[] options1 = {"Choose Direction", "NORTH" , "SOUTH" , "EAST" , "WEST"};
        String[] options2 = {"Choose Alignment", "LEFT","RIGHT" , "CENTER"};
        JComboBox<String> comboBox1 = new JComboBox<>(options1);
        JComboBox<String> comboBox2 = new JComboBox<>(options2);
        panel2.add(comboBox1);
        panel2.add(comboBox2);

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (e.getSource() == comboBox1) {
                    roomDirection = (String)comboBox1.getSelectedItem();
                }
            }
        });

        comboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (e.getSource() == comboBox2) {
                    roomAlignment = (String)comboBox2.getSelectedItem();
                }
            }
        });

        if (result == JOptionPane.OK_OPTION){
            // Room Options
            if(!dir.isSelected()){
                drawingPanel.onFunctionSelected(selectRoom);
                // while (selectRoom.selectedRooms.isEmpty()) {}
                if (!selectRoom.selectedRooms.isEmpty()) {
                    selectedRoom = selectRoom.selectedRooms.get(0);
                    System.out.println("Room Selected!");
                    int result2 = JOptionPane.showConfirmDialog(
                        null, 
                        panel2,
                        "Select a Room Type", 
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if(result2 == JOptionPane.OK_OPTION){
                        Point setPoint = calculateNewRoomPosition(selectedRoom, newRoom, roomDirection, roomAlignment);
                        newRoom.setStartPoint(setPoint);
                        if (!isOverlapping(newRoom) && (isWithinBounds(newRoom))) {
                            System.out.println("Executing!!");
                            drawingPanel.getDesignElements().add(newRoom);
                        } else {
                            JOptionPane.showMessageDialog(null, "Rooms cannot overlap or go outside the boundary");
                        }
                    }
                    else{
                        System.out.println("No Option Selected");
                    }
                } 
            } else {
                    drawingPanel.onElementSelected(newRoom);
                }
        } else {
            System.out.println("No Option Selected");
        }
    }
    public boolean isOverlapping(Room newRoom) {
        // Convert newRoom's polygon bounds to an Area
        Area newRoomArea = new Area(newRoom.getBounds());
        
        for (DesignElement ele : drawingPanel.getDesignElements()) {
            if (ele instanceof Room) {
                if (!ele.equals(newRoom)) {
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
    private Point calculateNewRoomPosition(Room selectedRoom, Room newRoom, String direction, String alignment) {
        int x = selectedRoom.getStartPoint().x;
        int y = selectedRoom.getStartPoint().y;
        
        switch (direction) {
            case "NORTH": {
                switch(roomAlignment) {
                    case "LEFT":{
                        x = x - selectedRoom.getWidth()/2 + newRoom.getWidth()/2;
                        y = y - selectedRoom.getHeight()/2 - newRoom.getHeight()/2;
                        break;
                    }
                    case "RIGHT":{
                        x = x + selectedRoom.getWidth()/2 - newRoom.getWidth()/2;
                        y = y - selectedRoom.getHeight()/2 - newRoom.getHeight()/2;
                        break;
                    }
                    case "CENTER":{
                        y = y - selectedRoom.getHeight()/2 - newRoom.getHeight()/2;
                        break;
                    }
                }
                break;
            }
            case "SOUTH": {
                switch(roomAlignment) {
                    case "LEFT": {
                        x = x - selectedRoom.getWidth()/2 + newRoom.getWidth()/2;
                        y = y + selectedRoom.getHeight()/2 + newRoom.getHeight()/2;
                        break;
                    }

                    case "RIGHT":{
                        x = x + selectedRoom.getWidth()/2 - newRoom.getWidth()/2;
                        y = y + selectedRoom.getHeight()/2 + newRoom.getHeight()/2;
                        break;
                    }
                    case "CENTER":{
                        y = y + selectedRoom.getHeight()/2 + newRoom.getHeight()/2;
                        break;
                    }
                }
                break;
            }
            case "WEST": {
                switch(roomAlignment) {
                    case "LEFT": {
                        x = x - selectedRoom.getWidth()/2 - newRoom.getWidth()/2;
                        y = y + selectedRoom.getHeight()/2 - newRoom.getHeight()/2;       
                        break;
                    }
                    case "RIGHT":{
                        x = x - selectedRoom.getWidth()/2 - newRoom.getWidth()/2;
                        y = y - selectedRoom.getHeight()/2 + newRoom.getHeight()/2;       
                        break;
                    }
                    case "CENTER":{
                        x = x - selectedRoom.getWidth()/2 - newRoom.getWidth()/2;      
                        break;
                    }
                }
                break;
            }
            case "EAST": {
                switch(roomAlignment) {
                    case "LEFT": {
                        x = x + selectedRoom.getWidth()/2 + newRoom.getWidth()/2;
                        y = y - selectedRoom.getHeight()/2 + newRoom.getHeight()/2;       
                        break;
                    }
                    case "RIGHT": {
                        x = x + selectedRoom.getWidth()/2 + newRoom.getWidth()/2;
                        y = y + selectedRoom.getHeight()/2 - newRoom.getHeight()/2;       
                        break;
                    }
                    case "CENTER":{
                        x = x + selectedRoom.getWidth()/2 + newRoom.getWidth()/2;       
                        break;
                    }
                }
                break;
            }
            default:
                break;
        }
        return new Point(x, y);
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
}

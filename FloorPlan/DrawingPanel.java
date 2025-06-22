package FloorPlan;

import Assets.*;
import Assets.Window;
import Functions.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class DrawingPanel extends JPanel implements ElementSelectedObserver, FunctionSelectedObserver {
    //The canvas image
    private BufferedImage canvas;
    //The last mouse point
    private Point lastPoint;

    //All design elements on canvas that are drawn
    private List<DesignElement> designElements;
    //Currently drawing element (previewing on canvas, draw will be finalized on click)
    private DesignElement currentElement;

    //Manipulation Functions
    private Select selectFunction;
    private Move moveFunction;
    private Remove removeFunction;
    private Resize resizeSlider;
    private Rotate rotateSlider;
    private AddRoom addRoom;
    private DeleteBoundary deleteBoundary;
    private SelectRoom selectRoom;
    private MoveRoom moveRoom;
    //Currently selected function
    private ManipulationFunction currentFunction;


    public DrawingPanel(int width, int height) {
        //Create the canvas image and set the focus
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        clearCanvas();
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);

        //Initialize variables
        designElements = new ArrayList<>();
        selectFunction = new Select(this);
        selectRoom = new SelectRoom(this);
        moveFunction = new Move(this, selectFunction);
        moveRoom = new MoveRoom(this, selectRoom);
        removeFunction = new Remove(this, selectFunction, selectRoom);
        resizeSlider = new Resize(this, selectFunction);
        rotateSlider = new Rotate(this, selectFunction);
        addRoom = new AddRoom(this, selectRoom);
        deleteBoundary = new DeleteBoundary(this, selectFunction);

        //Mouse Event Handlers
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Return if it wasn't the left mouse button
                if (e.getButton() != MouseEvent.BUTTON1) {return;}

                lastPoint = e.getPoint();
                
                //ELEMENTS
                //Wall will only be finalized once mouse is released later, for now set the start point
                if (currentElement instanceof BlankCanvas) {
                    BlankCanvas blankCanvas = new BlankCanvas();
                    currentElement = blankCanvas;
                    blankCanvas.setStartPoint(lastPoint);
                    blankCanvas.setEndPoint(blankCanvas.getStartPoint());
                    designElements.add(currentElement);
                } else if (currentElement instanceof Room) {
                    currentElement.setStartPoint(currentElement.getStartPoint());
                    
                    //check Overlapp and check Bounds
                    if (isOverlapping((Room)currentElement) || !isWithinBounds((Room)currentElement)) {
                        //selectedRoom.setPosition(originalX, originalY);
                        JOptionPane.showMessageDialog(null, "Rooms cannot overlap or go outside the boundary!");
                        currentElement = null;
                    }
                    //Addroom to design elements if Overlap is not found
                    if(currentElement!=null){
                        designElements.add(currentElement);
                        currentElement = null;
                    }
                } else if (currentElement instanceof Window){
                    // Check if canvasPanel is added
                    boolean canvasPresent = false;
                    for (DesignElement ele : designElements) {
                        if (ele instanceof BlankCanvas) {
                            canvasPresent = true;
                            break;
                        }
                    }
                    if (canvasPresent) {
                        if (isWithinBoundary((Window)currentElement)) {
                            try {
                                currentElement.setStartPoint(currentElement.getStartPoint());
                                if (currentElement != null) {
                                    designElements.add(currentElement);
                                    currentElement = currentElement.getClass().getDeclaredConstructor().newInstance();
                                    currentElement.setStartPoint(lastPoint);
                                }
                            } catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Cannot add elements outside the boundary!");
                            currentElement = null;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Create a boundary to add a window!");
                        currentElement = null;
                    }
                    repaint();
                } else if (currentElement instanceof Door) {
                    boolean canvasPresent = false;
                    for (DesignElement ele : designElements) {
                        if (ele instanceof BlankCanvas) {
                            canvasPresent = true;
                            break;
                        }
                    }
                    if (canvasPresent) {
                        if (selectRoom.selectedRooms.isEmpty()) {
                            if (isWithinBounds(currentElement)) {
                                boolean doorRoomOverlap = false; 
                                for (DesignElement ele : designElements) {
                                    if (ele instanceof Room) {
                                        if (((Room)ele).getRoomType() == RoomType.Bathroom || ((Room)ele).getRoomType() == RoomType.Bedroom) {
                                            selectRoom.selectedRooms.add((Room)ele);
                                            if(isWithinRoom(currentElement)) {
                                                doorRoomOverlap = true;
                                                selectRoom.clearSelection();
                                            }
                                        }
                                        selectRoom.clearSelection();
                                    }
                                }

                                if (!doorRoomOverlap) {
                                    currentElement.setStartPoint(currentElement.getStartPoint());
                                    designElements.add(currentElement);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Bedroom and bathrooms cannot have doors to outside!");
                                    currentElement = null;

                                }
                                try {
                                    if (currentElement != null) {
                                        currentElement = currentElement.getClass().getDeclaredConstructor().newInstance();
                                        currentElement.setStartPoint(lastPoint);
                                    }
                                } catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Cannot add elements outside the boundary!");
                                currentElement = null;
                            }
                        } else {
                            Room selectedRoom = selectRoom.selectedRooms.get(0);
                            if (isWithinRoom(currentElement)) {
                                if (selectedRoom.getRoomType() == RoomType.Bedroom || selectedRoom.getRoomType() == RoomType.Bathroom) {
                                    if (isOnBoundary(currentElement)) {
                                        currentElement.setStartPoint(currentElement.getStartPoint());
                                        designElements.add(currentElement);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Bedroom and Bathrooms can't have doors to outside");
                                        currentElement = null;
                                        repaint();
                                    }
                                } else {
                                    currentElement.setStartPoint(currentElement.getStartPoint());
                                    designElements.add(currentElement);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Cannot add elements outside the room!");
                                currentElement = null;
                            }
                            try {
                                if (currentElement != null) {
                                    currentElement = currentElement.getClass().getDeclaredConstructor().newInstance();
                                    currentElement.setStartPoint(lastPoint);
                                }
                            } catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Create a boundary to add a Door!");
                        currentElement = null;
                    }
                    repaint();
                } else if (currentElement != null) {
                    try {
                        //add the preview current element to design elements to finalize its position
                        if(isWithinBounds(currentElement)){
                            if (!selectRoom.selectedRooms.isEmpty()) {
                                if (isWithinRoom(currentElement)) {
                                    currentElement.setRoom(selectRoom.selectedRooms.get(0));
                                    if (!isOverlappingWithElements(currentElement)){
                                        designElements.add(currentElement);
                                        selectRoom.selectedRooms.get(0).roomFixtures.add(currentElement);
                                        if (currentElement.isFixture()) {
                                            currentElement.setStartPoint(currentElement.getStartPoint());
                                        } else {
                                            currentElement.setStartPoint(lastPoint);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Elements cannot overlap!");
                                        currentElement = null;
                                        repaint();
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Elements can be placed only inside a room!");
                                    currentElement = null;
                                    repaint();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Select a room to add the element");
                                currentElement = null;
                                repaint();
                            }
                            
                        } else {
                            JOptionPane.showMessageDialog(null, "Elements cannot go outside the boundary!");
                            currentElement = null;
                            repaint();
                        } 
                        // Create a new instance of the current design element using reflection (for the next click)
                        if (currentElement != null) {
                            currentElement = currentElement.getClass().getDeclaredConstructor().newInstance();
                            currentElement.setStartPoint(lastPoint);
                        }
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                        ex.printStackTrace();
                    }
                }
                TabbedPanel.updateLayout();
                BottomPanel.notifyCanvasButton(designElements);

                //FUNCTIONS
                //Set the start points for the select/move functions
                if(currentFunction instanceof Select){
                    selectFunction.startPoint = lastPoint;
                    selectFunction.endPoint = selectFunction.startPoint;
                }
                if(currentFunction instanceof Move){
                    moveFunction.startDragPoint = lastPoint;
                    moveFunction.initialPoint = selectFunction.selectedElements.get(0).getStartPoint();
                    for (DesignElement element : selectFunction.selectedElements) {
                        element.saveRotationAngle(element.getRotationAngle());
                    }
                }
                if(currentFunction instanceof MoveRoom){
                    moveRoom.startDragPoint = lastPoint;
                    if (!selectRoom.selectedRooms.isEmpty()) {
                        if (!selectRoom.selectedRooms.isEmpty()) {
                            moveRoom.initialPoint = selectRoom.selectedRooms.get(0).getStartPoint();
                        }
                    }
                }
                

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Return if it wasn't the left mouse button
                if (e.getButton() != MouseEvent.BUTTON1) {return;}

                lastPoint = e.getPoint();

                //ELEMENTS
                if (currentElement instanceof BlankCanvas) {
                    ((BlankCanvas)currentElement).setEndPoint(lastPoint);
                    currentElement = null;
                }

                
                //FUNCTIONS
                //Reset points for select/move
                if(currentFunction instanceof Select){
                    selectFunction.startPoint = null;
                    selectFunction.endPoint = null;
                }
                if(currentFunction instanceof Move){
                    if (!selectFunction.selectedElements.isEmpty()) {
                        moveFunction.checkOverlap(selectFunction.selectedElements.get(0).getStartPoint());
                    }
                    moveFunction.startDragPoint = null;
                    moveFunction.initialPoint = null;
                }
                if(currentFunction instanceof MoveRoom){
                    if (!selectRoom.selectedRooms.isEmpty()) {
                        moveRoom.checkOverlap(selectRoom.selectedRooms.get(0).getStartPoint());
                    }
                    selectRoom.clearSelection();
                    moveRoom.startDragPoint = null;
                    moveRoom.initialPoint = null;
                }

                repaint();
            }

            public void mouseClicked(MouseEvent e) {
                if(currentFunction instanceof SelectRoom){
                    currentFunction.performFunction(lastPoint);
                    repaint();
                }
            }

            
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Return if it wasn't the left mouse button
                if (!SwingUtilities.isLeftMouseButton(e)) {return;}

                lastPoint = e.getPoint();

                //ELEMENTS
                if (currentElement instanceof BlankCanvas) {
                    ((BlankCanvas)currentElement).drawCanvas(lastPoint);
                    repaint();
                }

                //FUNCTIONS
                //Select/Move as mouse is dragged
                if(currentFunction instanceof Select){
                    currentFunction.performFunction(lastPoint);
                    repaint();
                }
                if(currentFunction instanceof Move){
                    currentFunction.performFunction(lastPoint);
                    repaint();
                }
                if(currentFunction instanceof MoveRoom){
                    currentFunction.performFunction(lastPoint);
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lastPoint = e.getPoint();

                //show preview of current element
                if(currentElement != null && !((currentElement instanceof BlankCanvas) || (currentElement instanceof Window) || (currentElement instanceof Door) || (currentElement instanceof Room))){
                    if (currentElement.isFixture()) {
                        if (!selectRoom.selectedRooms.isEmpty()) {
                            Room selectedRoom = selectRoom.selectedRooms.get(0);
                            Point snapPoint = getClosestPointOnRoomEdge(e.getPoint(), selectedRoom, currentElement);
                            if (snapPoint != null) {
                                currentElement.setStartPoint(snapPoint);
                            } else {
                                currentElement.setStartPoint(lastPoint);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Select a room to add the element");
                            currentElement = null;
                            repaint();
                        }
                    } else {
                        currentElement.setStartPoint(lastPoint);
                    }
                } else if (currentElement instanceof Window) {
                    BlankCanvas canvas = null;
                    for (DesignElement ele : designElements) {
                        if (ele instanceof BlankCanvas) {
                            canvas = (BlankCanvas)ele;
                        }
                    }
                    if (canvas != null) {
                        Point snapPoint = getWindowPoint(e.getPoint(), canvas, currentElement);
                        if (snapPoint != null) {
                            currentElement.setStartPoint(snapPoint);
                        } else {
                            currentElement.setStartPoint(lastPoint);
                        }
                    }
                } else if (currentElement instanceof Door) {
                    if (selectRoom.selectedRooms.isEmpty()){
                        BlankCanvas canvas = null;
                        for (DesignElement ele : designElements) {
                            if (ele instanceof BlankCanvas) {
                                canvas = (BlankCanvas)ele;
                            }
                        }
                        if (canvas != null) {
                            Point snapPoint = getWindowPoint(e.getPoint(), canvas, currentElement);
                            if (snapPoint != null) {
                                currentElement.setStartPoint(snapPoint);
                            } else {
                                currentElement.setStartPoint(lastPoint);
                            }
                        }
                    } else {
                        Room selectedRoom = selectRoom.selectedRooms.get(0);
                        Point snapPoint = getDoorPoint(e.getPoint(), selectedRoom, currentElement);
                        if (snapPoint != null) {
                            currentElement.setStartPoint(snapPoint);
                        } else {
                            currentElement.setStartPoint(lastPoint);
                        }
                    }
                } else if (currentElement instanceof Room) {
                    BlankCanvas canvas = null;
                    for (DesignElement ele : designElements) {
                        if (ele instanceof BlankCanvas) {
                            canvas = (BlankCanvas)ele;
                        }
                    }
                    currentElement.setStartPoint(getRoomPoint(lastPoint, canvas, currentElement));
                    // currentElement.setStartPoint(lastPoint);
                }

                // Repaint the panel to update the position of the current element
                repaint();
            }
        });

        //Keyboard Event Handler (was too hard to move this to separate class)
        addKeyListener(new KeyAdapter() {
            //Implementing keyboard shortcuts
            @Override
            public void keyPressed(KeyEvent e) {
                //ESCAPE, not placing a design element anymore so discard the preview current element
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    currentElement = null;
                    selectFunction.clearSelection();
                    selectRoom.clearSelection();
                    repaint();
                }

                //SPACE, unselect to finalize manipulations
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    selectFunction.clearSelection();
                    repaint();
                }

                //S, select
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    onFunctionSelected(selectFunction);
                }

                //M, move
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    onFunctionSelected(moveFunction);
                }

                if (e.getKeyCode() == KeyEvent.VK_N) {
                    onFunctionSelected(moveRoom);
                }

                //Del, remove
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    onFunctionSelected(removeFunction);
                }

                //R, Select Room
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    onFunctionSelected(selectRoom);
                }

                //T, rotate
                if (e.getKeyCode() == KeyEvent.VK_T) {
                    onFunctionSelected(rotateSlider);
                }

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (currentElement != null) {
                        currentElement.rotate(currentElement.getRotationAngle() + 90);
                        repaint();
                    }
                    else{
                        for(DesignElement ele : designElements){
                            if(ele.isSelected()){
                                ele.rotate(ele.getRotationAngle()+90);
                            }
                        }
                        repaint();
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (currentElement != null) {
                        currentElement.rotate(currentElement.getRotationAngle() - 90);
                        repaint();
                    }else{
                        for(DesignElement ele : designElements){
                            if(ele.isSelected()){
                                ele.rotate(ele.getRotationAngle() - 90);
                            }
                        }
                        repaint();
                    }
                }
                

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    onFunctionSelected(addRoom);
                }

                //CTRL+S, save floorplan to file
                if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
                    saveImage();
                }

                //CTRL+O, load floorplan from file
                if (e.getKeyCode() == KeyEvent.VK_O && e.isControlDown()) {
                    loadImage();
                }
            }
        });

        // Component Event Handler for resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeCanvas(getWidth(), getHeight());
            }
        });
    }

    //This function is continuously automatically called, and repaint() calls it manually
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Erase everything
        eraseCanvas();
        //Draw elements onto canvas
        drawElements(designElements);
        //Draw the canvas image to panel
        g.drawImage(canvas, 0, 0, null);
    }

    private void drawElements(List<DesignElement> designElements) {
        Graphics2D g2d = canvas.createGraphics();

        //Draw the design elements
        for (DesignElement element : designElements){
            element.draw(g2d);
        }

        //also draw selection rectangle if needed
        if(currentFunction instanceof Select){
            selectFunction.draw(g2d);
        }

        //also draw preview of current element if needed
        if(currentElement != null){
            currentElement.draw(g2d);
        }

        g2d.dispose();
    }

    //User clicked on the elements toolbar
    @Override
    public void onElementSelected(DesignElement element) {
        //create a copy of the element so we can draw it on our canvas
        try {
            if (!(element instanceof Room)){
                currentElement = element.getClass().getDeclaredConstructor().newInstance();
            } else {
                currentElement = element;
            }
            currentElement.setStartPoint(lastPoint);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
        }
        currentFunction = null;

        //clear selection and hide sliders
        selectFunction.clearSelection();
        
        //Request focus for the panel (move focus away from element bar)
        requestFocusInWindow();
    } 

    //User clicked on the functions toolbar
    @Override
    public void onFunctionSelected(ManipulationFunction function) {
        currentElement = null;
        currentFunction = function;

        //Perform remove immediately on click
        if(currentFunction instanceof Remove){
            currentFunction.performFunction(lastPoint);
            currentFunction = null;
        }

        if(currentFunction instanceof Rotate){      
            currentFunction.performFunction(lastPoint);
            currentFunction = null;
        }

        if (currentFunction instanceof AddRoom) {
            
            currentFunction.performFunction(lastPoint);
            currentFunction = null;
        }

        if (currentFunction instanceof DeleteBoundary) {
            currentFunction.performFunction(lastPoint);
            currentFunction = null;
        }

        //Request focus for the panel (move focus away from function bar)
        requestFocusInWindow();
        //Hide current element preview (current element is now null, we are manipulating not placing)
        repaint();
    }

    //Resize canvas if panel resized
    private void resizeCanvas(int width, int height) {
        BufferedImage newCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newCanvas.createGraphics();
        g2d.drawImage(canvas, 0, 0, null);
        g2d.dispose();
        canvas = newCanvas;
        repaint();
    }

    //Only erasing the canvas image, Keep design elements
    public void eraseCanvas() {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.dispose();
    }

    //Erase the canvas image and design elements
    public void clearCanvas() {
        eraseCanvas();
        if(designElements != null){
            selectFunction.clearSelection();
            designElements.clear();
        }
        repaint();
    }

    // Check for room overlap
    public boolean isOverlapping(Room newRoom) {
        // Convert newRoom's polygon bounds to an Area
        Area newRoomArea = new Area(newRoom.getBounds());
        
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

    public boolean isWithinBounds(DesignElement designElement) {
        Point designElementPoint = designElement.getStartPoint();
        int graphicHeight = designElement.getHeight();
        int graphicWidth = designElement.getWidth();
        int graphicX = designElementPoint.x - graphicWidth/2;
        int graphicY = designElementPoint.y - graphicHeight/2;
        int rectX, rectY, rectHeight, rectWidth;
    
        for (DesignElement ele : designElements) {
            if (ele instanceof BlankCanvas) {
                rectX = ele.getStartPoint().x - 1;
                rectY = ele.getStartPoint().y - 1;
                rectHeight = ((BlankCanvas)ele).getFrameHeight() + 2;
                rectWidth = ((BlankCanvas)ele).getFrameWidth() + 2;
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
        if (!selectRoom.selectedRooms.isEmpty()) {
            Room selectedRoom = selectRoom.selectedRooms.get(0);
            int roomX = selectedRoom.getStartPoint().x - selectedRoom.getWidth()/2 - 1;
            int roomY = selectedRoom.getStartPoint().y - selectedRoom.getHeight()/2 - 1;
            int roomWidth = selectedRoom.getWidth() + 2;
            int roomHeight = selectedRoom.getHeight() + 2;
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
                        xPoint = selectedRoomX - width + currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY - height + currentElement.getHeight()/2;
                        element.rotate(0);
                    }
                    
                } else {
                    // Point D
                    if (dx1 <= dy2) {
                        xPoint = selectedRoomX - width + currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY + height - currentElement.getHeight()/2;
                        element.rotate(180);
                    }
                }
            } else {
                if (dy1 <= dy2) {
                    // Point B
                    if (dx2 <= dy1) {
                        xPoint = selectedRoomX + width - currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY - height + currentElement.getHeight()/2;
                        element.rotate(0);
                    }
                } else {
                    // Point C
                    if (dx2 <= dy2) {
                        xPoint = selectedRoomX + width - currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY + height - currentElement.getHeight()/2;
                        element.rotate(180);
                    }
                }
            }
        }
    
        return new Point(xPoint, yPoint);
    }

    public boolean isOnBoundary(DesignElement designElement) {
        Point designElementPoint = designElement.getStartPoint();
        int graphicHeight = designElement.getHeight();
        int graphicWidth = designElement.getWidth();
        int graphicX = designElementPoint.x - graphicWidth/2;
        int graphicY = designElementPoint.y - graphicHeight/2;
        int rectX, rectY, rectHeight, rectWidth;
    
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

    
    

    //Save floorplan to file
    public void saveImage() {
        //clear selections before saving
        selectFunction.clearSelection();
        selectRoom.clearSelection();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                // Serialize the design elements and canvas
                oos.writeObject(designElements);
                repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //Load floorplan from file
    @SuppressWarnings("unchecked")
    public void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Image");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                // Deserialize the design elements
                designElements = (List<DesignElement>) ois.readObject();
                repaint();
                TabbedPanel.updateLayout();
                BottomPanel.notifyCanvasButton(designElements);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Point getWindowPoint(Point e, BlankCanvas canvas, DesignElement element) {
        int dx1 = e.x - (canvas.getStartPoint().x - 1);
        int dx2 = canvas.getStartPoint().x + canvas.getFrameWidth() + 1 - e.x;
        int dy1 = e.y - (canvas.getStartPoint().y - 1);
        int dy2 = canvas.getStartPoint().y + canvas.getFrameHeight() + 1 - e.y;
        int xPoint = 0;
        int yPoint = 0;
        if (dx1 >= 0 && dx2 >= 0 && dy1 >= 0 && dy2 >= 0) {
            if (dx1 <= dx2) {
                if (dy1 <= dy2) {
                    // Point A
                    if (dx1 <= dy1) {
                        xPoint = canvas.getStartPoint().x - 1 + currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = canvas.getStartPoint().y - 1 + currentElement.getHeight()/2;
                        element.rotate(0);
                    }
                    
                } else {
                    // Point D
                    if (dx1 <= dy2) {
                        xPoint = canvas.getStartPoint().x - 1 + currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = canvas.getStartPoint().y + canvas.getFrameHeight() + 1 - currentElement.getHeight()/2;
                        element.rotate(180);
                    }
                }
            } else {
                if (dy1 <= dy2) {
                    // Point B
                    if (dx2 <= dy1) {
                        xPoint = canvas.getStartPoint().x + canvas.getFrameWidth() + 1 - currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = canvas.getStartPoint().y - 1 + currentElement.getHeight()/2;
                        element.rotate(0);
                    }
                } else {
                    // Point C
                    if (dx2 <= dy2) {
                        xPoint = canvas.getStartPoint().x + canvas.getFrameWidth() + 1 - currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = canvas.getStartPoint().y + canvas.getFrameHeight() + 1 - currentElement.getHeight()/2;
                        element.rotate(180);
                    }
                }
            }
        }
    
        return new Point(xPoint, yPoint);
    }

    public Point getRoomPoint(Point e, BlankCanvas canvas, DesignElement element) {
        int x1 = e.x - element.getWidth()/2 - 1;
        int y1 = e.y - element.getHeight()/2 - 1;
        int x2 = e.x + element.getWidth()/2 + 1;
        int y2 = e.y + element.getHeight()/2 + 1;
        int dx1 = e.x - (canvas.getStartPoint().x);
        int dx2 = canvas.getStartPoint().x + canvas.getFrameWidth() - e.x;
        int dy1 = e.y - (canvas.getStartPoint().y - 1);
        int dy2 = canvas.getStartPoint().y + canvas.getFrameHeight() - e.y;
        int xPoint = e.x;
        int yPoint = e.y;
        if (dx1 >= 0 && dx2 >= 0 && dy1 >= 0 && dy2 >= 0) {
            if (dx1 <= dx2) {
                if (dy1 <= dy2) {
                    // Point A
                    if (dx1 <= dy1) {
                        if (x1 - canvas.getStartPoint().x < 10) {
                            xPoint = canvas.getStartPoint().x + element.getWidth()/2;
                            yPoint = e.y;
                        }
                    } else {
                        if (y1 - canvas.getStartPoint().y < 10) {
                            xPoint = e.x;
                            yPoint = canvas.getStartPoint().y + element.getHeight()/2;
                        }
                    }
                    if (x1 - canvas.getStartPoint().x < 10 && y1 - canvas.getStartPoint().y < 10) {
                        xPoint = canvas.getStartPoint().x + element.getWidth()/2;
                        yPoint = canvas.getStartPoint().y + element.getHeight()/2;
                    }
                } else {
                    // Point D
                    if (dx1 <= dy2) {
                        if (x1 - canvas.getStartPoint().x < 10) {
                            xPoint = canvas.getStartPoint().x + element.getWidth()/2;
                            yPoint = e.y;
                        }
                    } else {
                        if (canvas.getStartPoint().y + canvas.getHeight() + 1 - y2 < 10) {
                            xPoint = e.x;
                            yPoint = canvas.getStartPoint().y + canvas.getFrameHeight() - element.getHeight()/2;
                        }
                    }
                    if (x1 - canvas.getStartPoint().x < 10 && canvas.getStartPoint().y + canvas.getHeight() + 1 - y2 < 10) {
                        xPoint = canvas.getStartPoint().x + element.getWidth()/2;
                        yPoint = canvas.getStartPoint().y + canvas.getFrameHeight() - element.getHeight()/2;
                    }
                }
            } else {
                if (dy1 <= dy2) {
                    // Point B
                    if (dx2 <= dy1) {
                        if (canvas.getStartPoint().x + canvas.getFrameWidth() + 1 - x2 < 10) {
                            xPoint = canvas.getStartPoint().x + canvas.getFrameWidth() - element.getWidth()/2;
                            yPoint = e.y;
                        }
                    } else {
                        if (y1 - canvas.getStartPoint().y < 10) {
                            xPoint = e.x;
                            yPoint = canvas.getStartPoint().y + element.getHeight()/2;
                        }
                    }
                    if (canvas.getStartPoint().x + canvas.getFrameWidth() + 1 - x2 < 10 && y1 - canvas.getStartPoint().y < 10) {
                        xPoint = canvas.getStartPoint().x + canvas.getFrameWidth() - element.getWidth()/2;
                        yPoint = canvas.getStartPoint().y + element.getHeight()/2;
                    }
                } else {
                    // Point C
                    if (dx2 <= dy2) {
                        if (canvas.getStartPoint().x + canvas.getFrameWidth() + 1 - x2 < 10) {
                            xPoint = canvas.getStartPoint().x + canvas.getFrameWidth() - element.getWidth()/2;
                            yPoint = e.y;
                        }
                    } else {
                        if (canvas.getStartPoint().y + canvas.getFrameHeight() + 1 - y2 < 10) {
                            xPoint = e.x;
                            yPoint = canvas.getStartPoint().y + canvas.getFrameHeight() - element.getHeight()/2;
                        }
                    }
                    if (canvas.getStartPoint().x + canvas.getFrameWidth() + 1 - x2 < 10 && canvas.getStartPoint().y + canvas.getFrameHeight() + 1 - y2 < 10) {
                        xPoint = canvas.getStartPoint().x + canvas.getFrameWidth() - element.getWidth()/2;
                        yPoint = canvas.getStartPoint().y + canvas.getFrameHeight() - element.getHeight()/2;
                    }
                }
            }
        }
    
        return new Point(xPoint, yPoint);
    }

    public boolean isWithinBoundary(Window window) {
        Point designElementPoint = window.getStartPoint();
        int graphicHeight = window.getHeight();
        int graphicWidth = window.getWidth();
        int graphicX = designElementPoint.x - graphicWidth/2;
        int graphicY = designElementPoint.y - graphicHeight/2;
        int rectX, rectY, rectHeight, rectWidth;
    
        for (DesignElement ele : designElements) {
            if (ele instanceof BlankCanvas) {
                rectX = ele.getStartPoint().x - 1;
                rectY = ele.getStartPoint().y - 1;
                rectHeight = ((BlankCanvas)ele).getFrameHeight() + 2;
                rectWidth = ((BlankCanvas)ele).getFrameWidth() + 2;
                return graphicX >= rectX &&
                       graphicY >= rectY &&
                       (graphicX + graphicWidth) <= (rectX + rectWidth) &&
                       (graphicY + graphicHeight) <= (rectY + rectHeight);
            } 
        }
        return false;
    }

    public Point getDoorPoint(Point e, DesignElement selectedRoom, DesignElement element) {
        int selectedRoomX = selectedRoom.getStartPoint().x;
        int selectedRoomY = selectedRoom.getStartPoint().y;
        int width = selectedRoom.getWidth()/2 + 1;
        int height = selectedRoom.getHeight()/2 + 1;

        int dx1 = e.x - (selectedRoomX - width);
        int dx2 = (selectedRoomX + width) - e.x;
        int dy1 = e.y - (selectedRoomY - height);
        int dy2 = (selectedRoomY + height) - e.y;
        int xPoint = 0;
        int yPoint = 0;
        if (dx1 >= 0 && dx2 >= 0 && dy1 >= 0 && dy2 >= 0) {
            if (dx1 <= dx2) {
                if (dy1 <= dy2) {
                    // Point A
                    if (dx1 <= dy1) {
                        xPoint = selectedRoomX - width + currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY - height + currentElement.getHeight()/2;
                        element.rotate(0);
                    }
                    
                } else {
                    // Point D
                    if (dx1 <= dy2) {
                        xPoint = selectedRoomX - width + currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(270);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY + height - currentElement.getHeight()/2;
                        element.rotate(180);
                    }
                }
            } else {
                if (dy1 <= dy2) {
                    // Point B
                    if (dx2 <= dy1) {
                        xPoint = selectedRoomX + width - currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY - height + currentElement.getHeight()/2;
                        element.rotate(0);
                    }
                } else {
                    // Point C
                    if (dx2 <= dy2) {
                        xPoint = selectedRoomX + width - currentElement.getWidth()/2;
                        yPoint = e.y;
                        element.rotate(90);
                    } else {
                        xPoint = e.x;
                        yPoint = selectedRoomY + height - currentElement.getHeight()/2;
                        element.rotate(180);
                    }
                }
            }
        }
    
        return new Point(xPoint, yPoint);
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

    //Getters
    public List<DesignElement> getDesignElements(){
        return designElements;
    }

    public Select getSelect(){
        return selectFunction;
    }

    public Move getMove(){
        return moveFunction;
    }

    public Remove getRemove(){
        return removeFunction;
    }

    public Rotate getRotate(){
        return rotateSlider;
    }

    public Resize getResize(){
        return resizeSlider;
    }

    public AddRoom getAddRoom() {
        return addRoom;
    }

    public DeleteBoundary getDeleteBoundary() {
        return deleteBoundary;
    }

    public SelectRoom getSelectRoom() {
        return selectRoom;
    }

    public MoveRoom getMoveRoom() {
        return moveRoom;
    }

    // setters
    public void setDesignElements(List <DesignElement> designElements){
        this.designElements = designElements;
    }
}
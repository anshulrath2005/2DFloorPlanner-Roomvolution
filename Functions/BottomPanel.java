package Functions;

import Assets.BlankCanvas;
import Assets.DesignElement;
import FloorPlan.DrawingPanel;
import FloorPlan.FunctionSelectedObserver;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class BottomPanel extends JPanel {
    private List<FunctionSelectedObserver> observers = new ArrayList<>();
    static JButton deleteBoundary;
    static JButton createBoundary;

    public BottomPanel(DrawingPanel canvasPanel) {
        setLayout(new FlowLayout());
        this.addObserver(canvasPanel);
        //add functions to this
        this.addFunction(canvasPanel.getAddRoom());
        this.addFunction(canvasPanel.getSelect());
        this.addFunction(canvasPanel.getSelectRoom());
        this.addFunction(canvasPanel.getMove());
        this.addFunction(canvasPanel.getMoveRoom());
        this.addFunction(canvasPanel.getRemove());
        this.addFunction(canvasPanel.getRotate());
        createBoundary = new JButton("Create Boundary");
        this.add(createBoundary);
        createBoundary.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                canvasPanel.onElementSelected(new BlankCanvas());
                createBoundary.setEnabled(false);
            }
            
        });
        deleteBoundary = new JButton("Delete Boundary");
        this.add(deleteBoundary);
        deleteBoundary.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                canvasPanel.onFunctionSelected(canvasPanel.getDeleteBoundary());
                createBoundary.setEnabled(true);
            }
            
        });
    }

    public void addFunction(ManipulationFunction function) {
        //Custom Button Names with Keyboard Shortcut Hints
        String buttonName;
        switch (function.getClass().getSimpleName()) {
            case "Select":
                buttonName = "Select   (S)";
                break;
            case "Move":
                buttonName = "Move   (M)";
                break;
            case "Remove":
                buttonName = "Delete   (Del)";
                break;
            case "Resize":
                buttonName = "Resize   (Z)";
                break;
            case "Rotate":
                buttonName = "Rotate   (T)";
                break;
            case "AddRoom":
                buttonName = "Add Room   (A)";
                break;
            case "SelectRoom":
                buttonName = "Select Room   (R)";
                break;
            case "MoveRoom":
                buttonName = "Move Room   (N)";
                break;
            default:
                buttonName = "Unknown";
                break;
        }
        JButton button = new JButton(buttonName);

        //Whenever button is clicked, observers (canvasPanel) know what function was picked
        button.addActionListener(e -> notifyObservers(function));
        add(button);
    }

    public void addObserver(FunctionSelectedObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(ManipulationFunction function) {
        for (FunctionSelectedObserver observer : observers) {
            observer.onFunctionSelected(function);
        }
    }

    public static void notifyCanvasButton(List <DesignElement> elements) {
        deleteBoundary.setEnabled(elements.size() <= 1);
        createBoundary.setEnabled(elements.size() == 0);
    }
}
package Assets;
import FloorPlan.ElementSelectedObserver;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Furniture extends JPanel {
    private List<ElementSelectedObserver> observers = new ArrayList<>();

    public Furniture() {
        setLayout(new GridLayout(5, 1));
        
        addElement(new Bed());
        addElement(new Chair());
        addElement(new DiningSet());
        
        addElement(new Sofa());
        addElement(new Table());
        
    }

    private void addElement(DesignElement element) {
        //Custom Button Names
        String buttonName = element.getClass().getSimpleName();
        if(buttonName.equals("DiningSet")){
            buttonName = "Dining Set";
        }
        JButton button = new JButton(buttonName);

        // Set a preferred size for the button
        button.setPreferredSize(new Dimension(60, 40));
        button.setFont(new Font("Roboto",Font.PLAIN,20));
        button.setFocusable(false);
        
        // Set a margin to provide padding around the text
        button.setMargin(new Insets(3, 3, 3, 3));

        //Whenever button is clicked, observers (drawingPanel) know what design element was picked
        button.addActionListener(e -> notifyObservers(element));

        add(button);
    }

    public void addObserver(ElementSelectedObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(DesignElement element) {
        for (ElementSelectedObserver observer : observers) {
            observer.onElementSelected(element);
        }
    }
}
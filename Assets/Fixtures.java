package Assets;
import FloorPlan.ElementSelectedObserver;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Fixtures extends JScrollPane {
    private List<ElementSelectedObserver> observers = new ArrayList<>();
    JPanel panel;

    public Fixtures() {
        panel = new JPanel();

        panel.setLayout(new GridLayout(7, 1));
        
        addElement(new Sink());
        addElement(new Stove());
        addElement(new Commode());
        addElement(new WashBasin());
        addElement(new Door());
        addElement(new Window());
        addElement(new Shower());

        this.getVerticalScrollBar().setUnitIncrement(20);
        this.setViewportView(panel);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    private void addElement(DesignElement element) {
        //Custom Button Names
        String buttonName = element.getClass().getSimpleName();
        if(buttonName.equals("WashBasin")){
            buttonName = "Wash Basin";
        }
        
        JButton button = new JButton(buttonName);

        // Set a preferred size for the button
        button.setPreferredSize(new Dimension(150, 60));
        button.setFont(new Font("Roboto",Font.PLAIN,20));
        button.setFocusable(false);

        // Set a margin to provide padding around the text
        button.setMargin(new Insets(3, 3, 3, 3));

        //Whenever button is clicked, observers (drawingPanel) know what design element was picked
        button.addActionListener(e -> notifyObservers(element));

        panel.add(button);
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

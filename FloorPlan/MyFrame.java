package FloorPlan;

import Functions.BottomPanel;

import java.awt.*;
import javax.swing.*;

public class MyFrame extends JFrame{
    JFrame frame;
    JPanel bottomPanel;
    DrawingPanel canvasPanel;
    JTabbedPane tabbedPanel;
    public MyFrame(){
        frame = new JFrame();
        frame.setTitle("Roomvolution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        //frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setSize(1300, 700);

        canvasPanel = new DrawingPanel(1300, 700);
        bottomPanel = new BottomPanel(canvasPanel);
        tabbedPanel = new TabbedPanel(canvasPanel);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(tabbedPanel, BorderLayout.WEST);
        frame.add(canvasPanel, BorderLayout.CENTER);
        frame.add(new JPanel(), BorderLayout.NORTH);
        frame.add(new JPanel(), BorderLayout.EAST);

        frame.setJMenuBar(new MenuBarFrame(canvasPanel)); // Menu Bar
        

        // put the frame in the middle of the display
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setVisible(true);
    }
}
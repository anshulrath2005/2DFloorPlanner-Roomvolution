package FloorPlan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.*;

import Assets.DesignElement;
import Functions.BottomPanel;

public class Homepage extends JFrame {

    JFrame frame;

    JButton newProject;
    JButton openProject;
    JLabel textLabel;
    JPanel tempPanel;
    ImageIcon icon1;

    Homepage() {
        frame = new JFrame();
        frame.setTitle("Roomvolution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);

        tempPanel = new JPanel();
        newProject = new JButton();
        openProject = new JButton();
        textLabel = new JLabel();
        icon1 = new ImageIcon("FloorPlan/img1.jpg");
        
        tempPanel.setLayout(null);
        tempPanel.setSize(new Dimension(859, 396));

        // Text Label
        textLabel = new JLabel();
        textLabel.setText("Planning Redesigned");
        textLabel.setFont(new Font("Roboto", Font.PLAIN, 35));
        textLabel.setBounds(0, 113, 433, 50);

        // New Project Button
        newProject.setText("New Project");
        newProject.setFont(new Font("Roboto", Font.PLAIN, 20));
        newProject.setBounds(0, 173, 344, 50);
        newProject.setFocusable(false);
        newProject.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == newProject) {
                    new MyFrame();
                    frame.dispose();
                }
            }
            
        });

        // Open Project Button
        openProject.setText("Open Project");
        openProject.setFont(new Font("Roboto", Font.PLAIN, 20));
        openProject.setBounds(0, 233, 344, 50);
        openProject.setFocusable(false);
        openProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyFrame openFrame = new MyFrame(); // Initialize the new frame
        
                if (e.getSource() == openProject) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Open Image");
                    int openResponse = fileChooser.showOpenDialog(null);
        
                    if (openResponse == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try (FileInputStream fis = new FileInputStream(file);
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
        
                            // Deserialize the design elements
                            @SuppressWarnings("unchecked")
                            List<DesignElement> designElements = (List<DesignElement>) ois.readObject();
        
                            // Set the design elements in the canvas panel
                            openFrame.canvasPanel.setDesignElements(designElements);
                            TabbedPanel.updateLayout();
                            BottomPanel.notifyCanvasButton(designElements);
                            // Repaint the canvas to reflect the new elements
                            openFrame.canvasPanel.repaint();
        
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        // Image Panel
        JLabel imgLabel = new JLabel();
        imgLabel.setIcon(icon1);
        imgLabel.setBounds(463,0, 400, 400);

        // tempPanel
        tempPanel.add(textLabel);
        tempPanel.add(newProject);
        tempPanel.add(openProject);
        tempPanel.add(imgLabel);

        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int panelWidth = tempPanel.getWidth();
                int panelHeight = tempPanel.getHeight();
                int frameWidth = frame.getWidth();
                int frameHeight = frame.getHeight();

                // Center tempPanel within the frame
                tempPanel.setBounds((frameWidth - panelWidth) / 2, (frameHeight - panelHeight) / 2, 859, 396);
            }
        });

        frame.setJMenuBar(new MenuBarHome()); // Menu Bar

        frame.add(tempPanel);
        // put the frame in the middle of the display
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setVisible(true);
    }
}

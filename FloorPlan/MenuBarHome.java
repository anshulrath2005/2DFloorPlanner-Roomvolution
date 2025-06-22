package FloorPlan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.*;
import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import Assets.DesignElement;
import Functions.BottomPanel;

public class MenuBarHome extends JMenuBar {
    MenuBarHome() {
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        this.add(fileMenu);
        this.add(editMenu);
        this.add(helpMenu);

        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(exitItem);  
        
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MyFrame();
            }
        });

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyFrame openFrame = new MyFrame(); // Initialize the new frame
        
                if (e.getSource() == openItem) {
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
        
                            // Repaint the canvas to reflect the new elements
                            openFrame.canvasPanel.repaint();
                            TabbedPanel.updateLayout();
                            BottomPanel.notifyCanvasButton(designElements);
        
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == exitItem) {
                    System.exit(0);
                }
            }
        });

        fileMenu.setMnemonic(KeyEvent.VK_F); 
        editMenu.setMnemonic(KeyEvent.VK_E); 
        helpMenu.setMnemonic(KeyEvent.VK_H); 

        // Mnemonics for Items in File menu
        newItem.setMnemonic(KeyEvent.VK_N); // n for new
        openItem.setMnemonic(KeyEvent.VK_O); // o for open
        exitItem.setMnemonic(KeyEvent.VK_E); // e for exit
    }
}

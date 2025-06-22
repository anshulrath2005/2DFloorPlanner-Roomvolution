package FloorPlan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import Assets.DesignElement;
import Functions.BottomPanel;

public class MenuBarFrame extends JMenuBar {
    MenuBarFrame(DrawingPanel canvasPanel) {
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        this.add(fileMenu);
        this.add(editMenu);
        this.add(helpMenu);

        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
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
                if (e.getSource() == openItem) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Open Image");
                    int openResponse = fileChooser.showOpenDialog(null);
                    if (openResponse == JFileChooser.APPROVE_OPTION) {
                        MyFrame openFrame = new MyFrame();
                        File file = fileChooser.getSelectedFile();
                        try (FileInputStream fis = new FileInputStream(file);
                            ObjectInputStream ois = new ObjectInputStream(fis)) {
                            // Deserialize the design elements
                            @SuppressWarnings("unchecked")
                            List <DesignElement> designElements = (List<DesignElement>) ois.readObject();
                            openFrame.canvasPanel.setDesignElements(designElements);
                            TabbedPanel.updateLayout();
                            BottomPanel.notifyCanvasButton(designElements);
                            openFrame.canvasPanel.repaint();
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        saveItem.addActionListener(e -> canvasPanel.saveImage());

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
        saveItem.setMnemonic(KeyEvent.VK_S); // s for save
        exitItem.setMnemonic(KeyEvent.VK_E); // e for exit
    }
}

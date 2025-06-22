package FloorPlan;

import Assets.DesignElement;
import Assets.Fixtures;
import Assets.Furniture;
import Assets.Room;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TabbedPanel extends JTabbedPane {
    JPanel layout;
    JPanel assets;
    public static DrawingPanel canvasPanel;
    public static DefaultTableModel model;
    public static JTable jt;

    TabbedPanel(DrawingPanel canvasPanel) {
        TabbedPanel.canvasPanel = canvasPanel;
        layout = new JPanel();
        assets = new JPanel();

        // Layout Panel
        model = new DefaultTableModel();
        model.addColumn("Element");
        model.addColumn("Type");
        jt = new JTable(model);
		JScrollPane js = new JScrollPane(jt,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		js.setPreferredSize(new Dimension(180, 800));
        layout.add(js);

        // Assets Panel
        assets.setLayout(new GridLayout(2, 1, 0, 10));
        // Furniture
        JPanel furniture = new JPanel();
        Furniture furns = new Furniture();
        JLabel furnLabel = new JLabel("Furniture");
        
        furniture.setLayout(new BorderLayout());

        furnLabel.setHorizontalAlignment(CENTER);
        furnLabel.setVerticalAlignment(CENTER);
        
        furns.setBorder(BorderFactory.createEtchedBorder());
        
        furnLabel.setPreferredSize(new Dimension(200,30));
        furnLabel.setFont(new Font("Roboto",Font.BOLD,20));
        furns.addObserver(canvasPanel);

        

        furniture.setBorder(BorderFactory.createEtchedBorder());
        furniture.add(furnLabel, BorderLayout.NORTH);
        furniture.add(furns, BorderLayout.CENTER);

        

        // Fixtures
        JPanel fixture = new JPanel();
        Fixtures fixs = new Fixtures();
        JLabel fixLabel = new JLabel("Fixtures");

        fixture.setLayout(new BorderLayout());

        fixLabel.setHorizontalAlignment(CENTER);
        fixLabel.setVerticalAlignment(CENTER);

        fixs.setBorder(BorderFactory.createEtchedBorder());
        fixs.addObserver(canvasPanel);
        fixLabel.setPreferredSize(new Dimension(200,30));
        fixLabel.setFont(new Font("Roboto",Font.BOLD,20));

        
        fixture.setBorder(BorderFactory.createEtchedBorder());
        fixture.add(fixLabel, BorderLayout.NORTH);
        fixture.add(fixs, BorderLayout.CENTER);

        assets.add(furniture);
        assets.add(fixture);

        //set Titles
        this.add("Elements", assets);
        this.add("Layout", layout);

        //setTitleStyle
        JLabel layoutset = new JLabel(this.getTitleAt(0));
        layoutset.setFont(new Font("Roboto",Font.BOLD,22));
        this.setTabComponentAt(0, layoutset);

        JLabel assetsset = new JLabel(this.getTitleAt(1));
        assetsset.setFont(new Font("Roboto",Font.BOLD,22));
        this.setTabComponentAt(1, assetsset);
        this.setPreferredSize(new Dimension(200, 350));
    }

    public static void updateLayout() {
        model.getDataVector().removeAllElements();
		model.fireTableDataChanged();
        for (DesignElement ele : canvasPanel.getDesignElements()) {
            if (ele instanceof Room) {
                model.addRow(new Object[]{((Room)ele).getRoomType() + "", ele.getClass().getSimpleName() + ""});
                for (DesignElement designElement : ((Room)ele).roomFixtures) {
                    String str = designElement.isFixture() ? "Fixture" : "Furniture";
                    model.addRow(new Object[]{designElement.getClass().getSimpleName() + "", str});
                }
                model.addRow(new Object[]{"", ""});
            }
        }
    }
}

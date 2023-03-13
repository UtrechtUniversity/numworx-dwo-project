package fi.beans.numworxlf;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class JTabbedPaneFrame {

  public static void main(String[] args) {
    JFrame main = new JFrame("tabbedpane");
    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("een", new JPanel());
    tabs.addTab("twee", new JPanel());
    tabs.addTab("drie", new JPanel());
    main.setContentPane(tabs);
    main.pack();
    main.show();
    
  }

}

package fi.dwo.dwojapplet.gui.numworx;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScrollPaneFrame {

  public static void main(String[] args) {
    JFrame frame = new JFrame("scrollpane");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel view = new JPanel();
    view.setBackground(Color.white);
    view.setSize(1024, 1024);
    view.setPreferredSize(view.getSize());
    view.setMaximumSize(view.getSize());
    view.setMinimumSize(view.getSize());
    frame.setContentPane(new JScrollPane(view));
    frame.pack();
    frame.setSize(200,300);
    frame.show();
  }

}

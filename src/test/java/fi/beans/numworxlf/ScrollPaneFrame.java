package fi.beans.numworxlf;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fi.beans.numworxlf.JScrollPane;

public class ScrollPaneFrame {

  public static void main(String[] args) {
    JFrame frame = new JFrame("scrollpane");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel view = new JPanel();
    view.setBackground(Color.gray);
    view.setSize(1024, 1024);
    view.setPreferredSize(view.getSize());
    view.setMaximumSize(view.getSize());
    view.setMinimumSize(view.getSize());
    JScrollPane pane = new JScrollPane(view);
    pane.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
	frame.setContentPane(pane);
    frame.pack();
    frame.setSize(200,300);
    frame.show();
  }

}

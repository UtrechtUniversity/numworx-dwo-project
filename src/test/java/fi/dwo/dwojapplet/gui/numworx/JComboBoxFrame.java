package fi.dwo.dwojapplet.gui.numworx;

import java.awt.FlowLayout;

import javax.swing.JFrame;

public class JComboBoxFrame {

  public static void main(String[] args) {
    JFrame main = new JFrame("combobox");
    
    JComboBox<String> box = new JComboBox<>(new String[] { "1", "twee", "3"} );
    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    main.getContentPane().add(box);
    
    javax.swing.JComboBox<String> org = new javax.swing.JComboBox<>(new String[] { "org 1", "org twee"});
    main.getContentPane().add(org);
    main.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    main.pack();
    main.show();
  }

}

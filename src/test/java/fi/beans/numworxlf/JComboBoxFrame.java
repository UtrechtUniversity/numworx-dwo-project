package fi.beans.numworxlf;

import java.awt.FlowLayout;

import javax.swing.JFrame;

import fi.beans.numworxlf.JComboBox;

public class JComboBoxFrame {

  public static void main(String[] args) {
    JFrame main = new JFrame("combobox");
    
    JComboBox<String> box = new JComboBox<>(new String[] { "1", "twee", "3"} );
    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    main.getContentPane().add(box);
    JComboBox<String> box1 = new JComboBox<>(new String[] { "1", "twee", "3"} );
    box1.setEnabled(false);
    main.getContentPane().add(box1);
    
    javax.swing.JComboBox<String> org = new javax.swing.JComboBox<>(new String[] { "org 1", "org twee"});
    main.getContentPane().add(org);
    main.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    main.pack();
    main.show();
  }

}

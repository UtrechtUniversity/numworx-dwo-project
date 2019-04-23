package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class ShowLeerdomein extends JFrame {

  public ShowLeerdomein() throws HeadlessException {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    LeerdomeinEditPanel panel = new LeerdomeinEditPanel();
    setContentPane(panel);
    pack();
  }


  public static void main(String[] args) {

    new ShowLeerdomein().show();

  }

}

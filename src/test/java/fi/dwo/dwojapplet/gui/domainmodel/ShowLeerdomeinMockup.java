package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class ShowLeerdomeinMockup extends JFrame {

  public ShowLeerdomeinMockup() throws HeadlessException {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    LeerdomeinMockupPanel panel = new LeerdomeinMockupPanel();
    setContentPane(panel);
    pack();
  }


  public static void main(String[] args) {

    new ShowLeerdomeinMockup().show();

  }

}


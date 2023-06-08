package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class SelectKoppelingGRMockup extends JFrame {

  public SelectKoppelingGRMockup() throws HeadlessException {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    KoppelingGRPanel panel = new KoppelingGRPanel();
    setContentPane(panel);
    pack();
  }


  public static void main(String[] args) {

    new SelectKoppelingGRMockup().show();

  }

}


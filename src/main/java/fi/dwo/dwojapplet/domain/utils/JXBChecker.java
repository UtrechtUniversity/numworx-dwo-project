package fi.dwo.dwojapplet.domain.utils;

import javax.swing.JApplet;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.dwojapplet.domain.DwoHelper;

public class JXBChecker {
  
  static final String CLASS = "com.teamdev.jxbrowser.engine.Engine";

  static final String[] message = {"Deze applicatie is verouderd.","Ga naar https://www.numworx.nl/ voor een update!"};  
  private JApplet applet;
  
  
  public JXBChecker(JApplet applet) {
    this.applet = applet;
  }

  public void check() {
    
    if (!DwoHelper.isTest()) {
      try {
        Class.forName(CLASS);
      } catch(Exception oops) {
        JOptionPane.showMessageDialog(applet, message);
      }
    }
  }

}

package fi.beans.jxbchecker;

import javax.swing.JApplet;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.dwojapplet.domain.DwoHelper;

public class JXBChecker {
  
  private static final String CLASS = "com.teamdev.jxbrowser.engine.Engine";

  private static final String[] message = {"Deze applicatie is verouderd.",
                                   "Ga naar https://www.numworx.nl/help/downloads/ voor een update!",
                                   "",
                                   "This application is outdated.",
                                   "Go to https://www.numworx.nl/help/downloads/ for an update!"
                                  };  
  private JApplet applet;
  
  
  public JXBChecker(JApplet applet) {
    this.applet = applet;
  }

  public void check() {
    
    if (true) {
      try {
        Class.forName(CLASS);
        
//        String version = com.teamdev.jxbrowser.VersionInfo.version();
//        if ("7.2".equals(version)) throw new IllegalArgumentException();
        
      } catch(Exception oops) {
        JOptionPane.showMessageDialog(applet, message);
      }
    }
  }

}

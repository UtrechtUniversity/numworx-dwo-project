package fi.beans.mainframe;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

public interface AppletContext extends java.applet.AppletContext {

  @Override @Deprecated
  default Applet getApplet(String name) {
    return null;
  }

  @Override @Deprecated
  default Enumeration<Applet> getApplets() {
    return Collections.emptyEnumeration();
  }

  @Override @Deprecated
  default AudioClip getAudioClip(URL url) {
      return java.applet.Applet.newAudioClip(url);
  }

}

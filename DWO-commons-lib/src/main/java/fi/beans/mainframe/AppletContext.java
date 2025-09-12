package fi.beans.mainframe;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

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

  // AppletContext interface without java.applet package
  
	default Image getImage(URL url) {
		return java.awt.Toolkit.getDefaultToolkit().createImage(url);
	}
	
	default void showDocument(URL url) {
	}
	
	default void showDocument(URL url, String target) {
	}
	
	default void showStatus(String status) {
	}
	
	default void setStream(String key, InputStream stream) throws IOException {
		throw new IOException("Not Implemented");
	}
	
	default InputStream getStream(String key) {
		return null;
	}
	
	default Iterator<String> getStreamKeys() {
		return Collections.emptyIterator();
	}

}

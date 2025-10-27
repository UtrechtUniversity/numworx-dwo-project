package fi.beans.mainframe;

import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("serial")
public abstract class JApplet extends javax.swing.JApplet {
  public void setStub(AppletStub stub) {
    super.setStub(stub);
  }

  public AppletContext getAppletContext() {
	  return (AppletContext) super.getAppletContext();
  }

	@Override @Deprecated
	public AudioClip getAudioClip(URL url, String name) {
	    try {
	        return getAudioClip(new URL(url, name));
	    } catch (MalformedURLException e) {
	        return null;
	    }
	}

	@Override @Deprecated
	public AudioClip getAudioClip(URL url) {
		return getAppletContext().getAudioClip(url);
	}

}

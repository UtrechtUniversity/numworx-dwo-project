package fi.beans.mainframe;

import java.net.URL;

public interface AppletStub extends java.applet.AppletStub {

  public AppletContext getAppletContext();
  public boolean isActive();
  public URL getCodeBase();
  public URL getDocumentBase();
  public String getParameter(String key);
  public void appletResize(int w, int h);
}

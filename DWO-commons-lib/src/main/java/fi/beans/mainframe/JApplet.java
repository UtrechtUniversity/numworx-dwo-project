package fi.beans.mainframe;

@SuppressWarnings("serial")
public abstract class JApplet extends javax.swing.JApplet {
  public void setStub(AppletStub stub) {
    super.setStub(stub);
  }

  public AppletContext getAppletContext() {
	  return (AppletContext) super.getAppletContext();
  }
}

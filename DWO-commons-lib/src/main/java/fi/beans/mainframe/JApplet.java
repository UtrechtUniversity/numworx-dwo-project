package fi.beans.mainframe;

import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.LayoutManager;
import java.net.URL;

import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

@SuppressWarnings("serial")
public abstract class JApplet extends java.applet.Applet implements RootPaneContainer {

	@Override
	public void setLayeredPane(JLayeredPane layeredPane) {
		root.setLayeredPane(layeredPane);		
	}

	@Override
	public JLayeredPane getLayeredPane() {
		return root.getLayeredPane();
	}

  private AppletStub stub;
  private final JRootPane root;

  public void setStub(AppletStub stub) {
	super.setStub(stub);
    this.stub = stub;
  }

  public AppletContext getAppletContext() {
	  return stub.getAppletContext();
  }
  
  public void init() { }
  public void start() { }
  public void stop() { }
  public void destroy() { }
  
  @Deprecated public boolean isActive() { return stub.isActive(); }
  public URL getCodeBase() { return stub.getCodeBase(); }
  public URL getDocumentBase() { return stub.getDocumentBase(); }
  public String getParameter(String key) { return stub.getParameter(key); }

  @Deprecated
  public String[][] getParameterInfo() {
	return null;
  }

  @Deprecated
  public String getAppletInfo() {
    return null;
  }

	public JApplet() {
		super.setLayout(new BorderLayout());
		root = new JRootPane();
		super.addImpl(root, BorderLayout.CENTER, -1);
	}
	
  public void setContentPane(Container content) {
	  root.setContentPane(content);
  }

  public Container getContentPane() {
	  return root.getContentPane();
  }
  
  public void setGlassPane(Component glass) {
	  root.setGlassPane(glass);
  }
  
  public Component getGlassPane() {
	  return root.getGlassPane();
  }

  public Image getImage(URL url) {
	return getToolkit().createImage(url);
  }

  @Deprecated public AudioClip getAudioClip(URL u) {
	return getAppletContext().getAudioClip(u);
  }
  
  public void setLayout(LayoutManager manager) {
	  if (root == null) {
		  super.setLayout(manager); 
	  } else
          getContentPane().setLayout(manager);
  }
  
  protected void addImpl(Component comp, Object constraints, int index)
  {
      getContentPane().add(comp, constraints, index);
  }

  public JRootPane getRootPane() {
	  return root;
  }
  
  public LayoutManager getLayout() {
	  if (root == null) {
		  return super.getLayout();
	  } else
		  return getContentPane().getLayout();
  }
}

package fi.dwo.dwojapplet.gui.wiskopdr;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class WiskOpdrEditPanel extends JPanel implements Scrollable, AppletStub {

	public class Listener extends ComponentAdapter implements ComponentListener {

    @Override
    public void componentResized(ComponentEvent e) {
      Component nosize = ((Container) getComponent(0));
      Dimension size = getSize();
      nosize.setSize(size.width, size.height);
//      nosize.validate();
    }
  }

  private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(getClass().getName());

//	private static int defaultEditorWidth = 800;
//    private static int defaultEditorHeight = 350;
//    private static int defaultDocumentWidth = 800;
//    private static int defaultDocumentHeight = 300;
	
	String text;
	private Component component;
	
	public WiskOpdrEditPanel(String text) {
		super(new BorderLayout());
		this.text = text;
		getEditor();
	}

    private void getEditor() {
      try {
        Class<?> wiskopdr = WiskOpdrCache.getInstance();
        Method m = wiskopdr.getMethod("getWiskOpdrEditPanel", String.class);
        component = (Component) m.invoke(null, this.text);
        add(component, BorderLayout.CENTER);
        return;
  		} catch (Exception e) {
  		  LOG.log(Level.SEVERE, "WiskOpdrEditPanel", e);
  		  add(new JLabel("not implemented: " + e));
  		}
    }
	
	public WiskOpdrEditPanel(String text, Locale locale, int editorWidth, int editorHeight, int documentWidth, int documentHeight) {
	  super(null);
	  addComponentListener(new Listener());
	  this.text = text;
	  try {
	      Class<?> wiskopdr = WiskOpdrCache.getInstance();
	      Method m = wiskopdr.getMethod("getWiskOpdrEditPanel", String.class, Locale.class, AppletStub.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
	      component = (Component) m.invoke(null, text, locale, this, editorWidth, editorHeight, documentWidth, documentHeight);
	      add(component, BorderLayout.CENTER);
	      return;
	  } catch(Exception e) {
	    LOG.log(Level.WARNING, "WiskOpdrEditPanel with bounds", e);
	    removeAll();
	    getEditor();
	  }
	}

	public String getText() {
		try {
			Method m = component.getClass().getMethod("getText");
			return (String) m.invoke(component);
		} catch(Exception e) {
			LOG.log(Level.SEVERE, null, e);			
		}
		return text;
	}

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 1;
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 1;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return true;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public URL getDocumentBase() {
    return null;
  }

  @Override
  public URL getCodeBase() {
    return null;
  }

  @Override
  public String getParameter(String name) {
    if ("abo_type".equals(name)) {
      try {
        String abo_type = DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getAboType().name();
        return abo_type;
      } catch (Exception e) {
        return AboType.standard.name();
      }
    }
    if ("dwoProfile".equals(name)) {
      Genson g = StoredRestManager.getInstance().getGenson();
      DomDwoProfile profile = DWO.getDwoProfile();
      return g.serialize(profile, new GenericType<DomDwoProfile>() {});
    }
    if ("dwo_env".equals(name)) {
      return GuiCreator.instance().getDWO().getParameter(name);
    }

    if ( DwoHelper.isPremium() && name.startsWith("studentModelMethod:")) {
      try {
        String id = name.substring("studentModelMethod:".length());
        PersistenceId pid = new PersistenceId(id);
        DomMethod result = MethodsProperties.instance().getMethod(pid);
        Genson genson = StoredRestManager.getInstance().getGenson();
        return genson.serialize(result);         
      } catch (Exception e) {
        LOG.log(Level.WARNING, "studentModelMethod", e);
      }
    }
    if ( DwoHelper.isPremium() && "reducedStudentModelContexts".equals(name)) {
      try {
        SecureStudentModelManager manager = GuiCreator.instance().getStudentModelManager();
        List<DomStudentModelContext> list = manager.getReducedList(DWO.getDwoProfile());
        Genson genson = StoredRestManager.getInstance().getGenson();
        return genson.serialize(list);
      } catch (Exception e) {
        LOG.log(Level.WARNING, "studentModelContexts", e);
        return null;
      }
    }

    if ( DwoHelper.isPremium() && name.startsWith("studentModelContext:")) {
      try {
        String id = name.substring("studentModelContext:".length());
        PersistenceId pid = new PersistenceId(id);
        DomStudentModelContextId stid = new DomStudentModelContextId();
        stid.setId(pid);
        SecureStudentModelManager manager = GuiCreator.instance().getStudentModelManager();
        DomStudentModelContext result = manager.get(stid);
        if (result.getPublishState() == PublishState.overt) {
          DomSchoolMethod activeMethod = manager.getActiveMethod(result);
          result.getModelStructure().setActiveMethod(activeMethod.getActiveMethod());
        }
        Genson genson = StoredRestManager.getInstance().getGenson();
        return genson.serialize(result);
      } catch (Exception e) {
        LOG.log(Level.WARNING, "studentModelContext", e);
        return null;
      }
  }
    return null;
  }

  @Override
  public AppletContext getAppletContext() {
    return null;
  }

  @Override
  public void appletResize(int width, int height) {
  }

}

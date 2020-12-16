package fi.dwo.dwojapplet.gui.wiskopdr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WiskOpdrPanel extends JPanel implements InvocationHandler {

	private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(getClass().getName());

	JComponent component;
	String text;
	LinkIF link;

	public WiskOpdrPanel(String s, Locale locale) {
		super(new BorderLayout());
		this.text = s;
		
		try {
			Class<?> wiskopdr = WiskOpdrCache.getInstance();
			Method m = wiskopdr.getMethod("getWiskOpdrPanel", String.class, Locale.class);
			component = (JComponent) m.invoke(null, s, locale);
			component.setBackground(getBackground());
			add(component, BorderLayout.CENTER);
			setSize(component.getSize());
			return;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "WiskOpdrPanel", e);
			add(new JLabel("not implemented: " + e));
		}
		setSize(getPreferredSize());
		
	}

	public void setJSObjectOwner(LinkIF link) {
		this.link = link;
		try {
			ClassLoader loader = component.getClass().getClassLoader();
			Class linkif = loader.loadClass("fi.wiskopdr.tekstobjects.LinkIF");
			Method m = component.getClass().getMethod("setJSObjectOwner", linkif);
			Object proxy = Proxy.newProxyInstance(loader, new Class[] { linkif } , this);
			m.invoke(component, proxy);
		} catch(Exception e) {
			LOG.log(Level.SEVERE, null, e);
		}
		
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String name = method.getName();
		if("getJSObject".equals(name))
			return link.getJSObject();
		if("toString".equals(name)) {
			return link.toString();
		}
		if("gotoScoNr".equals(name)) {
			return link.gotoScoNr( (String)  args[0]);
		}
		
		return null;
	}

  @Override
  public void setBackground(Color bg) {
    super.setBackground(bg);
    if (component != null) {
      component.setBackground(bg);
      for(Component c: component.getComponents()) {
        c.setBackground(bg);
      }
    }
  }

  public void end() {
    try {
      Class<?> wiskopdr = component.getClass();
      Method m = wiskopdr.getMethod("end");
      m.invoke(component);
    } catch (Exception e) {
      LOG.log(Level.WARNING, "WiskOpdr.end()", e);
    }
  }
}

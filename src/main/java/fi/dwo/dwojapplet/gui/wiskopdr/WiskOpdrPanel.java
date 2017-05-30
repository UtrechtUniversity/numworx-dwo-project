package fi.dwo.dwojapplet.gui.wiskopdr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.beans.loader.Loader;

public class WiskOpdrPanel extends JPanel implements InvocationHandler {

	private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(getClass().getName());

	Component component;
	String text;
	LinkIF link;

	public WiskOpdrPanel(String s, Locale locale) {
		super(new BorderLayout());
		this.text = s;
		
		try {
			Class<?> wiskopdr = WiskOpdrCache.getInstance();
			Method m = wiskopdr.getMethod("getWiskOpdrPanel", String.class, Locale.class);
			component = (Component) m.invoke(null, s, locale);
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

}

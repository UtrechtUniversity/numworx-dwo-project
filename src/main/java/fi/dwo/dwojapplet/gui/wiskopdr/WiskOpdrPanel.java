package fi.dwo.dwojapplet.gui.wiskopdr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.dwo.dwojapplet.system.Loader;

public class WiskOpdrPanel extends JPanel implements InvocationHandler {

	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(getClass().getName());

	Component component;
	String text;
	LinkIF link;

	public WiskOpdrPanel(String s) {
		super(new BorderLayout());
		this.text = s;
		
		try {
			Class<?> wiskopdr = Loader.create("wiskopdr.jar").loadClass("fi.wiskopdr.WiskOpdr");
			Method m = wiskopdr.getMethod("getWiskOpdrPanel", String.class);
			component = (Component) m.invoke(null, s);
			add(component, BorderLayout.CENTER);
			setSize(component.getSize());
			return;
		} catch (Exception e) {
			log.log(Level.SEVERE, null, e);
			add(new JLabel("not implemented: " + e));
		}
		setSize(getPreferredSize());
	}

	public void setJSObjectOwner(LinkIF link) {
		this.link = link;
		try {
			ClassLoader loader = component.getClass().getClassLoader();
			Class linkif = loader.loadClass("fi.wiskopdr.textobjects.LinkIF");
			Method m = component.getClass().getMethod("setJSObjectOwner", linkif);
			Object proxy = Proxy.newProxyInstance(loader, new Class[] { linkif } , this);
			m.invoke(component, proxy);
		} catch(Exception e) {
			log.log(Level.SEVERE, null, e);
		}
		
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		log.log(Level.SEVERE, proxy + "." +  method, args);
		return null;
	}

}

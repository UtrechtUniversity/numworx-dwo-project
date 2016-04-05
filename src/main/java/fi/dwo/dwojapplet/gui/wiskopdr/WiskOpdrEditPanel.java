package fi.dwo.dwojapplet.gui.wiskopdr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.system.Loader;

public class WiskOpdrEditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(getClass().getName());
	
	String text;
	private Component component;
	
	public WiskOpdrEditPanel(String text) {
		super(new BorderLayout());
		this.text = text;
		try {
            if(DwoHelper.getJarUrlPath()!=null) Loader.setPrefix(DwoHelper.getJarUrlPath().toString());
			Class<?> wiskopdr = Loader.create("wiskopdr.jar").loadClass("fi.wiskopdr.WiskOpdr");
			Method m = wiskopdr.getMethod("getWiskOpdrEditPanel", String.class);
			component = (Component) m.invoke(null, text);
			add(component, BorderLayout.CENTER);
			return;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			add(new JLabel("not implemented: " + e));
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

}

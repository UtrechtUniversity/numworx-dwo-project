package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;

public class MyBoxLayout extends BoxLayout implements LayoutManager {

	public MyBoxLayout(Container panel, int axis) {
		super(panel, axis);
	}

	/* (non-Javadoc)
	 * @see javax.swing.BoxLayout#layoutContainer(java.awt.Container)
	 */
        @Override
	public void layoutContainer(Container panel) {
		super.layoutContainer(panel);
		Dimension size = panel.getSize();
		Component[] components = panel.getComponents();
		Dimension pref = panel.getPreferredSize();
		if(pref.width > size.width && components.length > 1)
		{
			Component c = components[1];
			pref = components[1].getPreferredSize();
			c.setBounds(Math.max(size.width-pref.width,10), c.getY(), pref.width, c.getHeight());
			int x = c.getX();
			c = components[0];
			c.setSize(x-c.getX(), c.getHeight());
		}
		
	}

	
	
}

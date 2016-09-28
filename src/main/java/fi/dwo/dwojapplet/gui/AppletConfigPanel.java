package fi.dwo.dwojapplet.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import fi.dwo.commons.system.TextMapper;

class AppletConfigPanel extends JPanel implements CenterSubPanel {

	private CenterPanel center;

	@Override
	public void stateChanged(ChangeEvent e) {
	}

	@Override
	public void end() {
	}

	@Override
	public Component getHeaderPanel() {
    	return new HeaderPanel("Template Configuraties");
	}

	@Override
	public void setCenterPanel(CenterPanel centerPanel) {
		this.center = centerPanel;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Object getUserObject() {
		return null;
	}

	public AppletConfigPanel() {
		super(new BorderLayout());
	}

	
	
}

package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.dwojapplet.gui.CenterSubPanel;

public class AppletConfigAction extends GuiAction {

	public AppletConfigAction() {
		this("Templates");
	}

	public AppletConfigAction(String text) {
		super(text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CenterSubPanel panel = instance().getAppletConfigPanel();
		getCenter().loadCenter(panel);
	}

}

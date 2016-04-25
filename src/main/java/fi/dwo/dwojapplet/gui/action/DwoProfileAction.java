package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.CenterSubPanel;

public class DwoProfileAction extends GuiAction {

	public DwoProfileAction() {
		super(TextMapper.getText(TextMapper.DWO_PROFILE_ADMIN));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		CenterSubPanel panel = instance().getDwoProfilePanel();
		getCenter().loadCenter(panel);
	}

}

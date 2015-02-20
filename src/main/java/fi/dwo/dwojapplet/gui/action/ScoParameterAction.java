package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.GuiCreatorTeacher;
import fi.dwo.dwojapplet.gui.ScoPanel;

public class ScoParameterAction extends GuiAction {

	private Sco sco;

	public ScoParameterAction(ScoPanel scoPanel) {
		this(scoPanel.getSco());
	}
	public ScoParameterAction(Sco sco) {
		super(TextMapper.getText(TextMapper.GUIH_EDIT));
		this.sco = sco;
	}

        @Override
	public void actionPerformed(ActionEvent arg0) {
		GuiCreatorTeacher.instance().loadParameterManagementPanel(sco);
		CenterPanel center = getCenter();
		center.getMenu().setEditing(true);
        center.setStrategy(new NullStrategy());
        GuiCreatorTeacher.instance().setReady();           
	}
	
}
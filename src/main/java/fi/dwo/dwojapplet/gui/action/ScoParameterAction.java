package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.client.domain.Sco;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.GuiCreatorTeacher;
import fi.dwo.client.gui.ScoPanel;
import fi.dwo.client.system.TextMapper;

public class ScoParameterAction extends GuiAction {

	private Sco sco;

	public ScoParameterAction(ScoPanel scoPanel) {
		this(scoPanel.getSco());
	}
	public ScoParameterAction(Sco sco) {
		super(TextMapper.getText(TextMapper.GUIH_EDIT));
		this.sco = sco;
	}

	public void actionPerformed(ActionEvent arg0) {
		GuiCreatorTeacher.instance().loadParameterManagementPanel(sco);
		CenterPanel center = getCenter();
		center.getMenu().setEditing(true);
        center.setStrategy(new NullStrategy());
        GuiCreatorTeacher.instance().setReady();           
	}
	
}
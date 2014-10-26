package fi.dwo.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoProfile;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ParameterManagementPanel;
import fi.dwo.client.gui.ScoPanel;

public class PreviewHtml5 extends GuiAction {

	ParameterManagementPanel panel;
	Sco sco;
	
	public PreviewHtml5() {
		super("Preview HTML");
	}

	public PreviewHtml5(String text) {
		super(text);
	}

	public PreviewHtml5(ParameterManagementPanel panel, Sco sco) {
		this();
		this.panel = panel;	
		this.sco = sco;
		setEnabled(sco.hasFeature(Sco.JSON_OUT) && DwoProfile.hasRight(DwoProfile.PREVIEW));
	}
	

	@Override
	public void actionPerformed(ActionEvent _) {
		final CenterPanel center = instance().getMainPanel().getCenter();
		final WrapSco wrap = new WrapSco(sco);
    	Hashtable tmp = panel.getLaunchdata();
    	panel.tmp = sco.getLaunchdata(); //????
    	panel.done = true;
    	sco.setLaunchdata(tmp);
    	ScoPanel sp = instance().previewSco(wrap);
    	sp.tmp = panel;
    	wrap.dwo.setCurrentSco(wrap);
    	center.loadCenter(sp);
	}
}

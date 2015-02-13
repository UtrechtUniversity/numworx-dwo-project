package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fi.dwo.client.domain.CourseMap;

public class CutCopyAction extends GuiAction
{
	CourseMap object;
	boolean cut;
	public void actionPerformed(ActionEvent e) {
		if(object == null)
			Clipboard.setClipboard(Clipboard.getSelection());
		else
			Clipboard.setClipboard(object);
		Clipboard.cmd = e.getActionCommand();
	}

	public CutCopyAction(CourseMap object) {
		this.object = object;
	}
	
	public CutCopyAction(boolean cut) {
		Clipboard.addPropertyChangeListener("selection", this);
		this.cut = cut;
	}

	void setMap(CourseMap map) {
		if(map == null || map.getUserObject()instanceof String)
			setEnabled(false);
		else
			setEnabled(!cut || canModify(map));
	}
	
	
}
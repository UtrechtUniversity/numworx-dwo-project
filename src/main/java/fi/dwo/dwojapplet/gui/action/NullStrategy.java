package fi.dwo.dwojapplet.gui.action;

import javax.swing.JPopupMenu;

import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.gui.SelectStrategy;

public class NullStrategy implements SelectStrategy {

	public void nodeSelected(CourseMap node) {
	}

	public JPopupMenu nodeAction(CourseMap node) {
		return null;
	}
	
}
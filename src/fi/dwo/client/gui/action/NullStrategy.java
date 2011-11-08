package fi.dwo.client.gui.action;

import javax.swing.JPopupMenu;

import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.gui.SelectStrategy;

public class NullStrategy implements SelectStrategy {

	public void nodeSelected(CourseMap node) {
	}

	public JPopupMenu nodeAction(CourseMap node) {
		return null;
	}
	
}
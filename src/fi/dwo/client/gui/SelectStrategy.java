package fi.dwo.client.gui;

import javax.swing.JPopupMenu;

import fi.dwo.client.domain.CourseMap;

public interface SelectStrategy {
	void nodeSelected(CourseMap node);
	JPopupMenu nodeAction(CourseMap node);
}
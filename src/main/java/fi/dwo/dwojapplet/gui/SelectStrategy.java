package fi.dwo.dwojapplet.gui;

import javax.swing.JPopupMenu;

import fi.dwo.dwojapplet.domain.CourseMap;

public interface SelectStrategy {
	void nodeSelected(CourseMap node);
	JPopupMenu nodeAction(CourseMap node);
}
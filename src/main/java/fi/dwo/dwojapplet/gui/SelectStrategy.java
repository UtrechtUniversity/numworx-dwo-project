package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.CourseMap;
import javax.swing.JPopupMenu;

public interface SelectStrategy {

    void nodeSelected(CourseMap node);

    JPopupMenu nodeAction(CourseMap node);
}

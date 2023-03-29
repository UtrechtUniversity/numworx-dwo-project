package fi.dwo.dwojapplet.gui.action;

import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.gui.SelectStrategy;
import javax.swing.JPopupMenu;

public class NullStrategy implements SelectStrategy {

    @Override
    public void nodeSelected(CourseMap node) {
    }

    @Override
    public JPopupMenu nodeAction(CourseMap node) {
        return null;
    }

}

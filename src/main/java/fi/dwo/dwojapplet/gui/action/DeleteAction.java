package fi.dwo.dwojapplet.gui.action;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import javax.swing.JOptionPane;

public class DeleteAction extends GuiAction {

    private CourseMap map;
    private CourseMap parent;
    Course course;
    int row = 0;
    Sco sco;

    public DeleteAction() {
        setMap(null);
        Clipboard.addPropertyChangeListener("selection", this);
    }

    public DeleteAction(CourseMap map) {
        super(""); // setmap vult opschrift
        setMap(map);
    }

    @Override
    void setMap(CourseMap map) {
        this.map = map;
        Object o = map == null ? null : map.getUserObject();
        if (o instanceof Sco) {
            sco = (Sco) o;
            parent = sco.getCourse();
            course = null;
            String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCO);
            Object[] arguments = {sco.toString()};
            putValue(NAME, MessageFormat.format(format, arguments));
            setEnabled(canModify(map));
        } else if (o instanceof Course) {
            course = (Course) o;
            sco = null;
            parent = map.getParentMap();
            CourseMap[] courses = parent.getChildren();
            for (row = 0; row < courses.length; row++) {
                if (courses[row] == course) {
                    break;
                }
            }
            String format
                    = course.isWithChildren()
                            ? TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_MAP)
                            : TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_COURSE);
            Object[] arguments = {o.toString()};
            putValue(NAME, MessageFormat.format(format, arguments));
            setEnabled(canModify(map));
        } else {
            setEnabled(false);
            putValue(NAME, TextMapper.getText("delete"));
        }

    }

    private void newSelection(Object o) {
        CourseMap selection = Clipboard.getSelection();
        if (selection == null) {
            return;
        }
        if (selection.getUserObject() == o) {
            getCenter().select(parent);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (map == null) {
            setMap(Clipboard.getSelection());
            map = null;
            Clipboard.cmd = null;
            Clipboard.setClipboard(null);
        } else // verwijder clipboard als die wordt verwijdert
        if (Clipboard.getClipboard() == map) {
            Clipboard.setClipboard(null);
            Clipboard.cmd = null;
        }

        if (course != null) {
            if (deleteCourse(course)) {
                parent.removeChild(row);
                newSelection(course); // TODO if selection=course then select(parent)                    
            }

        } else if (sco != null) {
            if (deleteSco(sco)) {
                getCenter().updateCourse((Course) parent);
                newSelection(sco); // TODO if selection=sco then select(parent)
            }
            return;
        }
        getCenter().updateMap(parent);
    }

    public static boolean deleteCourse(Course c) {
        String message;
        boolean b = hasScos(c);
        if (b) {
            message = TextMapper.getText(TextMapper.GUIC_MSG_COURSE_DELETE);
        } else {
            message = TextMapper.getText(TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO);
        }
        if (JOptionPane.showConfirmDialog(DwoHelper.getApplet(), message, TextMapper.getText(TextMapper.GUIC_MSG_TTL_COURSE_DELETE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (instance().deleteCourse(c)) {
                return true;
            }
        }
        return false;
    }

    // scolist.lenght > 0 maar dan recursief
    private static boolean hasScos(Course c) {
        if (c.isWithChildren()) {
            CourseMap[] children = c.getChildren();
            for (int i = 0; i < children.length; i++) {
                if (hasScos((Course) children[i])) {
                    return true;
                }
            }
        }
        c.loadScos();
        boolean b = c.getScoList().length > 0;
        return b;
    }

    public static boolean deleteSco(Sco s) {
        String message;
        message = TextMapper.getText(TextMapper.GUIS_MSG_SCO_DELETE);
        if (JOptionPane.showConfirmDialog(DwoHelper.getApplet(), message, TextMapper.getText(TextMapper.GUIS_MSG_TTL_SCO_DELETE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (instance().deleteSco(s)) {
                return true;
            }
        }
        return false;
    }

}

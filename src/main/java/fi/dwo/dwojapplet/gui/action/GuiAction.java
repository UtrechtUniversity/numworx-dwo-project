package fi.dwo.dwojapplet.gui.action;

import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;

abstract public class GuiAction extends AbstractAction implements PropertyChangeListener {

    private CenterPanel center;

    final boolean hasAdminRight() {
        return instance().getUser().hasRight(User.PROFILE_ADMIN_RIGHT);
    }

    final static GuiCreator instance() {
        return GuiCreator.instance();
    }

    GuiAction() {
        super();
    }

    public CenterPanel getCenter() {
        if (center == null) {
            center = instance().getMainPanel().getCenter();
        }
        return center;
    }

    public void setCenter(CenterPanel center) {
        this.center = center;
    }

    boolean canModify(CourseMap map) {
        if (instance().getUser().getSchool() == null) {
            return false;
        }

        if (map == null) {
            return false;
        }
        if (map instanceof Course) {
            if (hasAdminRight()) {
                return true;
            }
            Course course = (Course) map;
            int id = course.getSchoolID();
            User user = instance().getUser();
            School school = user.getSchool();
            int ID = school.getSchoolID();
            return id == ID;// course.getSchoolID() == instance.getUser().getSchool().getSchoolID();
        } else if (map.getUserObject() instanceof Sco) {
            if (hasAdminRight()) {
                return true;
            }
            Course course = ((Sco) map.getUserObject()).getCourse();
            return course.getSchoolID() == instance().getUser().getSchool().getSchoolID();
        }
        if (map.getUserObject() == ModuleTreePanel.ALLE_MODULES) {
            return false;
        }
        return hasAdminRight() || map.getUserObject() == ModuleTreePanel.SCHOOL_MODULES;
    }

    public GuiAction(String text) {
        super(text);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CourseMap map = (CourseMap) evt.getNewValue();
        setMap(map);
    }

    void setMap(CourseMap map) {
    }

}

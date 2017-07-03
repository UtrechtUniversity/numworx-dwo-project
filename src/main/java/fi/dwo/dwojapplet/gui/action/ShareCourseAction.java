package fi.dwo.dwojapplet.gui.action;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.gui.ExportImportDialog;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShareCourseAction extends GuiAction {
    private static final Logger LOG = Logger.getLogger(ShareCourseAction.class.getName());

    private CourseMap map;

    public ShareCourseAction(Component component) {
        this(component, null);
    }

    public ShareCourseAction(Component component, CourseMap map) {
        super(TextMapper.getText(TextMapper.GUIC_COURSE_SHARE));
        this.map = map;
        setEnabled(instance().getUser() instanceof Teacher);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Component component;
        Object o = ev.getSource();
        if (o instanceof Component) {
            component = (Component) o;
        } else {
            component = DwoHelper.getApplet();
        }
        ExportImportDialog dialog;
        try {
            dialog = new ExportImportDialog(DwoHelper.getFrameForComponent(component), instance().getUser(), instance().getDWO().getDwoProfileID());
            dialog.setMap(map);
            dialog.setVisible(true);
            getCenter().updateMap(map);
        } catch (PersistenceException e1) {
            LOG.log(Level.SEVERE,null,e1);
        }

    }

    @Override
    public void setMap(CourseMap map) {
        this.map = map;
    }

}

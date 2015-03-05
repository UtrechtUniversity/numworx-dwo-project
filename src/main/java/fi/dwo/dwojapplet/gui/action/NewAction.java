package fi.dwo.dwojapplet.gui.action;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.AddScoDialog;
import fi.dwo.dwojapplet.gui.CourseNameDialog;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;
import fi.dwo.dwojapplet.gui.ScoNameDialog;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class NewAction extends GuiAction {
    private static final Logger log = Logger.getLogger(NewAction.class.getName());

    private final static Course STANDARD_MAP = new Course();
    private CourseMap map;
    boolean ismap, submap;
    Course course;

    public NewAction(CourseMap map, boolean submap) {
        super();
        this.submap = submap;
        setMap(map);

    }

    @Override
    void setMap(CourseMap map) {
        setEnabled(canModify(map));
        this.map = map;
        if (map instanceof Course) {
            course = (Course) map.getUserObject();
            ismap = course.isWithChildren();

        } else if (map == ModuleTreePanel.SCHOOL_MAP) {
            course = null;
            ismap = true;
        } else {
            course = STANDARD_MAP;
            ismap = true;
        }
        /* vier gevallen: 
         * ismap submap
         * true  true     addmap enabled
         * false true     addmap disabled
         * true  false    addcourse
         * false false    addsco			
         */
        if (ismap) {
            if (submap) {
                putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_MAP));
            } else {
                putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_COURSE));
            }
        } else if (submap) {
            setEnabled(false);
        } else {
            putValue(NAME, TextMapper.getText(TextMapper.GUIS_ADD_SCO));
        }
    }

    public NewAction(boolean ismap, boolean submap) {
        this.ismap = ismap;
        this.submap = submap;
        if (submap && ismap) {
            putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_MAP));
        } else if (ismap) {
            putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_COURSE));
        } else {
            putValue(NAME, TextMapper.getText(TextMapper.GUIS_ADD_SCO));
        }
        Clipboard.addPropertyChangeListener("selection", this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//			if(map == null)
//				map = Clipboard.getSelection(); // FIXME en course dan?
        if (map == null) {
            return;
        }
// FIXME werkt niet goed, als updateMap werkt op een toplevel map.
        if (submap) {
            Course child = CourseNameDialog.addMap(DwoHelper.getApplet(), course);
            if (child != null) {
                addNewCourse(child);
            }
        } else if (ismap) {
            Course child = CourseNameDialog.addCourse(DwoHelper.getApplet(), course);
            if (child != null) {
                addNewCourse(child);
            }
        } else {
            Sco s = null;
            if (course.getScoList() == null) {
                course.loadScos();
            }
// speciaal voor de SAG en REV: er kan maar 1 soort appletConfig gebruikt worden, nl WiskOpdr
            if (course.getDwoProfile() == 15) {
                try {
                    AppletConfig ac = (AppletConfig) (PersistenceFacade.instance().get(55, AppletConfig.class));
                    s = ScoNameDialog.addSco(DwoHelper.getApplet(), course, ac);
                } catch (PersistenceException ex) {
                    JOptionPane.showMessageDialog(DwoHelper.getApplet(), ex.getMessage());
                }
            } else {
                s = AddScoDialog.addSco(DwoHelper.getApplet(), course);
            }
            if (s != null) {
// FIXME addSco kan de sco al in de lijst gezet hebben....
                Sco[] as = course.getScoList();
                /* Create a larger array and add the item */
                Sco[] tmp = new Sco[as.length + 1];
                System.arraycopy(as, 0, tmp, 0, as.length);
                tmp[tmp.length - 1] = s;
                course.setScoList(tmp);
                getCenter().updateCourse(course);
            }
        }

    }

    private void addNewCourse(Course child) {
        map.addChild(child);
        if (DWO.SEQUENCE) {
            sequenceCourses(map);
        }
        getCenter().updateMap(map);
    }
// DIT IS EEN KOPIE VAN CourseManagementPanel TODO in deze vorm verplaatsen naar de Domain layer = DWO

    private void sequenceCourses(CourseMap map) {
        try {
            Object userObject = map.getUserObject();
            CourseMap[] courses = map.getChildren();

            School school = User.getCurrentUser().getSchool();
// een profile admin mag de standaard modules sorteren, maar de school is dan wel null				
            if (userObject == ModuleTreePanel.STANDAARD_DWO_MAP || ModuleTreePanel.STANDAARD_DWO_MODULES == userObject) {
                school = null;
            }
            if (userObject instanceof Course) {
                if (((Course) userObject).getSchoolID() == 0) {
                    school = null;
                }
            }
            PersistenceFacade.instance().setCourseSequence(courses, school);
        } catch (PersistenceException e) {
            log.log(Level.SEVERE,null,e);
        }

    }

}

package fi.dwo.dwojapplet.domain;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * Een ResultsModuleIF geschikt gemaakt voor één enkele student. TODO een common
 * AbstractResultsModule base class samen met ResultsModule
 *
 * @author Wim
 * @see ResultsModule
 *
 */
public class UserResultsModule implements Comparator, ResultsModuleIF {

    private Course[] courses;
    private User user;
    private DWO dwo;
    private LessonGroup currentlyZoomedLesson;
    private UserGroup currentlyZoomedUser;
    private UserGroup currentlyOrderedUser;
    private Object currentlyOrderedLesson;
    private int orderedLessonIndex;
    private Vector userResultList = new Vector();
    private int orderedWay;

    public UserResultsModule(Course course, User user, DWO dwo) {
        this(new Course[]{course}, user, dwo);
        currentlyZoomedLesson = course;
    }

    public UserResultsModule(Course[] courses, User user, DWO dwo) {
        this.courses = courses;
        this.user = user;
        this.dwo = dwo;
        reset();
        currentlyZoomedLesson = null;
        currentlyZoomedUser = user;
        currentlyOrderedUser = user;
        currentlyOrderedLesson = null;
        orderedLessonIndex = -1;
    }

    @Override
    public int compare(Object o1, Object o2) {
        UserResultList url1 = (UserResultList) o1;
        UserResultList url2 = (UserResultList) o2;

        if (currentlyOrderedLesson != null) {
            if (orderedLessonIndex != -1) {
                if (orderedWay == ResultsModuleIF.ASC) {
                    return compareFloats(url1.getResultScore()[orderedLessonIndex].getScore(), url2.getResultScore()[orderedLessonIndex].getScore());
                } else {
                    return compareFloats(url2.getResultScore()[orderedLessonIndex].getScore(), url1.getResultScore()[orderedLessonIndex].getScore());
                }
            } else {
                for (int i = 0; i < url1.getResultScore().length; i++) {
                    if (url1.getResultScore()[i].getLessonGroup() == currentlyOrderedLesson) {
                        orderedLessonIndex = i;
                        break;
                    }
                }
                return compare(o1, o2);
            }
        } else if (currentlyOrderedUser != null) {
            if (orderedWay == ResultsModuleIF.ASC) {
                return url1.getResultScore()[0].getUserGroup().getOrderName().compareTo(url2.getResultScore()[0].getUserGroup().getOrderName());
            } else {
                return url2.getResultScore()[0].getUserGroup().getOrderName().compareTo(url1.getResultScore()[0].getUserGroup().getOrderName());
            }

        }
        return 0;
    }

    private int compareFloats(float f1, float f2) {
        if (f1 < f2) {
            return -1; // Neither val is NaN, thisVal is smaller
        }
        if (f1 > f2) {
            return 1; // Neither val is NaN, thisVal is larger
        }
        return 0;
    }

    @Override
    public Course[] getAllCourses() {
        return dwo.getCourses();
    }

    @Override
    public Vector getResults() {
        if (currentlyZoomedLesson != null) {
            try {
                userResultList = PersistenceFacade.instance().getUserResults((Course) currentlyZoomedLesson, user);
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(dwo, e.getMessage());
            }
        } else {
            try {
                userResultList = PersistenceFacade.instance().getUserResults(courses, user);
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(dwo, e.getMessage());
            }
        }
        return userResultList;
    }

    @Override
    public Course[] getSelectedCourse() {
        return courses;
    }

    @Override
    public LessonGroup getZoomedLessonGroup() {
        return currentlyZoomedLesson;
    }

    @Override
    public UserGroup getZoomedUserGroup() {
        return currentlyZoomedUser;
    }

    @Override
    public Vector orderBy(UserGroup ug, int orderWay) {
        orderedLessonIndex = -1;
        orderedWay = orderWay;
        currentlyOrderedLesson = null;
        currentlyOrderedUser = ug;
        Collections.sort(userResultList, this);
        return userResultList;
    }

    @Override
    public Vector orderBy(LessonGroup lg, int orderWay) {
        orderedLessonIndex = -1;
        orderedWay = orderWay;
        currentlyOrderedLesson = lg;
        currentlyOrderedUser = null;
        Collections.sort(userResultList, this);
        return userResultList;
    }

    @Override
    public void reset() {
        MapperIF m = MapperCreator.instance(UserResultList.class);

        if (m instanceof UserResultListMapper) {
            ((UserResultListMapper) m).setResultsModule(this);
        }

        currentlyZoomedLesson = null;
        currentlyZoomedUser = null;
        currentlyOrderedUser = null;
        currentlyOrderedLesson = null;
        orderedLessonIndex = -1;
    }

    @Override
    public void selectCourses(Course[] courses) {
        this.courses = courses;
    }

    @Override
    public Vector selectCourses(Course[] courses, boolean getResults) {
        this.courses = courses;
        if (getResults) {
            if (currentlyZoomedLesson == null) {
                return getResults();
            } else {
                return userResultList;
            }
        } else {
            return new Vector();
        }
    }

    @Override
    public void showResult(ResultScore rs) {
        LessonGroup lg = rs.getLessonGroup();
        if (lg instanceof Sco) {
            Sco sco = (Sco) lg;
            GuiCreator.instance().setWait();
            final Sco s = sco;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    CenterSubPanel csp = GuiCreator.instance().getScoPanel(s);
                    if (csp != null) {
                        s.setLessonMode(Sco.REVIEW);
                        GuiCreator.instance().getMainPanel().getCenter().loadTotal(csp);
                    }
                    GuiCreator.instance().setReady();
                }
            };
            thread.start();/**/

        }
    }

    @Override
    public Vector zoomIn(UserGroup ug) {
        orderedLessonIndex = -1;
        currentlyZoomedUser = ug;
        currentlyOrderedUser = null;
        return getResults();
    }

    @Override
    public Vector zoomIn(LessonGroup lg) {
        orderedLessonIndex = -1;
        currentlyZoomedLesson = lg;
        currentlyOrderedLesson = null;
        return getResults();
    }

    @Override
    public Vector zoomOut(UserGroup ug) {
        orderedLessonIndex = -1;
        currentlyZoomedUser = null;
        currentlyOrderedUser = null;
        return getResults();
    }

    @Override
    public Vector zoomOut(LessonGroup lg) {
        orderedLessonIndex = -1;
        currentlyZoomedLesson = null;
        currentlyOrderedLesson = null;
        return getResults();
    }

}

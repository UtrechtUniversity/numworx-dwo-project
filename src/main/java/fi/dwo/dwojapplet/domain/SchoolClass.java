// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\SchoolClass.java
package fi.dwo.dwojapplet.domain;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * This class is responsible for the SchoolClass data.
 *
 * @author M.J.B. Kupers
 *
 */
public class SchoolClass implements UserGroup, Comparable {

    private int classID;

    private String className;

    private boolean iconizer = false; // database entry

    /**
     * @param iconizer the iconizer to set
     */
    public void setIconizer(boolean iconizer) {
        this.iconizer = iconizer;
    }

    /**
     * Creates a new SchoolClass object.
     *
     */
    public SchoolClass() {

    }

    ////peter
    public Course[] getSelectedSchoolCourses() {
        Course[] courses = null;
        try {
            courses = (Course[]) PersistenceFacade.instance()
                    .getSelectedSchoolCourses(this);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return courses;
    }

    public void saveSelectedCourses(Course[] selectedCourses) {
        ClassCourse[] v = new ClassCourse[selectedCourses.length];
        for (int i = 0; i < selectedCourses.length; i++) {
            ClassCourse link = selectedCourses[i].link;
            if (link != null) {
            } else {
                link = new ClassCourse();
            }
            link.setCourseID(selectedCourses[i].getID());
            v[i] = link;
        }
        try {
            PersistenceFacade.instance().selectCoursesForClass(this, v);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void saveSelectedCourses(Course[] allCourses,
            Course[] selectedCourses) {
        if (true) {
            saveSelectedCourses(selectedCourses);
            return;
        }
        deselectAllCourses(allCourses);
        for (int i = 0; i < selectedCourses.length; i++) {
            try {
                Date tot = null;
                Date van = null;
                int type = 0;
                ClassCourse link = selectedCourses[i].link;
                if (link != null) {
                    van = link.getNotBefore();
                    tot = link.getNotAfter();
                    type = link.getType();
                }
                PersistenceFacade.instance().selectCoursesForClass(getID(),
                        selectedCourses[i].getID(), type, van, tot);
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private void deselectAllCourses(CourseMap[] allCourses) {
        for (int i = 0; i < allCourses.length; i++) {
            Course course = (Course) allCourses[i];
            if (course.isWithChildren()) {
                deselectAllCourses(course.getChildren());
            }
            try {
                PersistenceFacade.instance().deSelectCoursesForClass(getID(),
                        course.getID());
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }

        }
    }

    ////peter
    /**
     * Returns all the students of the class.
     *
     * @return All the students of the class.
     *
     */
    public User[] getStudents() {
        User[] users = null;
        try {
            users = (User[]) PersistenceFacade.instance().get(User.class, this);
        } catch (PersistenceException e) {
        }
        return users;
    }
    /**
     * Disconnect an user from the class.
     *
     * @param user The user to disconnect.
     *
     */
    public void disconnectStudent(int classID, User user) {
        try {
            PersistenceFacade.instance().removeStudentFromClass(classID, user.getID());
        } catch (PersistenceException e) {
        }
    }


    /**
     * Disconnect an user from the class.
     *
     * @param user The user to disconnect.
     *
     */
    public void disconnectTeacher(SchoolClass sc, User user) {
        try {
            PersistenceFacade.instance().removeTeacherFromClass(classID, user.getID());
        } catch (PersistenceException e) {
        }
    }
    
    /**
     * Returns the name of the class.
     *
     * @return The name of the class.
     *
     */
    @Override
    public String getName() {
        return className;
    }

    /**
     * Returns the unique-identifier of the class.
     *
     * @return The unique-identifier of the class.
     *
     */
    @Override
    public int getID() {
        return classID;
    }

    /**
     * Indicates if this is the deepest UserGroup.
     *
     * @return If this is the deepest UserGroup it returns true. Otherwise it
     * returns false.
     * @see fi.dwo.client.domain.UserGroup#isDeepestLevel()
     */
    @Override
    public boolean isDeepestLevel() {
        return false;
    }

    /**
     * Indicates if this is the highest UserGroup.
     *
     * @return If this is the highest UserGroup it returns true. Otherwise it
     * returns false.
     * @see fi.dwo.client.domain.UserGroup#isHighestLevel()
     */
    @Override
    public boolean isHighestLevel() {
        return true;
    }

    /**
     * Returns a title representing the UserGroup object.
     *
     * @return A title representing the UserGroup object.
     * @see fi.dwo.client.domain.UserGroup#getTitle()
     */
    @Override
    public String getTitle() {
        return TextMapper.getText(TextMapper.UG_CLASSES);
    }

    /**
     * Sets the unique-identifier of the class.
     *
     * @param classID The unique-identifier to set.
     */
    public void setClassID(int classID) {
        this.classID = classID;
    }

    /**
     * Sets the name of the class.
     *
     * @param className The new class name.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the name to order the usergroup.
     *
     * @return The name to order the usergroup.
     * @see fi.dwo.client.domain.UserGroup#getOrderName()
     */
    @Override
    public String getOrderName() {
        return className.toLowerCase();
    }

    /**
     * Compares an other SchoolClass with this class.
     *
     * @param o A Schoolclass to compare with.
     * @return a negative integer, zero, or a positive integer as the first
     * argument is alfabetical less than, equal to, or greater than the second.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Object o) {
        SchoolClass sc = (SchoolClass) o;
        return className.toLowerCase().compareTo(sc.getName().toLowerCase());
    }

    /**
     * Returns a typename representing the Class.
     *
     * @return A typename representing the Class.
     * @see fi.dwo.client.domain.UserGroup#getType()
     */
    @Override
    public String getType() {
        return TextMapper.getText(TextMapper.UG_CLASS_TITLE);
    }

    /**
     * Returns a title represents the parent item.
     *
     * @return A title represents the parent item.
     * @see fi.dwo.client.domain.UserGroup#getParentTitle()
     */
    @Override
    public String getParentTitle() {
        return "";
    }

    /**
     * Returns a title represents the child item.
     *
     * @return A title represents the child item.
     * @see fi.dwo.client.domain.UserGroup#getChildTitle()
     */
    @Override
    public String getChildTitle() {
        String[] arguments = new String[1];
        arguments[0] = className;
        return TextMapper.format((TextMapper.UG_CLASS_CHILD), arguments);
    }

    /**
     * Returns a title represents the Ascending Order item.
     *
     * @return A title represents the Ascending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderAscTitle()
     */
    @Override
    public String getOrderAscTitle() {
        return TextMapper.getText(TextMapper.UG_CLASS_ORDER_ASC);
    }

    /**
     * Returns a title represents the Descending Order item.
     *
     * @return A title represents the Descending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderDescTitle()
     */
    @Override
    public String getOrderDescTitle() {
        return TextMapper.getText(TextMapper.UG_CLASS_ORDER_DESC);
    }

    public boolean hasIconizer() {
        return iconizer;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getUsername() {
        return "";
    }

}

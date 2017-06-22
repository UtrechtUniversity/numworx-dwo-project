package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

/**
 * SchoolClassItem for ListBoxes and Cell renderings.
 * 
 * @author G.A.J. van der Plas
 */
public class TeacherListBoxItem {

    private String key; //unique
    private String teacherName;

    public TeacherListBoxItem(String aKey, String value) {
        key = aKey;
        teacherName = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String aKey) {
        key = aKey;
    }

    /**
     * @return the teacherName
     */
    public String getTeacherName() {
        return teacherName;
    }

    /**
     * @param teacherName the teacherName to set
     */
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}

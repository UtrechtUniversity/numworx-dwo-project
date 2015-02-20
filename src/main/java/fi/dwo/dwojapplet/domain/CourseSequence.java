package fi.dwo.dwojapplet.domain;

/**
 * Modules op volgorde zetten, voor een klas, voor een school en voor een gast.
 * TODO voor een map: parent is CourseMap interface.
 *
 * @author wim
 *
 */
public class CourseSequence implements Comparable {

    private int courseSequenceID;
    private int courseID;
    private int sequencenr;
    private School school;
    private SchoolClass schoolClass;
    private int parent; // TODO... 
    private int profileID;

    @Override
    public int compareTo(Object o) {
        CourseSequence cs = (CourseSequence) o;
        int a = sequencenr;
        int b = cs.sequencenr;
        return a < b ? -1 : a > b ? +1 : 0;
    }

    public int getID() {
        return courseSequenceID;
    }

    public int getCourseID() {
        return courseID;
    }

    public int getSequencenr() {
        return sequencenr;
    }

    public School getSchool() {
        return school;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setID(int courseSequenceID) {
        this.courseSequenceID = courseSequenceID;
    }

    public void setCourseID(int course) {
        this.courseID = course;
    }

    public void setSequencenr(int sequencenr) {
        this.sequencenr = sequencenr;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    // TODO implement CourseMap
    public int getParentID() {
        return parent;
    }

    public void setParentID(int parent) {
        this.parent = parent;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

}

package fi.dwo.dwojapplet.domain;

import fi.dwo.dwojapplet.persistence.PersistenceFacade;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassCourse {
    private static final Logger LOG = Logger.getLogger(ClassCourse.class.getName());

    public static final int NORMAL = 0;
    public static final int ASSESSMENT = 1;
    private int classCourseID;
    private int classID;
    private int courseID;
    /**
     * Enum
     */
    private int viewState;
    private int type;
    private Date notBefore;
    private Date notAfter;
    private String accessKey;

//    protected SchoolClass schoolClass;
    protected Course course;

    /**
     * @return the classCourseID
     */
    public int getClassCourseID() {
        return classCourseID;
    }

    public int getID() {
        return getClassCourseID();
    }

    /**
     * @param classCourseID the classCourseID to set
     */
    public void setClassCourseID(int classCourseID) {
        this.classCourseID = classCourseID;
    }

    /**
     * @return the classID
     */
    public int getClassID() {
        return classID;
    }

    /**
     * @param classID the classID to set
     */
    public void setClassID(int classID) {
//        schoolClass = null;
        this.classID = classID;
    }

    /**
     * @return the courseID
     */
    public int getCourseID() {
        return courseID;
    }

    /**
     * @param courseID the courseID to set
     */
    public void setCourseID(int courseID) {
        course = null;
        this.courseID = courseID;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the notBefore
     */
    public Date getNotBefore() {
        return notBefore;
    }

    /**
     * @param notBefore the notBefore to set
     */
    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    /**
     * @return the notAfter
     */
    public Date getNotAfter() {
        return notAfter;
    }

    /**
     * @param notAfter the notAfter to set
     */
    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

//    /**
//     * @return the schoolClass
//     */
//    public SchoolClass getSchoolClass() {
//        if (schoolClass == null) {
//            fetchSchoolClass();
//        }
//        return schoolClass;
//    }

//    protected void fetchSchoolClass() {
//        try {
//            schoolClass = PersistenceFacade.instance().getSchoolClass(getClassID());
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE,null,e);
//        }
//    }

//    /**
//     * @return the course
//     */
//    public Course getCourse() {
//        if (course == null) {
//            fetchCourse();
//        }
//        return course;
//    }

//    protected void fetchCourse() {
//        try {
//            course = (Course) PersistenceFacade.instance().getCourse(getCourseID());
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE,null,e);
//        }
//
//    }

    /**
     * @return the viewState
     */
    public int getViewState() {
        return viewState;
    }

    /**
     * @param viewState the viewState to set
     */
    public void setViewState(int viewState) {
        this.viewState = viewState;
    }

	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

}

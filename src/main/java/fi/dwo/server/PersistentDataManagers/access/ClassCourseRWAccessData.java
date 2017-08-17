/**
 * Copyrighted Aug 17, 2017
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;

/**
 *
 * @author Gert van der Plas
 */
public class ClassCourseRWAccessData {
    private PersistentHasRole phr = null;
    private PersistentSchool school = null;
    private PersistentSchoolClass schoolClass = null;
    private PersistentCourse course = null;
    private PersistentTeacherOfClass toc = null;
    private PersistentDwoProfile profile = null;

    ClassCourseRWAccessData(PersistentHasRole hr, PersistentSchool s, 
            PersistentSchoolClass sc, PersistentCourse c, PersistentTeacherOfClass t,
        PersistentDwoProfile p){
        phr = hr;
        school =s;
        schoolClass = sc;
        course = c;
        toc = t;
        profile = p;
    }
    
    /**
     * @return the phr
     */
    public PersistentHasRole getPhr() {
        return phr;
    }

    /**
     * @param phr the phr to set
     */
    public void setPhr(PersistentHasRole phr) {
        this.phr = phr;
    }

    /**
     * @return the school
     */
    public PersistentSchool getSchool() {
        return school;
    }

    /**
     * @param school the school to set
     */
    public void setSchool(PersistentSchool school) {
        this.school = school;
    }

    /**
     * @return the schoolClass
     */
    public PersistentSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(PersistentSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    /**
     * @return the course
     */
    public PersistentCourse getCourse() {
        return course;
    }

    /**
     * @param course the course to set
     */
    public void setCourse(PersistentCourse course) {
        this.course = course;
    }

    /**
     * @return the toc
     */
    public PersistentTeacherOfClass getToc() {
        return toc;
    }

    /**
     * @param toc the toc to set
     */
    public void setToc(PersistentTeacherOfClass toc) {
        this.toc = toc;
    }

    /**
     * @return the profile
     */
    public PersistentDwoProfile getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(PersistentDwoProfile profile) {
        this.profile = profile;
    }

}

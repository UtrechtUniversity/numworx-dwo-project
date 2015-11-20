/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestRemoveTeacherFromSchoolClass {
    private RestTeacher teacher;
    private RestSchoolClass schoolClass;

    /**
     * @return the teacher
     */
    public RestTeacher getTeacher() {
        return teacher;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(RestTeacher teacher) {
        this.teacher = teacher;
    }

    /**
     * @return the schoolClass
     */
    public RestSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(RestSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }
}

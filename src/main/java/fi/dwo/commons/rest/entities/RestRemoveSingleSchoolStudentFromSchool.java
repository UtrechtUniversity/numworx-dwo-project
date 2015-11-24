/**
 * Copyrighted Nov 24, 2015
 */
package fi.dwo.commons.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestRemoveSingleSchoolStudentFromSchool {
    private RestStudent student;
    private RestSchool school;

    /**
     * @return the student
     */
    public RestStudent getStudent() {
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(RestStudent student) {
        this.student = student;
    }

    /**
     * @return the school
     */
    public RestSchool getSchool() {
        return school;
    }

    /**
     * @param school the school to set
     */
    public void setSchool(RestSchool school) {
        this.school = school;
    }
}

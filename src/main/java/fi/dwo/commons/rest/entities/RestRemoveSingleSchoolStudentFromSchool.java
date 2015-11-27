/**
 * Copyrighted Nov 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomSchool;
import fi.dwo.commons.dom.entities.DomStudent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestRemoveSingleSchoolStudentFromSchool {
    private RestContext restContext;
    private DomStudent student;
    private DomSchool school;

    /**
     * @return the student
     */
    public DomStudent getStudent() {
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(DomStudent student) {
        this.student = student;
    }

    /**
     * @return the school
     */
    public DomSchool getSchool() {
        return school;
    }

    /**
     * @param school the school to set
     */
    public void setSchool(DomSchool school) {
        this.school = school;
    }
    
    /**
     * @return the restContext
     */
    public RestContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(RestContext restContext) {
        this.restContext = restContext;
    }
}

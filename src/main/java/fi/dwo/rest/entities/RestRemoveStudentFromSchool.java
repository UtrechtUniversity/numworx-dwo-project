/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomSchool;
import fi.dwo.rest.dom.entities.DomStudent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestRemoveStudentFromSchool {
    private DomStudent student;
    private DomSchool school;
    private DomContext restContext;

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
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }

}

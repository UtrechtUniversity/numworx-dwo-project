/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomStudent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestRemoveStudentFromSchoolClass {
    private DomStudent student;
    private DomSchoolClass schoolClass;
    private RestContext restContext;

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
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
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

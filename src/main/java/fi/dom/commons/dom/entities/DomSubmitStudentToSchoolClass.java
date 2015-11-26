/**
 * Copyrighted Nov 20, 2015
 */
package fi.dom.commons.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSubmitStudentToSchoolClass {
    private DomStudent student;
    private DomSchoolClass schoolClass;

    /**
     * @return the student
     */
    public DomStudent getStudent() {
        return student;
    }

    /**
     * @param student
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
}

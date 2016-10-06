/**
 * Copyrighted Nov 20, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSubmitStudentToSchoolClass {
    private DomStudent student;
    private DomSchoolClass schoolClassFrom;
    private DomSchoolClass schoolClassTo;

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
     * @return the schoolFromClass
     */
    public DomSchoolClass getSchoolClassFrom() {
        return schoolClassFrom;
    }

    /**
     * @param schoolFromClass the schoolFromClass to set
     */
    public void setSchoolClassFrom(DomSchoolClass schoolFromClass) {
        this.schoolClassFrom = schoolFromClass;
    }

    /**
     * @return the schoolClassTo
     */
    public DomSchoolClass getSchoolClassTo() {
        return schoolClassTo;
    }

    /**
     * @param schoolClassTo the schoolClassTo to set
     */
    public void setSchoolClassTo(DomSchoolClass schoolClassTo) {
        this.schoolClassTo = schoolClassTo;
    }

}

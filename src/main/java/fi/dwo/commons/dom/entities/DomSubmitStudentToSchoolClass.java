/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSubmitStudentToSchoolClass {
    private DomStudent student;
    private DomSchoolClass schoolFromClass;
    private DomSchoolClass schoolToClass;

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
    public DomSchoolClass getSchoolFromClass() {
        return schoolFromClass;
    }

    /**
     * @param schoolFromClass the schoolFromClass to set
     */
    public void setSchoolFromClass(DomSchoolClass schoolFromClass) {
        this.schoolFromClass = schoolFromClass;
    }

    /**
     * @return the schoolToClass
     */
    public DomSchoolClass getSchoolToClass() {
        return schoolToClass;
    }

    /**
     * @param schoolToClass the schoolToClass to set
     */
    public void setSchoolToClass(DomSchoolClass schoolToClass) {
        this.schoolToClass = schoolToClass;
    }

}

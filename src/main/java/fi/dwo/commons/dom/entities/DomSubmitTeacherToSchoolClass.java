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
public class DomSubmitTeacherToSchoolClass {
    private DomTeacher teacher;
    private DomSchoolClass schoolClass;

    /**
     * @return the teacher
     */
    public DomTeacher getTeacher() {
        return teacher;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(DomTeacher teacher) {
        this.teacher = teacher;
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

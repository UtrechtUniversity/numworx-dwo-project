/**
 * Copyrighted Apr 1, 2016
 */
package fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomTeacherAndHasRole {
    private DomTeacher teacher;
    private DomHasRole hasRole;

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
     * @return the hasRole
     */
    public DomHasRole getHasRole() {
        return hasRole;
    }

    /**
     * @param hasRole the hasRole to set
     */
    public void setHasRole(DomHasRole hasRole) {
        this.hasRole = hasRole;
    }
}

/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomStudentOfClass {
    private PersistenceId id;
    private PersistenceId classId;
    private PersistenceId studentId;

    public DomStudentOfClass(){
    }

    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(PersistenceId id) {
        this.id = id;
    }

    /**
     * @return the classId
     */
    public PersistenceId getClassId() {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(PersistenceId classId) {
        this.classId = classId;
    }

    /**
     * @return the studentId
     */
    public PersistenceId getStudentId() {
        return studentId;
    }

    /**
     * @param studentId the studentId to set
     */
    public void setStudentId(PersistenceId studentId) {
        this.studentId = studentId;
    }
       
}

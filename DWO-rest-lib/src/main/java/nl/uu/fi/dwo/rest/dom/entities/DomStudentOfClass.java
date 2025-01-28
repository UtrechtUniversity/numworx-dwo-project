/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomStudentOfClass extends DomMemberOfClass {
    public DomStudentOfClass(){
    }

    /**
     * @return the studentId
     */
    @XmlTransient
    @Transient
    public PersistenceId getStudentId() {
        return userId;
    }

    /**
     * @param studentId the studentId to set
     */
    public void setStudentId(PersistenceId studentId) {
        this.userId = studentId;
    }
       
}

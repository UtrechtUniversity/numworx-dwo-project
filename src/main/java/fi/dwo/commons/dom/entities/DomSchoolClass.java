/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomSchoolClass {
    private PersistenceId id;
    private String schoolClassName = "";


    public DomSchoolClass(){
        
    }
        
    public DomSchoolClass(PersistentSchoolClass sc) {
        this.schoolClassName = sc.getClass1();
        this.id = MySQLPersistenceId.createPersistentId(sc);
    }

    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param classId the id to set
     */
    public void setId(PersistenceId classId) {
        this.id = classId;
    }

    /**
     * @return the schoolClassName
     */
    public String getSchoolClassName() {
        return schoolClassName;
    }

    /**
     * @param schoolClassName the schoolClassName to set
     */
    public void setSchoolClassName(String schoolClassName) {
        this.schoolClassName = schoolClassName;
    }

    
}

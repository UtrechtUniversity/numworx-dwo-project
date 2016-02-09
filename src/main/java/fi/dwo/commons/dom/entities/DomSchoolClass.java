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
public class DomSchoolClass implements Cloneable {
    private PersistenceId id;
    private String schoolClassName;
    private Boolean hasRegKey;


    public DomSchoolClass(){
        
    }
    
    public DomSchoolClass(DomSchoolClass sc){
        DomSchoolClass clone = (DomSchoolClass) sc.clone();
        this.id = clone.id;
        this.hasRegKey = clone.getHasRegKey();
        this.schoolClassName = clone.getSchoolClassName();
    }
    
    @Override
    public Object clone() {
        DomSchoolClass sc = new DomSchoolClass();
        sc.id = (PersistenceId) this.id.clone();
        sc.schoolClassName=this.getSchoolClassName(); //strings are final
        sc.hasRegKey = this.getHasRegKey().equals(true); //ensuring cloned stuff
        return sc;
    }
    
    public DomSchoolClass(PersistentSchoolClass sc) {
        this.schoolClassName = sc.getClass1();
        this.id = MySQLPersistenceId.createPersistentId(sc);
        this.hasRegKey=(sc.getRegistrationKey()!=null);
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

    /**
     * @return the hasRegKey
     */
    public Boolean getHasRegKey() {
        return hasRegKey;
    }

    /**
     * @param hasRegKey the hasRegKey to set
     */
    public void setHasRegKey(Boolean hasRegKey) {
        this.hasRegKey = hasRegKey;
    }

    void clearSettings() {
        id = null;
        schoolClassName = "";
        hasRegKey= new Boolean (false);
    }
    
}

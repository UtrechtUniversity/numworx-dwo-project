/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomHasRole {
    private PersistenceId id;
    private DomSchoolClass schoolClass;
    private PersistenceId schoolGroupId;
    private PersistenceId userId;

    DomHasRole(){
        
    }
    
    public DomHasRole(PersistentHasRole s){
        this.id = MySQLPersistenceId.createPersistentId(s);
        this.schoolClass=new DomSchoolClass(s.getSchoolClass());
    }
    
    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param schoolId the id to set
     */
    public void setId(PersistenceId schoolId) {
        this.id = schoolId;
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
     * @return the schoolGroupId
     */
    public PersistenceId getSchoolGroupId() {
        return schoolGroupId;
    }

    /**
     * @param schoolGroupId the schoolGroupId to set
     */
    public void setSchoolGroupId(PersistenceId schoolGroupId) {
        this.schoolGroupId = schoolGroupId;
    }

    /**
     * @return the userId
     */
    public PersistenceId getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(PersistenceId userId) {
        this.userId = userId;
    }
}

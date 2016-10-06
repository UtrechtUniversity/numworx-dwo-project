/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomHasRole {
    private PersistenceId id;
//    private DomSchoolClass schoolClass;
    private PersistenceId schoolGroupId;
    private PersistenceId userId;
    private String rights = "_";

    public DomHasRole(){
        
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

//    /**
//     * @return the schoolClass
//     */
//    public DomSchoolClass getSchoolClass() {
//        return schoolClass;
//    }
//
//    /**
//     * @param schoolClass the schoolClass to set
//     */
//    public void setSchoolClass(DomSchoolClass schoolClass) {
//        this.schoolClass = schoolClass;
//    }

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
    

    /**
     * @return the rights
     */
    public String getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public void setRights(String rights) {
        this.rights = rights;
    }    
}

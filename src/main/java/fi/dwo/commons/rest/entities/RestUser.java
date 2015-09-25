/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestUser {
    private PersistenceId id;
    private String usercode;
    private String givenName;
    private String familyName;
    private String familyNamePrefix;


    public RestUser(){
        
    }
        
    public RestUser(PersistentUser u) {
        this.givenName = u.getFirstname();
        this.familyNamePrefix = u.getMiddlename();
        this.familyName = u.getLastname();
        this.id = MySQLPersistenceId.createPersistentId(u);
        this.usercode = u.getUsername();
    }

    /**
     * The persistence id of the {@Link PersistentUser}.
     * 
     * @return the classId
     */
    public PersistenceId getId() {
        return id;
    }


    /**
     * The persistence id of the {@Link PersistentUser}.
     * 
     * @param id the id to set
     */
    public void setId(PersistenceId id) {
        this.id = id;
    }

    /**
     * @return the givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * @return the familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @param familyName the familyName to set
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * @return the familyNamePrefix
     */
    public String getFamilyNamePrefix() {
        return familyNamePrefix;
    }

    /**
     * @param familyNamePrefix the familyNamePrefix to set
     */
    public void setFamilyNamePrefix(String familyNamePrefix) {
        this.familyNamePrefix = familyNamePrefix;
    }

    /**
     * @return the usercode
     */
    public String getUsercode() {
        return usercode;
    }

    /**
     * @param usercode the usercode to set
     */
    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

}

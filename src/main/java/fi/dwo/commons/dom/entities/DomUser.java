/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomUser {
    private PersistenceId id;
    private String userName;
    private String givenName;
    private String familyName;
    private String insertion;
    private Boolean singleSchool;

    public DomUser(){
        
    }

    public DomUser(DomUser user){
        setId(user.getId());
        setUserName(user.getUserName());
        setGivenName(user.getGivenName());
        setFamilyName(user.getFamilyName());
        setInsertion(user.getInsertion());
        setSingleSchool(user.getSingleSchool());
    }
        
    public DomUser(PersistentUser user) {
        if(user.getId()!=null){
            setId(MySQLPersistenceId.createPersistenceId(user.getId().intValue(), PersistenceClassType.PersistentUser));
        }else{
            setId(null);
        }
        setUserName(user.getUsername());
        setGivenName(user.getGivenName());
        setFamilyName(user.getLastname());
        setInsertion(user.getInsertion());
        setSingleSchool(user.isSingleSchoolAccount());
    }

    public void clearSettings(){
        id = null;
        userName = "";
        givenName = "";
        familyName = "";
        insertion = "";
        singleSchool = new Boolean(true);
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
     * @return the insertion
     */
    public String getInsertion() {
        return insertion;
    }

    /**
     * @param familyNamePrefix the insertion to set
     */
    public void setInsertion(String familyNamePrefix) {
        this.insertion = familyNamePrefix;
    }

    /**
     * @return the usercode
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param usercode the usercode to set
     */
    public void setUserName(String usercode) {
        this.userName = usercode;
    }

    /**
     * @return the singleSchool
     */
    public Boolean getSingleSchool() {
        return this.singleSchool;
    }

    /**
     * @param singleSchool the singleSchool to set
     */
    public void setSingleSchool(Boolean singleSchool) {
        this.singleSchool = singleSchool;
    }

    public String getUniqueDisplayName() {
        StringBuilder result = new StringBuilder();
        result.append(this.userName);
        result.append(" - ");
        result.append(this.givenName);
        result.append(" ");
        result.append(this.insertion);
        result.append(" ");
        result.append(this.familyName);
        
        return result.toString();
    }

}

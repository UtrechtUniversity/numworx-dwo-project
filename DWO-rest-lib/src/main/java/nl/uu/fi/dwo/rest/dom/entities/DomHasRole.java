/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomHasRole extends DomId {
    private PersistenceId schoolGroupId;
    private PersistenceId userId;
    private String rights = "_";
    private Date lastLogin;

    public DomHasRole(){
        
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
	/**
	 * @return the lastLogin
	 */
	public Date getLastLogin() {
		return lastLogin;
	}
	/**
	 * @param lastLogin the lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}    
}

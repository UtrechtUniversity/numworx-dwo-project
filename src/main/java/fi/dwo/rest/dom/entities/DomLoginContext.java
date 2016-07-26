/**
 * Copyrighted Nov 24, 2015
 */
package fi.dwo.rest.dom.entities;

import fi.dwo.rest.persistence.PersistenceId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transfers login context information of a Rest call. registration date, login
 * timestamp etc..
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomLoginContext {
    private PersistenceId id;
    private PersistenceId userId;
    private Long registerTimeStamp;
    private Long lastLoginTimeStamp;

    /**
     * @return the lastLoginTimeStamp
     */
    public Long getLastLoginTimeStamp() {
        return lastLoginTimeStamp;
    }

    /**
     * @param lastLoginTimeStamp the lastLoginTimeStamp to set
     */
    public void setLastLoginTimeStamp(Long lastLoginTimeStamp) {
        this.lastLoginTimeStamp = lastLoginTimeStamp;
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
     * @return the registerTimeStamp
     */
    public Long getRegisterTimeStamp() {
        return registerTimeStamp;
    }

    /**
     * @param registerTimeStamp the registerTimeStamp to set
     */
    public void setRegisterTimeStamp(Long registerTimeStamp) {
        this.registerTimeStamp = registerTimeStamp;
    }
    
}

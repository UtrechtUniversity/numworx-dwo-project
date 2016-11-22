/**
 * Copyrighted Jul 25, 2016
 */
package fi.dwo.commons.persistence.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Login context data. Store time of registration and login in a UTC timestamp.
 * This replaces the more imprecise dates in the tbluser allowing session
 * tracking and time-zone aware translation. Splitting it into a separate table
 * allows a better security and IO-performance.
 *
 * @author Gert van der Plas
 */
@Entity
@Table(name = "tbllogincontext", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userID"})})
@NamedQueries({
    @NamedQuery(name = "PersistentLoginContext.findAll", query = "SELECT p FROM PersistentLoginContext p"),
    @NamedQuery(name = "PersistentLoginContext.findByUserID", query = "SELECT p FROM PersistentLoginContext p WHERE p.userID = :userID"),
    @NamedQuery(name = "PersistentLoginContext.findByRegisterTimeStamp", query = "SELECT p FROM PersistentLoginContext p WHERE p.registerTimeStamp = :registerTimeStamp"),
    @NamedQuery(name = "PersistentLoginContext.findByLoginTimeStamp", query = "SELECT p FROM PersistentLoginContext p WHERE p.lastLoginTimeStamp = :lastLoginTimeStamp")})
public class PersistentLoginContext implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "loginID", nullable = false)
    private Long id;
    @Basic(optional = false)
    @Column(name = "userID", nullable = false)
    private Long userID;
    @Basic(optional = false)
    @Column(name = "registerTimeStamp", nullable = true)
//    @Temporal(TemporalType.DATE)
    private Long registerTimeStamp;
    @Basic(optional = false)
    @Column(name = "lastLoginTimeStamp", nullable = true)
//    @Temporal(TemporalType.DATE)
    private Long lastLoginTimeStamp;
//CREATE TABLE `tbllogincontext` (
//  `loginid` int(11) NOT NULL AUTO_INCREMENT,
//  `userID` int(11) NOT NULL,
//  `registerTimeStamp` bigint(20) DEFAULT NULL,
//  `lastLoginTimeStamp` bigint(20) DEFAULT NULL,
//  PRIMARY KEY (`loginID`),
//  UNIQUE KEY `AK_ID_LOGIN_USER`  (`userID`),
//  `AK_ID_LOGIN_TIMESTAMP` (`lastLoginTimeStamp`)
//) ENGINE=InnoDB DEFAULT CHARSET=latin1

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the userID
     */
    public Long getUserId() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserId(Long userID) {
        this.userID = userID;
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

    /**
     * @return the lastLogin
     */
    public Long getLastLogin() {
        return lastLoginTimeStamp;
    }

    /**
     * @param lastLogin the lastLogin to set
     */
    public void setLastLogin(Long lastLogin) {
        this.lastLoginTimeStamp = lastLogin;
    }

    public DomLoginContext buildDomLoginContext() {
        DomLoginContext loginContext = new DomLoginContext();
        fillDomLoginContext(loginContext);
        return loginContext;
    }

    private void fillDomLoginContext(DomLoginContext loginContext) {
        if (this.id != null) {
            loginContext.setId(buildPersistenceId());
        }
        loginContext.setLastLoginTimeStamp(lastLoginTimeStamp);
        loginContext.setRegisterTimeStamp(registerTimeStamp);
        loginContext.setUserId(PersistentUser.buildPersistenceId(userID));
    }

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(id);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aId) {
        PersistenceId tmpId = new PersistenceId();
        tmpId.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentLoginContext.name(), tmpId));
        return tmpId;
    }
}

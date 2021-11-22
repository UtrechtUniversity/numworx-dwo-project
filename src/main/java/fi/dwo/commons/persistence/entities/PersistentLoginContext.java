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
import javax.persistence.Version;

import fi.dwo.commons.util.DatatypeConverter;
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
    @Column(name = "schoolGroupID", nullable = false)
    private Long schoolGroupID;
    @Basic(optional = false)
    @Column(name = "courseID", nullable = true)
    protected Long courseID;
    @Basic(optional = false)
    @Column(name = "secretKey", nullable = true)
    protected byte[] secretKey;
    @Basic(optional = false)
    @Column(name = "registerTimeStamp", nullable = true)
    private Long registerTimeStamp;
    @Basic(optional = false)
    @Column(name = "lastLoginTimeStamp", nullable = true)
    private Long lastLoginTimeStamp;
    // since 1.5.4
    @Column(name = "nonce", nullable = true)
    protected byte[] nonce;
    @Version
    @Column(name = "optlock", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Long version;

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
        if (this.schoolGroupID != null) {
            loginContext.setSchoolGroupId(PersistentSchoolGroup.buildPersistenceId(schoolGroupID));
            PersistentHasRolePK pk = new PersistentHasRolePK(userID, schoolGroupID);
            loginContext.setHasRoleId(PersistentHasRole.buildPersistenceId(pk));
        }
        loginContext.setLastLoginTimeStamp(lastLoginTimeStamp);
        loginContext.setRegisterTimeStamp(registerTimeStamp);
        loginContext.setUserId(PersistentUser.buildPersistenceId(userID));
        
        //Use hex-encoding for compatibility with software TOTP-generators
        String encoded = secretKey==null ? null : DatatypeConverter.printHexBinary(secretKey);
        //decode with         byte result[] = DatatypeConverter.parseHexBinary(encoded);
        loginContext.setSecretKey(encoded);    
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
                PersistenceClassType.PersistentLoginContext.name(), aId));
        return tmpId;
    }

    /**
     * @return the schoolGroupID
     */
    public Long getSchoolGroupId() {
        return schoolGroupID;
    }

    /**
     * @param schoolGroupID the schoolGroupID to set
     */
    public void setSchoolGroupId(Long schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    /**
     * @return the courseID
     */
    public Long getCourseID() {
        return courseID;
    }

    /**
     * @param courseID the courseID to set
     */
    public void setCourseID(Long courseID) {
        this.courseID = courseID;
    }

    /**
     * @return the secretKey
     */
    public byte[] getSecretKey() {
        return secretKey;
    }

    /**
     * @param secretKey the secretKey to set
     */
    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

    public byte[] getNonce() {
      return nonce;
    }

    public void setNonce(byte[] nonce) {
      this.nonce = nonce;
    }
    
}

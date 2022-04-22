package fi.dwo.commons.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import fi.dwo.commons.persistence.LogType;
import javax.persistence.EmbeddedId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import org.json.simple.JSONObject;

/**
 * PersistentLogData contains data for statistical analysis.
 *
 * @author Gert van der Plas
 */
@Entity
@Table(name = "tbllogdata", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LoginData.findByTimeStampRangeAndUsername", query = "SELECT p FROM PersistentLoginData p WHERE p.userId = :userId and p.utcTimeStamp > :fromTimestamp and p.utcTimeStamp < :toTimestamp "),
    @NamedQuery(name = "LoginData.findByTimeStampRange", query = "SELECT p FROM PersistentLoginData p WHERE p.utcTimeStamp > :fromTimestamp and p.utcTimeStamp < :toTimestamp ")})
public class PersistentLogData {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private PersistentLogDataPK PersistentLoginDataPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role", nullable = false)
    private RoleType role;
    @Basic(optional = false)
    @NotNull
    @Column(name = "logType", nullable = false, length = 50)
    private LogType logType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "logLevel", nullable = false)
    private int logLevel;
    @Basic(optional = false)
    @NotNull
    @Column(name = "jsonLogData", nullable = false, length = 512)
    private JSONObject jsonLogData;
    @Basic(optional = false)
//    @NotNull
//    @Lob
//    @Column(name = "data", columnDefinition="MEDIUMTEXT", nullable = false, length = 65535) //for 64MB textblobs
//    private String data;
//    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "userip", nullable = false, length = 128)
    private String userIP;
    

    /**
     * @return the PersistentLogDataPK
     */
    public PersistentLogDataPK getPersistentLoginDataPK() {
        return PersistentLoginDataPK;
    }

    /**
     * @param PersistentLoginDataPK the PersistentLogDataPK to set
     */
    public void setPersistentLoginDataPK(PersistentLogDataPK PersistentLoginDataPK) {
        this.PersistentLoginDataPK = PersistentLoginDataPK;
    }

    /**
     * @return the role
     */
    public RoleType getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(RoleType role) {
        this.role = role;
    }

    /**
     * @return the message
     */
    public LogType getMessage() {
        return logType;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(LogType logType) {
        this.logType = logType;
    }

    /**
     * @return the userIP
     */
    public String getUserIP() {
        return userIP;
    }

    /**
     * @param userIP the userIP to set
     */
    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    /**
     * @return the logLevel
     */
    public int getLogLevel() {
        return logLevel;
    }

    /**
     * @param logLevel the logLevel to set
     */
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * @return the logType
     */
    public LogType getLogType() {
        return logType;
    }

    /**
     * @param logType the logType to set
     */
    public void setLogType(LogType logType) {
        this.logType = logType;
    }
}

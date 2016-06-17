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

/**
 * PersistentLogData contains data for statistical analysis.
 *
 * @author Gert van der Plas
 */
@Entity
@Table(name = "tblLogData", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LoginData.findByTimeStampRangeAndUsername", query = "SELECT p FROM PersistentLoginData p WHERE p.username = username and p.utcTimeStamp > :fromTimestamp and p.utcTimeStamp < :toTimestamp "),
    @NamedQuery(name = "LoginData.findByTimeStampRange", query = "SELECT p FROM PersistentLoginData p WHERE p.utcTimeStamp > :fromTimestamp and p.utcTimeStamp < :toTimestamp ")})
public class PersistentLogData {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private PersistentLoginDataPK PersistentLoginDataPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role", nullable = false)
    private String role;
    @Basic(optional = false)
    @NotNull
    @Column(name = "logType", nullable = false, length = 50)
    private LogType logType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "message", nullable = false, length = 512)
    private String message;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "userip", nullable = false, length = 128)
    private String userIP;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "loglevel", nullable = false, length = 24)
    private String logLevel;
    

    /**
     * @return the PersistentLoginDataPK
     */
    public PersistentLoginDataPK getPersistentLoginDataPK() {
        return PersistentLoginDataPK;
    }

    /**
     * @param PersistentLoginDataPK the PersistentLoginDataPK to set
     */
    public void setPersistentLoginDataPK(PersistentLoginDataPK PersistentLoginDataPK) {
        this.PersistentLoginDataPK = PersistentLoginDataPK;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
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
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * @param logLevel the logLevel to set
     */
    public void setLogLevel(String logLevel) {
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

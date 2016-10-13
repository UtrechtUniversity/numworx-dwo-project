package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author Gert van der Plas
 */
public class DomStudentScoContextFull extends DomStudentScoContext{
    private String totalTime;
    private String sessionTime;
    private Date createDate;
    private java.sql.Time createTime;
    private String completionStatus;
    private String location;    

    /**
     * @return the totalTime
     */
    public String getTotalTime() {
        return totalTime;
    }

    /**
     * @param totalTime the totalTime to set
     */
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * @return the sessionTime
     */
    public String getSessionTime() {
        return sessionTime;
    }

    /**
     * @param sessionTime the sessionTime to set
     */
    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the createTime
     */
    public java.sql.Time getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(java.sql.Time createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the completionStatus
     */
    public String getCompletionStatus() {
        return completionStatus;
    }

    /**
     * @param completionStatus the completionStatus to set
     */
    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }
    
}

package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;

/**
 * 
 * @author Gert van der Plas
 */
public class DomStudentScoContextFull extends DomStudentScoContext{
    private String sessionTime;
    private Date createDate;
    private java.sql.Time createTime;
    private String location;    

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

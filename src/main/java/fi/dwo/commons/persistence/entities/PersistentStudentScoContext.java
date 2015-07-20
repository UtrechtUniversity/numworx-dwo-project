/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * StudentScoContext manager. Known issues are that is does not provide a way
 * to access data directly on a HasRolePK. Only via the component indices of the 
 * HasRolePK.
 * 
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblstudentscocontext", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentScoContext.findAll", query = "SELECT p FROM PersistentStudentScoContext p"),
    @NamedQuery(name = "PersistentStudentScoContext.findByTotalTime", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.totalTime = :totalTime"),
    @NamedQuery(name = "PersistentStudentScoContext.findBySessionTime", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.sessionTime = :sessionTime"),
    @NamedQuery(name = "PersistentStudentScoContext.findByStudentSco", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.studentSco = :studentSco"),
    @NamedQuery(name = "PersistentStudentScoContext.findByScoID", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.scoID = :scoID"),
    @NamedQuery(name = "PersistentStudentScoContext.findByHasRolePK", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.userID = :userID and p.schoolGroupID= :schoolGroupID"),
    @NamedQuery(name = "PersistentStudentScoContext.findByCreateDate", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.createDate = :createDate"),
    @NamedQuery(name = "PersistentStudentScoContext.findByScore", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.score = :score")})
public class PersistentStudentScoContext implements Serializable {
    private static final long serialVersionUID = 1L;
    @Size(max = 100)
    @Column(name = "total_time", length = 100)
    private String totalTime;
    @Size(max = 100)
    @Column(name = "session_time", length = 100)
    private String sessionTime;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "studentSco", nullable = false)
    private Integer studentSco;
    @Basic(optional = false)
    @NotNull
    @Column(name = "scoID", nullable = false)
    private int scoID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "userID", nullable = false)
    private int userID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolGroupID", nullable = false)
    private int schoolGroupID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "score", nullable = false)
    private float score;

    public PersistentStudentScoContext() {
    }

    public PersistentStudentScoContext(Integer studentSco) {
        this.studentSco = studentSco;
    }

    public PersistentStudentScoContext(Integer studentSco, int scoID, int userID, Date createDate, float score) {
        this.studentSco = studentSco;
        this.scoID = scoID;
        this.userID = userID;
        this.createDate = createDate;
        this.score = score;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    public Integer getStudentSco() {
        return studentSco;
    }

    public void setStudentSco(Integer studentSco) {
        this.studentSco = studentSco;
    }

    public int getScoID() {
        return scoID;
    }

    public void setScoID(int scoID) {
        this.scoID = scoID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentSco != null ? studentSco.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentStudentScoContext)) {
            return false;
        }
        PersistentStudentScoContext other = (PersistentStudentScoContext) object;
        if ((this.studentSco == null && other.studentSco != null) || (this.studentSco != null && !this.studentSco.equals(other.studentSco))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentStudentScoContext[ studentSco=" + studentSco + " ]";
    }

    /**
     * @return the schoolGroupID
     */
    public int getSchoolGroupID() {
        return schoolGroupID;
    }

    /**
     * @param schoolGroupID the schoolGroupID to set
     */
    public void setSchoolGroupID(int schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }
    
}

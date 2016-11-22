/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author G.A.J. van der Plas
 */
@Embeddable
public class PersistentStudentOfClassPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "userID", nullable = false)
    private Long userID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolGroupID", nullable = false)
    private Long schoolGroupID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "classID", nullable = false)
    private Long classID;

    public PersistentStudentOfClassPK() {
    }

    public PersistentStudentOfClassPK(Long userID, Long classID, Long schoolGroupID) {
        this.userID = userID;
        this.classID = classID;
        this.schoolGroupID = schoolGroupID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getClassID() {
        return classID;
    }

    public void setClassID(Long classID) {
        this.classID = classID;
    }

    public Long getSchoolGroupID() {
        return schoolGroupID;
    }

    public void setSchoolGroupID(Long schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) (userID % (long) (Integer.MAX_VALUE));
        hash += (int) (classID % (long) (Integer.MAX_VALUE));
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentStudentOfClassPK)) {
            return false;
        }
        PersistentStudentOfClassPK other = (PersistentStudentOfClassPK) object;
        if ((long) this.userID != (long) other.userID) {
            return false;
        }
        if ((long) this.classID != (long) other.classID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentStudentOfClassPK[ userID=" + userID + ", classID=" + classID + " ]";
    }

}

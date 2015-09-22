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
    private int userID;
    @Basic(optional = false)
    @NotNull    
    @Column(name = "schoolGroupID", nullable = false)
    private int schoolGroupID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "classID", nullable = false)
    private int classID;
    
    public long getId() {
        long id = classID;
        id = id << 32;
        id = id & userID;
        return (id);
    }

    public PersistentStudentOfClassPK() {
    }

    public PersistentStudentOfClassPK(int userID, int classID, int schoolGroupID) {
        this.userID = userID;
        this.classID = classID;
        this.schoolGroupID = schoolGroupID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public int getSchoolGroupID() {
        return schoolGroupID;
    }


    public void setSchoolGroupID(int schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userID;
        hash += (int) classID;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentStudentOfClassPK)) {
            return false;
        }
        PersistentStudentOfClassPK other = (PersistentStudentOfClassPK) object;
        if (this.userID != other.userID) {
            return false;
        }
        if (this.classID != other.classID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentStudentOfClassPK[ userID=" + userID + ", classID=" + classID + " ]";
    }

}

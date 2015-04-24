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

/**
 * Primary key for PersistentHasRole entity.
 * 
 * {@see PersistentHasRole}
 * 
 * @author plas0006
 */
@Embeddable
public class PersistentHasRolePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "userID", nullable = false)
    private int userID;
    @Basic(optional = false)
    @Column(name = "schoolGroupID", nullable = false)
    private int schoolGroupID;

   public  long getId(){
        long id = schoolGroupID;
        id = id <<32;
        id = id & userID;
        return (id);
    }
   
    public PersistentHasRolePK() {
    }

    public PersistentHasRolePK(int userID, int schoolGroupID) {
        this.userID = userID;
        this.schoolGroupID = schoolGroupID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
        hash += (int) schoolGroupID;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentHasRolePK)) {
            return false;
        }
        PersistentHasRolePK other = (PersistentHasRolePK) object;
        if (this.userID != other.userID) {
            return false;
        }
        if (this.schoolGroupID != other.schoolGroupID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentHasRolePK[ userID=" + userID + ", schoolGroupID=" + schoolGroupID + " ]";
    }
    
}

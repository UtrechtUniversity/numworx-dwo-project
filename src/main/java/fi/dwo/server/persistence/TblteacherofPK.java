/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
@Embeddable
public class TblteacherofPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "userID", nullable = false)
    private int userID;
    @Basic(optional = false)
    @Column(name = "classID", nullable = false)
    private int classID;

    public TblteacherofPK() {
    }

    public TblteacherofPK(int userID, int classID) {
        this.userID = userID;
        this.classID = classID;
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
        if (!(object instanceof TblteacherofPK)) {
            return false;
        }
        TblteacherofPK other = (TblteacherofPK) object;
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
        return "fi.dwo.server.persistence.TblteacherofPK[ userID=" + userID + ", classID=" + classID + " ]";
    }
    
}
